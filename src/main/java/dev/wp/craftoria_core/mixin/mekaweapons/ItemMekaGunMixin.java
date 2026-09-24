package dev.wp.craftoria_core.mixin.mekaweapons;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import mekanism.client.sound.SoundHandler;
import mekanism.common.registration.impl.SoundEventRegistryObject;
import meranha.mekaweapons.items.ItemMekaGun;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Restriction(
        require = {
                @Condition(value = "mekaweapons", versionPredicates = "4.3")
        }
)
@Mixin(ItemMekaGun.class)
public class ItemMekaGunMixin {
    @Redirect(method = "onUseTick", at = @At(value = "INVOKE", target = "Lmekanism/client/sound/SoundHandler;playSound(Lmekanism/common/registration/impl/SoundEventRegistryObject;)V"))
    private void clientOnlySound(SoundEventRegistryObject<?> soundEventRO, Level level) {
        if (level.isClientSide()) SoundHandler.playSound(soundEventRO);
    }
}
