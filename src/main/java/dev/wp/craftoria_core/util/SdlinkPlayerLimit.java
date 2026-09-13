package dev.wp.craftoria_core.util;

import com.hypherionmc.sdlink.core.database.SDLinkAccount;
import com.hypherionmc.sdlink.core.discord.BotController;
import com.hypherionmc.sdlink.core.managers.CacheManager;
import com.hypherionmc.sdlink.core.managers.DatabaseManager;
import com.mojang.authlib.GameProfile;
import dev.wp.craftoria_core.config.ServerConfig;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public final class SdlinkPlayerLimit {
    private static final long CACHE_TTL_NANOS = TimeUnit.SECONDS.toNanos(30);

    private static final Map<UUID, CacheEntry> BYPASS_CACHE = new ConcurrentHashMap<>();

    private record CacheEntry(boolean bypass, long expiresAtNanos) {
    }

    private SdlinkPlayerLimit() {
    }

    public static boolean hasBypassRole(GameProfile profile) {
        return hasBypassRole(profile.getId());
    }

    public static boolean hasBypassRole(UUID uuid) {
        if (ServerConfig.sdlinkBypassRoles.isEmpty()) {
            if (!BYPASS_CACHE.isEmpty()) {
                BYPASS_CACHE.clear();
            }
            return false;
        }

        long now = System.nanoTime();
        CacheEntry cached = BYPASS_CACHE.get(uuid);

        if (cached != null && cached.expiresAtNanos() > now) {
            return cached.bypass();
        }

        // Bot not ready: fail closed. An unexpired cached result was already returned above.
        if (BotController.INSTANCE == null || !BotController.INSTANCE.isBotReady()) {
            return false;
        }

        Set<String> bypassRoles = Set.copyOf(ServerConfig.sdlinkBypassRoles);

        CacheEntry entry = BYPASS_CACHE.compute(uuid, (key, existing) -> {
            long currentTime = System.nanoTime();

            if (existing != null && existing.expiresAtNanos() > currentTime) {
                return existing;
            }

            try {
                SDLinkAccount account =
                        DatabaseManager.INSTANCE.findById(key.toString(), SDLinkAccount.class);

                String discordId = account == null ? null : account.getDiscordID();
                if (discordId == null || discordId.isBlank()) {
                    return new CacheEntry(false, currentTime + CACHE_TTL_NANOS);
                }

                var member = CacheManager.getDiscordMembers().stream()
                        .filter(discordMember -> discordMember.getId().equals(discordId))
                        .findFirst();

                if (member.isEmpty()) {
                    return new CacheEntry(false, currentTime + CACHE_TTL_NANOS);
                }

                boolean bypass = member.get().getRoles().stream()
                        .anyMatch(role -> bypassRoles.contains(role.getId()));

                return new CacheEntry(bypass, currentTime + CACHE_TTL_NANOS);
            } catch (RuntimeException ignored) {
                // Do not allow a previous "true" authorization to live
                // indefinitely after the cache expires.
                return new CacheEntry(false, currentTime + CACHE_TTL_NANOS);
            }
        });

        return entry.bypass();
    }

    public static int countRegularPlayers(Collection<UUID> playerIds) {
        int regularPlayers = 0;

        for (UUID uuid : playerIds) {
            if (!hasBypassRole(uuid)) {
                regularPlayers++;
            }
        }

        return regularPlayers;
    }
}
