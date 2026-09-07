package dev.wp.craftoria_core.mixin.relics;

import dev.wp.craftoria_core.config.ServerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(targets = "it.hurts.sskirillss.relics.items.relics.ring.RingOfTheSevenDeadlySinsItem")
public class RingOfTheSevenDeadlySinsMixin {
    @Redirect(
            method = "curioTick",
            at = @At(value = "INVOKE", target = "Ljava/util/List;contains(Ljava/lang/Object;)Z")
    )
    private boolean blacklistPuffishAttributes(List<?> blacklist, Object attributeName) {
        return attributeName instanceof String name && ServerConfig.gluttonyAttributeModBlacklist.stream()
                .anyMatch(modId -> name.startsWith(modId + ":"))
                || blacklist.contains(attributeName);
    }
}
