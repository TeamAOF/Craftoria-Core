package dev.wp.craftoria_core.functionality;

import dev.wp.craftoria_core.config.ServerConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FaceAttachedHorizontalDirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.WeightedPressurePlateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DoorFeature {
    private DoorFeature() {}

    private static final TagKey<Block> NO_DOUBLE_OPEN = TagKey.create(Registries.BLOCK, ResourceLocation.parse("craftoria:no_double_open"));
    private static final int DOOR_UPDATE_FLAGS = Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE;

    private static final Set<BlockPos> prevPoweredPos = new HashSet<>();
    private static final Set<BlockPos> prevButtonPos = new HashSet<>();

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!ServerConfig.doubleDoor) return;

        Player player = event.getEntity();
        if (player instanceof FakePlayer) return;
        if (event.getHand() != InteractionHand.MAIN_HAND || player.isShiftKeyDown()) return;

        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!isAllowedDoor(state) || !canOpenByHand(state)) return;

        processDoor(player, level, pos, state, null);
    }

    @SubscribeEvent
    public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        if (!ServerConfig.doubleDoor) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        BlockPos blockPos = event.getPos();
        BlockState blockState = event.getState();
        Block block = blockState.getBlock();

        boolean isPressurePlate = block instanceof BasePressurePlateBlock;
        boolean isButtonOrLever = block instanceof ButtonBlock || block instanceof LeverBlock;
        if (!isPressurePlate && !isButtonOrLever) return;

        boolean isCurrentlyPowered = block instanceof WeightedPressurePlateBlock
                ? blockState.getValue(WeightedPressurePlateBlock.POWER) > 0
                : blockState.getValue(BlockStateProperties.POWERED);

        if (isButtonOrLever) {
            if (isCurrentlyPowered) {
                if (!prevButtonPos.add(blockPos)) return;
            } else if (!prevPoweredPos.remove(blockPos)) {
                return;
            }
        } else if (!isCurrentlyPowered && !prevPoweredPos.remove(blockPos)) {
            return;
        }

        Direction facing = isButtonOrLever ? blockState.getValue(HorizontalDirectionalBlock.FACING) : null;
        AttachFace face = isButtonOrLever ? blockState.getValue(FaceAttachedHorizontalDirectionalBlock.FACE) : null;

        BlockPos doorPos = null;
        for (BlockPos aroundPos : getNearbyBlocks(blockPos, isButtonOrLever, facing, face)) {
            if (isAllowedDoor(level.getBlockState(aroundPos))) {
                doorPos = aroundPos;
                break;
            }
        }
        if (doorPos == null) return;

        if (processDoor(null, level, doorPos, level.getBlockState(doorPos), isCurrentlyPowered) && isCurrentlyPowered) {
            prevPoweredPos.add(blockPos);
        }
    }

    private static List<BlockPos> getNearbyBlocks(BlockPos pos, boolean down, @Nullable Direction facing, @Nullable AttachFace face) {
        if (face == AttachFace.WALL && facing != null) pos = pos.relative(facing.getOpposite());

        List<BlockPos> nearby = new ArrayList<>();
        for (Direction dir : Direction.values()) {
            if (dir == Direction.DOWN && !down) continue;
            nearby.add(pos.relative(dir));
        }
        return nearby;
    }

    private static boolean isAllowedDoor(BlockState state) {
        if (!(state.getBlock() instanceof DoorBlock)) return false;
        return !state.is(NO_DOUBLE_OPEN);
    }

    private static boolean canOpenByHand(BlockState state) {
        return ((DoorBlock) state.getBlock()).type().canOpenByHand();
    }

    private static boolean processDoor(@Nullable Player player, Level level, BlockPos blockPos, BlockState blockState, @Nullable Boolean isOpen) {
        if (blockState.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
            blockPos = blockPos.below();
            blockState = level.getBlockState(blockPos);
        }

        boolean targetOpen = isOpen != null ? isOpen : !blockState.getValue(DoorBlock.OPEN);
        BlockPos partnerPos = doorPartnerPos(blockPos, blockState);
        BlockState partnerState = level.getBlockState(partnerPos);
        if (!isAllowedDoor(partnerState) || partnerState.getBlock() != blockState.getBlock()
                || partnerState.getValue(DoorBlock.FACING) != blockState.getValue(DoorBlock.FACING)
                || partnerState.getValue(DoorBlock.HINGE) == blockState.getValue(DoorBlock.HINGE)) {
            return false;
        }

        level.setBlock(partnerPos, partnerState.setValue(DoorBlock.OPEN, targetOpen), DOOR_UPDATE_FLAGS);
        if (player != null) player.swing(InteractionHand.MAIN_HAND);
        return true;
    }

    /** The mirrored-hinge door position that pairs with this one to form a double door (opposite hinges, outer edges). */
    private static BlockPos doorPartnerPos(BlockPos blockPos, BlockState blockState) {
        Direction facing = blockState.getValue(DoorBlock.FACING);
        Direction towardPartner = blockState.getValue(DoorBlock.HINGE) == DoorHingeSide.LEFT ? facing.getClockWise() : facing.getCounterClockWise();
        return blockPos.relative(towardPartner);
    }
}
