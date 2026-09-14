package dev.wp.craftoria_core.mixin.styledchat;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.chat.ChatDecorator;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = ServerGamePacketListenerImpl.class, priority = 1500)
public class StyledChatDecoratorCompatibility {
    @TargetHandler(
            mixin = "eu.pb4.styledchat.mixin.ServerPlayNetworkManagerMixin",
            name = "styledChat_replaceDecorator2"
    )
    @WrapMethod(method = "@MixinSquared:Handler")
    private Component craftoriaCore$restoreChatDecorator(
            ChatDecorator instance,
            ServerPlayer player,
            Component text,
            Operation<Component> original
    ) {
        if (player == null) return original.call(instance, player, text);

        Component decorated = instance.decorate(player, text);
        if (decorated == null) return null;
        return original.call(instance, player, decorated);
    }
}
