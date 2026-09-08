package dev.wp.craftoria_core.mixin.apothic_enchanting;

import dev.shadowsoffire.apothic_enchanting.compat.EnchJEIPlugin;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.util.RandomSource;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EnchJEIPlugin.class, remap = false)
public class EnchJEIPluginMixin {
    @Unique private final RandomSource craftoriaCore$random = RandomSource.createNewThreadLocalInstance();

    @Redirect(
            method = "registerRecipes",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/multiplayer/ClientLevel;random:Lnet/minecraft/util/RandomSource;", opcode = Opcodes.GETFIELD),
            remap = false
    )
    private RandomSource useThreadLocalRandom(ClientLevel instance) {
        return craftoriaCore$random;
    }
}
