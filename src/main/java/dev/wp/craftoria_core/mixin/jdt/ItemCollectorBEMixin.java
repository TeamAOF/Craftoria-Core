package dev.wp.craftoria_core.mixin.jdt;

import com.direwolf20.justdirethings.common.blockentities.ItemCollectorBE;
import dev.wp.craftoria_core.util.jdt.ItemCollectorAccess;
import dev.wp.craftoria_core.util.jdt.ItemCollectorPositions;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// Registration half of https://github.com/Direwolf20-MC/JustDireThings/pull/389, see LivingEntityEventsMixin
@Mixin(ItemCollectorBE.class)
public abstract class ItemCollectorBEMixin extends BlockEntity implements ItemCollectorAccess {
    protected ItemCollectorBEMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow
    private native IItemHandler getAttachedInventory();

    @Unique
    private boolean craftoriaCore$registered;

    // no onLoad override to hook, so register on the first tick instead
    @Inject(method = "tickServer", at = @At("HEAD"))
    private void craftoriaCore$registerPosition(CallbackInfo ci) {
        if (!craftoriaCore$registered && this.level != null) {
            ItemCollectorPositions.add(this.level, this.getBlockPos());
            craftoriaCore$registered = true;
        }
    }

    @Unique
    @Override
    public IItemHandler craftoriaCore$getAttachedInventory() {
        return getAttachedInventory();
    }
}
