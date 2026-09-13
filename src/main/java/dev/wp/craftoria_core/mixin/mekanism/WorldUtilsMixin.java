package dev.wp.craftoria_core.mixin.mekanism;

import mekanism.common.util.WorldUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldUtils.class)
public class WorldUtilsMixin {
    @Redirect(
            method = "markChunkDirty(Lnet/minecraft/world/level/Level;II)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;getChunk(IILnet/minecraft/world/level/chunk/status/ChunkStatus;Z)Lnet/minecraft/world/level/chunk/ChunkAccess;"
            )
    )
    private static ChunkAccess craftoriaCore$markChunkDirtyNonBlocking(Level world, int chunkX, int chunkZ, ChunkStatus status, boolean create) {
        MinecraftServer server = world.getServer();
        if (server != null && !server.isSameThread()) {
            server.execute(() -> craftoriaCore$markChunkDirtyOnServerThread(world, chunkX, chunkZ));
            return null;
        }

        return craftoriaCore$markChunkDirtyOnServerThread(world, chunkX, chunkZ);
    }

    @Unique
    private static ChunkAccess craftoriaCore$markChunkDirtyOnServerThread(Level world, int chunkX, int chunkZ) {
        ChunkAccess chunk = world.getChunkSource().getChunkNow(chunkX, chunkZ);
        if (chunk != null) {
            chunk.setUnsaved(true);
        }
        return chunk;
    }
}
