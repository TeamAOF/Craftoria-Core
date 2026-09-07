package dev.wp.craftoria_core.datagen.client;

import dev.wp.craftoria_core.CItems;
import dev.wp.craftoria_core.Craftoria;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public final class Lang extends LanguageProvider {
    public Lang(GatherDataEvent event) {
        super(event.getGenerator().getPackOutput(), Craftoria.ID, "en_us");
    }

    private static String key(String path) {
        return Craftoria.ID + "." + path;
    }

    @Override
    protected void addTranslations() {
        this.add(key("config.funny"), "Funny");
        this.add(key("config.gluttony_attribute_mod_blacklist"), "Gluttony attribute mod blacklist");
        this.add(key("config.sdlink_bypass_roles"), "SDLink Player limit bypass roles");

        for (var item : CItems.ITEMS.getEntries()) {
            String key = item.getId().getPath();
            String formattedName = toMcFormat(key);
            this.add(("item.%s.%s".formatted(Craftoria.ID, key)), formattedName);
        }
    }

    private String toMcFormat(String key) {
        String[] parts = key.split("_");
        StringBuilder formatted = new StringBuilder();
        for (String part : parts) {
            if (!formatted.isEmpty()) formatted.append(" ");
            formatted.append(part.substring(0, 1).toUpperCase()).append(part.substring(1).toLowerCase());
        }
        return formatted.toString();
    }
}
