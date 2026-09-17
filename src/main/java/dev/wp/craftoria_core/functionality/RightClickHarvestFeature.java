package dev.wp.craftoria_core.functionality;

import dev.wp.craftoria_core.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;

public final class RightClickHarvestFeature {
    private RightClickHarvestFeature() {}

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!ServerConfig.rightClickHarvest) return;

        Player player = event.getEntity();
        if (player instanceof FakePlayer) return;
        if (event.getHand() != InteractionHand.MAIN_HAND || player.isSpectator() || player.isShiftKeyDown()) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        if (!(block instanceof CropBlock) && !(block instanceof CocoaBlock) && !(block instanceof NetherWartBlock)) return;

        if (harvest(level, pos, state, player, event.getItemStack())) event.setCanceled(true);
    }

    private static boolean harvest(ServerLevel level, BlockPos pos, BlockState state, Player player, ItemStack tool) {
        if (!isMature(state)) return false;

        List<ItemStack> drops = Block.getDrops(state, level, pos, level.getBlockEntity(pos), player, tool);
        ItemStack seed = state.getBlock().asItem().getDefaultInstance();
        boolean removedASeed = false;
        for (ItemStack drop : drops) {
            if (!removedASeed && !seed.isEmpty() && ItemStack.isSameItem(drop, seed)) {
                removedASeed = true;
                drop.shrink(1);
            }
            if (drop.isEmpty()) continue;
            Block.popResource(level, pos, drop);
        }

        level.setBlock(pos, getAgeZero(state), Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE);
        player.swing(InteractionHand.MAIN_HAND, true);
        return true;
    }

    private static boolean isMature(BlockState state) {
        Block block = state.getBlock();
        return switch (block) {
            case CropBlock crop -> crop.isMaxAge(state);
            case CocoaBlock cocoaBlock -> state.getValue(CocoaBlock.AGE) >= CocoaBlock.MAX_AGE;
            case NetherWartBlock netherWartBlock -> state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
            default -> false;
        };
    }

    private static BlockState getAgeZero(BlockState state) {
        Block block = state.getBlock();
        return switch (block) {
            case CropBlock crop -> crop.getStateForAge(0);
            case CocoaBlock cocoaBlock -> state.setValue(CocoaBlock.AGE, 0);
            case NetherWartBlock netherWartBlock -> state.setValue(NetherWartBlock.AGE, 0);
            default -> state;
        };
    }
}
