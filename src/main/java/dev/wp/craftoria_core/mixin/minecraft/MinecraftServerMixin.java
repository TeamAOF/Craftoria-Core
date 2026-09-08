package dev.wp.craftoria_core.mixin.minecraft;

import dev.wp.craftoria_core.util.SdlinkPlayerLimit;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @ModifyArg(
            method = "buildPlayerStatus",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/protocol/status/ServerStatus$Players;<init>(IILjava/util/List;)V"
            ),
            index = 1
    )
    private int countRegularPlayers(int original) {
        PlayerList playerList = ((MinecraftServer) (Object) this).getPlayerList();
        return SdlinkPlayerLimit.countRegularPlayers(playerList.getPlayers().stream()
                .map(ServerPlayer::getUUID)
                .toList());
    }
}
