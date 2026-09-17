package dev.wp.craftoria_core.mixin.styledchat;

import dev.wp.craftoria_core.funny.FunnyFeature;
import eu.pb4.styledchat.StyledChatUtils;
import eu.pb4.styledchat.ducks.ExtSignedMessage;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = StyledChatUtils.class, remap = false)
public class StyledChatUtilsMixin {
    @Inject(method = "modifyForSending", at = @At("HEAD"))
    private static void craftoria$setFunnyBaseInput(
            PlayerChatMessage message,
            CommandSourceStack source,
            ResourceKey<ChatType> type,
            CallbackInfo ci
    ) {
        if (!(source.getEntity() instanceof ServerPlayer player)
                || !type.location().getPath().equals("chat")) return;

        FunnyFeature.Result transformed = FunnyFeature.transformForPlayer(player, message.signedContent());
        if (transformed != null) {
            ExtSignedMessage.setArg(message, "base_input", transformed.toComponent(Style.EMPTY));
        }
    }
}
