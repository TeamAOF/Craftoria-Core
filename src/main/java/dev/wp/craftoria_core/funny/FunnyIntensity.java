package dev.wp.craftoria_core.funny;

import java.util.Locale;

public enum FunnyIntensity {
    OFF,
    LOW,
    MEDIUM,
    HIGH;

    public static FunnyIntensity parse(String value) {
        return valueOf(value.toUpperCase(Locale.ROOT));
    }

    public static FunnyIntensity parseOrDefault(String value, FunnyIntensity fallback) {
        try {
            return parse(value);
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }

    public String serializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
