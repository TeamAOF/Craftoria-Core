package dev.wp.craftoria_core.mixin.sdlink;

import com.hypherionmc.craterlib.api.events.server.CraterServerChatEvent;
import com.hypherionmc.sdlink.server.ServerEvents;
import dev.wp.craftoria_core.funny.FunnyFeature;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import shadow.kyori.adventure.text.Component;

@Mixin(value = ServerEvents.class, remap = false)
public class SdlinkServerEventsMixin {
    @Inject(method = "onServerChatEvent", at = @At("HEAD"))
    private void craftoria$transformRelayMessage(CraterServerChatEvent event, CallbackInfo ci) {
        if (!event.getPlayer().isServerPlayer()) return;

        ServerPlayer player = event.getPlayer().toMojangServerPlayer();
        String transformed = FunnyFeature.transformForPlayer(player, event.getMessage());
        if (!transformed.equals(event.getMessage())) {
            event.setComponent(Component.text(transformed));
        }
    }
}
