package dev.wp.craftoria_core.arseng;

import com.hollingsworth.arsnouveau.api.source.AbstractSourceMachine;
import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.source.SourceProvider;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SourceContainerScanner {
    public static void collect(
            List<ISpecialSourceProvider> providers, BlockPos pos, Level level, int range, boolean taking) {
        Set<BlockPos> alreadyFound = new HashSet<>();

        for (var provider : providers) alreadyFound.add(provider.getCurrentPos());

        for (var neighbour : BlockPos.withinManhattan(pos, range, range, range)) {
            if (alreadyFound.contains(neighbour) || !level.isLoaded(neighbour)) continue;

            var tile = level.getBlockEntity(neighbour);

            if (tile instanceof AbstractSourceMachine) continue;

            var sourceCap = level.getCapability(CapabilityRegistry.SOURCE_CAPABILITY, neighbour, null);

            if (sourceCap != null && (taking ? sourceCap.canExtract() : sourceCap.canReceive())) {
                providers.add(new SourceProvider(new SourceCapTile(sourceCap), neighbour.immutable()));
            }
        }
    }

    private SourceContainerScanner() {
    }
}
