package dev.wp.craftoria_core.config;

import dev.wp.craftoria_core.Craftoria;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.BooleanValue FUNNY = BUILDER
            .translation(Craftoria.ID + ".config.funny")
            .comment("Enable the funny chat feature.")
            .define("funny", false);
    private static final ModConfigSpec.ConfigValue<List<? extends String>> GLUTTONY_ATTRIBUTE_MOD_BLACKLIST = BUILDER
            .translation(Craftoria.ID + ".config.gluttony_attribute_mod_blacklist")
            .comment("Mod IDs whose attributes are ignored by the Relics gluttony effect.")
            .defineListAllowEmpty(
                    "gluttony_attribute_mod_blacklist",
                    List.of("puffish_attributes", "eternal_starlight"),
                    () -> "mod_id",
                    value -> value instanceof String && !((String) value).isBlank()
            );
    private static final ModConfigSpec.ConfigValue<List<? extends String>> SDLINK_BYPASS_ROLES = BUILDER
            .translation(Craftoria.ID + ".config.sdlink_bypass_roles")
            .comment("Discord role IDs whose linked players bypass the server player limit.")
            .defineListAllowEmpty(
                    "sdlink_bypass_roles",
                    List.of(),
                    () -> "discord_role_id",
                    value -> value instanceof String && !((String) value).isBlank()
            );

    public static final ModConfigSpec SPEC = BUILDER.build();
    public static boolean funny = false;
    public static List<? extends String> gluttonyAttributeModBlacklist = List.of("puffish_attributes");
    public static List<? extends String> sdlinkBypassRoles = List.of();

    public static void init() {
        funny = FUNNY.get();
        gluttonyAttributeModBlacklist = GLUTTONY_ATTRIBUTE_MOD_BLACKLIST.get();
        sdlinkBypassRoles = SDLINK_BYPASS_ROLES.get();
    }
}
