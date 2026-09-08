package dev.wp.craftoria_core.mixin.remi;

import com.evandev.remi.feature.stackgroup.EmiGroupStack;
import com.evandev.remi.feature.stackgroup.GroupedEmiStack;
import com.evandev.remi.feature.stackgroup.StackGroupManager;
import com.evandev.remi.feature.stackgroup.data.StackGroup;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(value = StackGroupManager.class, remap = false)
public class StackGroupManagerMatchMixin {
    @Unique
    private static final ThreadLocal<Map<ResourceLocation, List<EmiStack>>> CRAFTORIA$BUILD_STATE = new ThreadLocal<>();

    @Inject(method = "buildGroupedEmiStacksAndStackGroupToContents", at = @At("HEAD"))
    private static void craftoria$beginBuild(List<EmiStack> source, CallbackInfo ci) {
        CRAFTORIA$BUILD_STATE.set(new HashMap<>());
    }

    @Inject(method = "buildGroupedEmiStacksAndStackGroupToContents", at = @At("RETURN"))
    private static void craftoria$endBuild(List<EmiStack> source, CallbackInfo ci) {
        CRAFTORIA$BUILD_STATE.remove();
    }

    @WrapOperation(
            method = "buildGroupedEmiStacksAndStackGroupToContents",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/evandev/remi/feature/stackgroup/StackGroupManager;registerMatch(Lcom/evandev/remi/feature/stackgroup/data/StackGroup;Ldev/emi/emi/api/stack/EmiStack;Ljava/util/Map;)V"
            )
    )
    private static void craftoria$registerMatch(
            StackGroup group,
            EmiStack stack,
            Map<StackGroup, EmiGroupStack> groupStacksMap,
            Operation<Void> original
    ) {
        Map<ResourceLocation, List<EmiStack>> groupedById = CRAFTORIA$BUILD_STATE.get();
        if (groupedById == null) {
            original.call(group, stack, groupStacksMap);
            return;
        }

        EmiGroupStack groupStack = groupStacksMap.get(group);
        if (groupStack == null) {
            return;
        }

        GroupedEmiStack<EmiStack> groupedStack = new GroupedEmiStack<>(stack, group);
        boolean added = groupStack.append(groupedStack);
        if (!added || !group.isEnabled) {
            return;
        }

        List<EmiStack> sameId = groupedById.computeIfAbsent(stack.getId(), ignored -> new ArrayList<>());
        boolean alreadyGrouped = false;
        for (EmiStack existing : sameId) {
            if (existing.isEqual(stack, Comparison.compareComponents())) {
                alreadyGrouped = true;
                break;
            }
        }

        if (!alreadyGrouped) {
            sameId.add(stack);
            StackGroupManager.groupedEmiStacks.add(stack);
        }

        StackGroupManager.getItemToGroupedStacks()
                .computeIfAbsent(stack.getId(), ignored -> new ArrayList<>())
                .add(groupedStack);
        StackGroupManager.stackToGroupedStacks
                .computeIfAbsent(stack, ignored -> new ArrayList<>())
                .add(groupedStack);
    }
}
