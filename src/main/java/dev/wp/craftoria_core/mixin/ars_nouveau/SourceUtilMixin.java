package dev.wp.craftoria_core.mixin.ars_nouveau;

import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import dev.wp.craftoria_core.arseng.SourceContainerScanner;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(SourceUtil.class)
public abstract class SourceUtilMixin {
    @Inject(method = "canGiveSource", at = @At("RETURN"))
    private static void canGiveSource(
            BlockPos pos, Level world, int range, CallbackInfoReturnable<List<ISpecialSourceProvider>> cir) {
        SourceContainerScanner.collect(cir.getReturnValue(), pos, world, range, false);
    }

    @Inject(method = "canTakeSource", at = @At("RETURN"))
    private static void canTakeSource(
            BlockPos pos, Level world, int range, CallbackInfoReturnable<List<ISpecialSourceProvider>> cir) {
        SourceContainerScanner.collect(cir.getReturnValue(), pos, world, range, true);
    }
}
