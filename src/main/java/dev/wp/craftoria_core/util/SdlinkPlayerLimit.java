package dev.wp.craftoria_core.util;

import com.hypherionmc.sdlink.core.database.SDLinkAccount;
import com.hypherionmc.sdlink.core.discord.BotController;
import com.hypherionmc.sdlink.core.managers.CacheManager;
import com.hypherionmc.sdlink.core.managers.DatabaseManager;
import com.mojang.authlib.GameProfile;
import dev.wp.craftoria_core.config.ServerConfig;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class SdlinkPlayerLimit {
    private static final Map<UUID, Boolean> BYPASS_CACHE = new ConcurrentHashMap<>();

    private SdlinkPlayerLimit() {
    }

    public static boolean hasBypassRole(GameProfile profile) {
        return hasBypassRole(profile.getId());
    }

    public static boolean hasBypassRole(UUID uuid) {
        if (ServerConfig.sdlinkBypassRoles.isEmpty()) {
            BYPASS_CACHE.clear();
            return false;
        }

        try {
            if (BotController.INSTANCE == null || !BotController.INSTANCE.isBotReady()) {
                return BYPASS_CACHE.getOrDefault(uuid, false);
            }

            SDLinkAccount account = DatabaseManager.INSTANCE.findById(uuid.toString(), SDLinkAccount.class);
            String discordId = account == null ? null : account.getDiscordID();
            if (discordId == null || discordId.isBlank()) {
                BYPASS_CACHE.remove(uuid);
                return false;
            }

            var member = CacheManager.getDiscordMembers().stream()
                    .filter(discordMember -> discordMember.getId().equalsIgnoreCase(discordId))
                    .findFirst();
            if (member.isEmpty()) {
                BYPASS_CACHE.remove(uuid);
                return false;
            }

            boolean bypass = member.get().getRoles().stream()
                    .anyMatch(role -> ServerConfig.sdlinkBypassRoles.stream()
                            .anyMatch(id -> id.equals(role.getId())));
            BYPASS_CACHE.put(uuid, bypass);
            return bypass;
        } catch (RuntimeException ignored) {
            return BYPASS_CACHE.getOrDefault(uuid, false);
        }
    }

    public static int countRegularPlayers(Collection<UUID> playerIds) {
        return (int) playerIds.stream().filter(uuid -> !hasBypassRole(uuid)).count();
    }
}
