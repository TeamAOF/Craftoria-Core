package dev.wp.craftoria_core.util;

import com.hypherionmc.sdlink.core.database.SDLinkAccount;
import com.hypherionmc.sdlink.core.discord.BotController;
import com.hypherionmc.sdlink.core.managers.CacheManager;
import com.hypherionmc.sdlink.core.managers.DatabaseManager;
import com.mojang.authlib.GameProfile;
import dev.wp.craftoria_core.config.ServerConfig;

public final class SdlinkPlayerLimit {
    private SdlinkPlayerLimit() {
    }

    public static boolean hasBypassRole(GameProfile profile) {
        if (ServerConfig.sdlinkBypassRoles.isEmpty()) return false;

        try {
            if (BotController.INSTANCE == null || !BotController.INSTANCE.isBotReady()) return false;

            SDLinkAccount account = DatabaseManager.INSTANCE.findById(profile.getId().toString(), SDLinkAccount.class);
            String discordId = account == null ? null : account.getDiscordID();
            if (discordId == null || discordId.isBlank()) return false;

            return CacheManager.getDiscordMembers().stream()
                    .filter(member -> member.getId().equalsIgnoreCase(discordId))
                    .flatMap(member -> member.getRoles().stream())
                    .anyMatch(role -> ServerConfig.sdlinkBypassRoles.stream()
                            .anyMatch(id -> id.equals(role.getId())));
        } catch (RuntimeException ignored) {
            return false;
        }
    }
}
