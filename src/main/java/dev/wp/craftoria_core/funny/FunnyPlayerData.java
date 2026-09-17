package dev.wp.craftoria_core.funny;

import dev.wp.craftoria_core.Craftoria;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public final class FunnyPlayerData extends SavedData {
    private final Map<UUID, FunnyIntensity> intensities = new HashMap<>();
    private final Map<UUID, FunnyIntensity> lastEnabled = new HashMap<>();
    private final Set<UUID> joinedPlayers = new HashSet<>();

    public static FunnyPlayerData get(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();
        return overworld.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(FunnyPlayerData::new, FunnyPlayerData::load),
                Craftoria.ID + "_funny"
        );
    }

    private static FunnyPlayerData load(CompoundTag tag, HolderLookup.Provider registries) {
        FunnyPlayerData data = new FunnyPlayerData();
        CompoundTag players = tag.getCompound("players");
        for (String key : players.getAllKeys()) {
            try {
                UUID uuid = UUID.fromString(key);
                CompoundTag player = players.getCompound(key);
                FunnyIntensity intensity = FunnyIntensity.parseOrDefault(player.getString("intensity"), FunnyIntensity.OFF);
                FunnyIntensity previous = FunnyIntensity.parseOrDefault(player.getString("last_enabled"), FunnyIntensity.MEDIUM);
                data.intensities.put(uuid, intensity);
                data.lastEnabled.put(uuid, previous == FunnyIntensity.OFF ? FunnyIntensity.MEDIUM : previous);
            } catch (IllegalArgumentException ignored) {
                // Ignore malformed entries so one bad player record cannot prevent the world loading.
            }
        }

        ListTag joined = tag.getList("joined", Tag.TAG_STRING);
        for (Tag entry : joined) {
            try {
                data.joinedPlayers.add(UUID.fromString(entry.getAsString()));
            } catch (IllegalArgumentException ignored) {
                // Ignore malformed entries.
            }
        }
        return data;
    }

    public FunnyIntensity getIntensity(UUID uuid) {
        return intensities.getOrDefault(uuid, FunnyIntensity.OFF);
    }

    public FunnyIntensity getLastEnabled(UUID uuid) {
        return lastEnabled.getOrDefault(uuid, FunnyIntensity.MEDIUM);
    }

    public void setIntensity(UUID uuid, FunnyIntensity intensity) {
        if (getIntensity(uuid) == intensity) return;
        intensities.put(uuid, intensity);
        if (intensity != FunnyIntensity.OFF) lastEnabled.put(uuid, intensity);
        setDirty();
    }

    public void setLastEnabled(UUID uuid, FunnyIntensity intensity) {
        if (intensity == FunnyIntensity.OFF || getLastEnabled(uuid) == intensity) return;
        lastEnabled.put(uuid, intensity);
        setDirty();
    }

    public boolean isFirstJoin(UUID uuid) {
        return !joinedPlayers.contains(uuid);
    }

    public void markJoined(UUID uuid) {
        if (joinedPlayers.add(uuid)) setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag players = new CompoundTag();
        Set<UUID> uuids = new HashSet<>(intensities.keySet());
        uuids.addAll(lastEnabled.keySet());
        for (UUID uuid : uuids) {
            CompoundTag player = new CompoundTag();
            player.putString("intensity", getIntensity(uuid).serializedName());
            player.putString("last_enabled", getLastEnabled(uuid).serializedName());
            players.put(uuid.toString(), player);
        }
        tag.put("players", players);

        ListTag joined = new ListTag();
        for (UUID uuid : joinedPlayers) joined.add(StringTag.valueOf(uuid.toString()));
        tag.put("joined", joined);
        return tag;
    }
}
