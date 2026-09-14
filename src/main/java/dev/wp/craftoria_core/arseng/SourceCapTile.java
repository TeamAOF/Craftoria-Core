package dev.wp.craftoria_core.arseng;

import com.hollingsworth.arsnouveau.api.source.ISourceCap;
import com.hollingsworth.arsnouveau.api.source.ISourceTile;

public record SourceCapTile(ISourceCap sourceCap) implements ISourceTile {
    private static final int MAX_PROBE = 1000000;

    @Override
    public int getTransferRate() {
        return sourceCap.getMaxExtract();
    }

    @Override
    public boolean canAcceptSource() {
        return sourceCap.canReceive();
    }

    @Override
    public boolean canProvideSource() {
        return sourceCap.canExtract();
    }

    @Override
    public int getSource() {
        return (int) Math.min(sourceCap.getSource(), Integer.MAX_VALUE - (long) MAX_PROBE);
    }

    @Override
    public int getMaxSource() {
        var receivable = sourceCap.receiveSource(MAX_PROBE, true);
        return (int) Math.min((long) getSource() + receivable, Integer.MAX_VALUE);
    }

    @Override
    public int setSource(int source) {
        int current = getSource();

        if (source > current) sourceCap.receiveSource(source - current, false);
        else if (source < current) sourceCap.extractSource(current - source, false);

        return getSource();
    }

    @Override
    public int addSource(int source, boolean simulate) {
        return sourceCap.receiveSource(source, simulate);
    }

    @Override
    public int addSource(int source) {
        sourceCap.receiveSource(source, false);
        return getSource();
    }

    @Override
    public int removeSource(int source) {
        int before = getSource();
        int extracted = sourceCap.extractSource(source, false);
        return before - extracted;
    }

    @Override
    public int removeSource(int source, boolean simulate) {
        return sourceCap.extractSource(source, simulate);
    }
}
