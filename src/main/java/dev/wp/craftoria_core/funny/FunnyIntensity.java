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

    public String serializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
