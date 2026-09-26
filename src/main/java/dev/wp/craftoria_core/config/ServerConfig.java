package dev.wp.craftoria_core.config;

import dev.wp.craftoria_core.Craftoria;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;

public class ServerConfig {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final ModConfigSpec.BooleanValue DOUBLE_DOOR = BUILDER
            .translation(Craftoria.ID + ".config.double_door")
            .comment("Enable opening a door's partner alongside it.")
            .define("double_door", true);
    private static final ModConfigSpec.BooleanValue RIGHT_CLICK_HARVEST = BUILDER
            .translation(Craftoria.ID + ".config.right_click_harvest")
            .comment("Enable harvesting mature crops, cocoa, and nether wart by right-clicking.")
            .define("right_click_harvest", true);
    private static final ModConfigSpec.ConfigValue<List<? extends String>> GLUTTONY_ATTRIBUTE_MOD_BLACKLIST = BUILDER
            .translation(Craftoria.ID + ".config.gluttony_attribute_mod_blacklist")
            .comment("Mod IDs whose attributes are ignored by the Relics gluttony effect.")
            .defineListAllowEmpty(
                    "gluttony_attribute_mod_blacklist",
                    List.of("puffish_attributes", "eternal_starlight"),
                    () -> "mod_id",
                    value -> value instanceof String && !((String) value).isBlank()
            );

    public static final ModConfigSpec SPEC = BUILDER.build();
    public static boolean doubleDoor = true;
    public static boolean rightClickHarvest = true;
    public static List<? extends String> gluttonyAttributeModBlacklist = List.of("puffish_attributes");

    public static void init() {
        doubleDoor = DOUBLE_DOOR.get();
        rightClickHarvest = RIGHT_CLICK_HARVEST.get();
        gluttonyAttributeModBlacklist = GLUTTONY_ATTRIBUTE_MOD_BLACKLIST.get();
    }
}
