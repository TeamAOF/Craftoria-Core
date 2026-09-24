package dev.wp.craftoria_core.mixin;

import com.bawnorton.mixinsquared.canceller.MixinCancellerRegistrar;
import dev.wp.craftoria_core.util.Utils;
import me.fallenbreath.conditionalmixin.api.mixin.RestrictiveMixinConfigPlugin;

import java.util.List;
import java.util.Set;

public class MixinPlugin extends RestrictiveMixinConfigPlugin {
    @Override
    public void onLoad(String s) {
        List<String> mods = Utils.getModList();

        if (mods.contains("extendedae_plus") && mods.contains("emi")) {
            MixinCancellerRegistrar.register((targets, mixin) -> mixin.startsWith("com.extendedae_plus.mixin.jei."));
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }
}
