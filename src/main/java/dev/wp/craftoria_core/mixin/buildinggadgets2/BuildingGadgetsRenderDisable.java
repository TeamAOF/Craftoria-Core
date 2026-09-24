package dev.wp.craftoria_core.mixin.buildinggadgets2;

import codechicken.enderstorage.tile.TileFrequencyOwner;
import com.direwolf20.buildinggadgets2.client.renderer.VBORenderer;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.vertex.PoseStack;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

@Restriction(require = @Condition("buildinggadgets2"))
@Mixin(VBORenderer.class)
public class BuildingGadgetsRenderDisable {
    @WrapWithCondition(method="drawRender",at=@At(value="INVOKE",target="Lnet/minecraft/client/renderer/blockentity/BlockEntityRenderDispatcher;render(Lnet/minecraft/world/level/block/entity/BlockEntity;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;)V"))
    private static boolean render(BlockEntityRenderDispatcher instance, @Coerce BlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource) {
        return !(blockEntity instanceof TileFrequencyOwner);
    }
}
