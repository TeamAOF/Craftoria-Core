package dev.wp.craftoria_core.mixin.cataclysm;

import com.github.L_Ender.cataclysm.blockentities.Cursed_tombstone_Entity;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.WeakHashMap;

@Restriction(
        require = {
                @Condition("cataclysm")
        }
)
@Mixin(Cursed_tombstone_Entity.class)
public class CursedTombstoneEntityMixin {
    @Unique
    private static final Map<Cursed_tombstone_Entity, Boolean> craftoriaCore$isBeingSummonedMap = new WeakHashMap<>();

    @Inject(
            method = "commonTick",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/entity/EntityType;create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;"),
            cancellable = true)
    private static void commonTickInject(Level level, BlockPos pos, BlockState blockState, Cursed_tombstone_Entity entity, CallbackInfo ci) {
        if (Boolean.TRUE.equals(craftoriaCore$isBeingSummonedMap.get(entity))) ci.cancel();
        craftoriaCore$isBeingSummonedMap.put(entity, true);
    }

    @Inject(
            method = "commonTick",
            at = @At(
                    value = "FIELD",
                    target = "Lcom/github/L_Ender/cataclysm/blockentities/Cursed_tombstone_Entity;tickCount:I",
                    ordinal = 6))
    private static void commonTickReturn(Level level, BlockPos pos, BlockState blockState, Cursed_tombstone_Entity entity, CallbackInfo ci) {
        craftoriaCore$isBeingSummonedMap.remove(entity);
    }
}