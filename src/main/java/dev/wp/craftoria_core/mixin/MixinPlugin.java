package dev.wp.craftoria_core.mixin;

import com.bawnorton.mixinsquared.canceller.MixinCancellerRegistrar;
import dev.wp.craftoria_core.util.Utils;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MixinPlugin implements IMixinConfigPlugin {
    private final Map<String, Boolean> modStatus = new HashMap<>();
    private final Map<String, String> mixinToMod = new HashMap<>();

    private static final String BASE_PACKAGE = "dev.wp.craftoria_core.mixin.";

    private void setMixinToMod(String mixin, String mod) {
        mixinToMod.put(BASE_PACKAGE + mixin, mod);
    }

    @Override
    public void onLoad(String s) {
        List<String> mods = Utils.getModList();

        boolean ae2AndEmi = mods.contains("ae2") && mods.contains("emi");
        boolean jdtAndIF = mods.contains("justdirethings") && mods.contains("industrialforegoing");
        modStatus.put("ae2", mods.contains("ae2"));
        modStatus.put("ae2emi", ae2AndEmi);
        modStatus.put("emi", mods.contains("emi"));
        modStatus.put("remi", mods.contains("remi"));
        modStatus.put("jdtAndIF", jdtAndIF);
        modStatus.put("xycraft_core", mods.contains("xycraft_core"));
        modStatus.put("cataclysm", mods.contains("cataclysm"));
        modStatus.put("jdt", mods.contains("justdirethings"));
        modStatus.put("curios", mods.contains("curios"));
        modStatus.put("buildinggadgets2", mods.contains("buildinggadgets2"));
        modStatus.put("sound_physics", mods.contains("sound_physics_remastered"));
        modStatus.put("spectrum", mods.contains("spectrum"));
        modStatus.put("mekanism", mods.contains("mekanism"));
        modStatus.put("relics", mods.contains("relics"));
        modStatus.put("sdlink", mods.contains("sdlink"));
        modStatus.put("styledchat", mods.contains("styledchat"));
        modStatus.put("subtle_effects", mods.contains("subtle_effects"));
        modStatus.put("extendedae_plus", mods.contains("extendedae_plus"));
        modStatus.put("apothic_enchanting", mods.contains("apothic_enchanting"));
        modStatus.put("transmog", mods.contains("transmog"));
        modStatus.put("animusnv", mods.contains("animusnv"));
        modStatus.put("worldedit", mods.contains("worldedit"));
        modStatus.put("wcwt", mods.contains("wcwt"));
        modStatus.put("ae2wtlib", mods.contains("ae2wtlib"));

        if (mods.contains("extendedae_plus") && mods.contains("emi")) {
            MixinCancellerRegistrar.register((targets, mixin) -> mixin.startsWith("com.extendedae_plus.mixin.jei."));
        }

        // Client
        setMixinToMod("ae2.KeySortersMixin", "ae2emi");
        setMixinToMod("emi.ReloadWorkerMixin", "emi");
        setMixinToMod("emi.ItemEmiStackTooltipMixin", "emi");
        setMixinToMod("remi.RemiSearchWorkerMixin", "remi");
        setMixinToMod("buildinggadgets2.BuildingGadgetsRenderDisable", "buildinggadgets2");
        setMixinToMod("buildinggadgets2.BuildingGadgetsDataSaveGuard", "buildinggadgets2");
        setMixinToMod("sound_physics.SoundPhysicsMixin", "sound_physics");
        setMixinToMod("subtle_effects.SplashParticleProviderMixin", "subtle_effects");
        setMixinToMod("extendedae_plus.InputEventsMixin", "extendedae_plus");
        setMixinToMod("apothic_enchanting.EnchJEIPluginMixin", "apothic_enchanting");
        setMixinToMod("spectrum.SpectrumTooltipsMixin", "spectrum");
        setMixinToMod("transmog.PlayerMixinCompatibility", "transmog");
        setMixinToMod("transmog.RenderUtilsAccessor", "transmog");

        // Common
        setMixinToMod("cataclysm.CursedTombstoneEntityMixin", "cataclysm");
        setMixinToMod("foregoing.EnchantmentExtractorTileMixin", "jdtAndIF");
        setMixinToMod("foregoing.MobCrusherTileMixin", "jdtAndIF");
        setMixinToMod("jdt.CreatureCatcherEntityMixin", "jdt");
        setMixinToMod("jdt.UnstablePortalFluidTypeMixin", "jdt");
        setMixinToMod("ae2.CableBusBlockCollisionGuardMixin", "ae2");
        setMixinToMod("curios.CuriosEventHandlerMixin", "curios");
        setMixinToMod("spectrum.SpectrumEndermanEntityMixin", "spectrum");
        setMixinToMod("mekanism.WorldUtilsMixin", "mekanism");
        setMixinToMod("relics.RingOfTheSevenDeadlySinsMixin", "relics");
        setMixinToMod("relics.RelicsTooltipCompatibilityMixin", "relics");
        setMixinToMod("minecraft.PlayerListMixin", "sdlink");
        setMixinToMod("minecraft.DedicatedPlayerListMixin", "sdlink");
        setMixinToMod("minecraft.MinecraftServerMixin", "sdlink");
        setMixinToMod("sdlink.SdlinkServerEventsMixin", "sdlink");
        setMixinToMod("sdlink.SDLinkMinecraftBridgeMixin", "sdlink");
        setMixinToMod("extendedae_plus.OpenCraftFromJeiC2SPacketMixin", "extendedae_plus");
        setMixinToMod("styledchat.StyledChatUtilsMixin", "styledchat");
        setMixinToMod("animusnv.AltarTierAdvancementHandlerMixin", "animusnv");
        setMixinToMod("worldedit.NeoForgeWorldEditMixin", "worldedit");
        setMixinToMod("wcwt.WcwtToolkitHotbarStateMixin", "wcwt");
        setMixinToMod("wcwt.WcwtToolkitAccessMixin", "wcwt");
        setMixinToMod("ae2wtlib.AE2wtlibForgeMixin", "ae2wtlib");
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String modId = mixinToMod.get(mixinClassName);
        return modId == null || modStatus.getOrDefault(modId, false);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
