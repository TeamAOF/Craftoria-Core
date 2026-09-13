package dev.wp.craftoria_core.mixin.remi;

import com.bawnorton.mixinsquared.TargetHandler;
import com.evandev.remi.integration.emi.StackManager;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.emi.emi.api.stack.EmiStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Mixin(targets = "dev.emi.emi.search.EmiSearch$SearchWorker", remap = false, priority = 1500)
public class RemiSearchWorkerMixin {
    @Unique
    private static final ExecutorService craftoriaCore$EXECUTOR =
            Executors.newSingleThreadExecutor(r -> {
                Thread thread = new Thread(r, "Reliable-EMI Search");
                thread.setDaemon(true);
                return thread;
            });

    @Unique
    private static final ThreadLocal<Boolean> ASYNC_APPEND = ThreadLocal.withInitial(() -> false);

    @TargetHandler(
            mixin = "com.evandev.remi.mixin.emi.EmiSearchSearchWorkerMixin",
            name = "run"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/evandev/remi/feature/stackgroup/StackGroupManager;appendStacksForMatchingGroups(Ljava/lang/String;Ljava/util/List;)V"
            )
    )
    private void asyncAppend(String query, List<EmiStack> results, Operation<Void> original) {
        ASYNC_APPEND.set(true);

        craftoriaCore$EXECUTOR.execute(() -> {
            try {
                original.call(query, results);
                StackManager.buildStacks(results);
            } finally {
                ASYNC_APPEND.remove();
            }
        });
    }

    @TargetHandler(
            mixin = "com.evandev.remi.mixin.emi.EmiSearchSearchWorkerMixin",
            name = "run"
    )
    @WrapOperation(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/evandev/remi/integration/emi/StackManager;buildStacks(Ljava/util/List;)V"
            )
    )
    private void asyncBuild(List<EmiStack> searched, Operation<Void> original) {
        if (ASYNC_APPEND.get()) return;
        craftoriaCore$EXECUTOR.execute(() -> StackManager.buildStacks(searched));
    }
}
