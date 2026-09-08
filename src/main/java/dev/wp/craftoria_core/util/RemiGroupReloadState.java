package dev.wp.craftoria_core.util;

public final class RemiGroupReloadState {
    private static final ThreadLocal<Boolean> GROUP_ONLY_RELOAD = ThreadLocal.withInitial(() -> false);

    private RemiGroupReloadState() {
    }

    public static void begin() {
        GROUP_ONLY_RELOAD.set(true);
    }

    public static void end() {
        GROUP_ONLY_RELOAD.remove();
    }

    public static boolean isActive() {
        return GROUP_ONLY_RELOAD.get();
    }
}
