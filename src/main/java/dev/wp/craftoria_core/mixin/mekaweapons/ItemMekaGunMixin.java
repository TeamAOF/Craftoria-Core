package dev.wp.craftoria_core.mixin.mekaweapons;

import mekanism.client.sound.SoundHandler;
import mekanism.common.registration.impl.SoundEventRegistryObject;
import meranha.mekaweapons.items.ItemMekaGun;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemMekaGun.class)
public class ItemMekaGunMixin {
    @Redirect(method = "use", at = @At(value = "INVOKE", target = "Lmekanism/client/sound/SoundHandler;playSound(Lmekanism/common/registration/impl/SoundEventRegistryObject;)V"))
    private void craftoriaCore$clientOnlySound(SoundEventRegistryObject<?> soundEventRO, Level level, Player player, InteractionHand usedHand) {
        if (level.isClientSide()) SoundHandler.playSound(soundEventRO);
    }
}
