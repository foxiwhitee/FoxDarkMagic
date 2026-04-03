package foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers;

import appeng.api.networking.GridFlags;
import foxiwhitee.FoxLib.integration.applied.tile.TilePatternMachine;
import foxiwhitee.FoxLib.tile.inventory.FoxInternalInventory;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class TileAssembler extends TilePatternMachine {
    private final long maxCount;

    public TileAssembler(long maxCount) {
        this.maxCount = maxCount;
        getProxy().setFlags(GridFlags.REQUIRE_CHANNEL);
        getProxy().setIdlePowerUsage((long)Math.max(1, maxCount / 100D));
    }

    @Override
    protected long getMaxCount() {
        return maxCount - 1;
    }

    @Override
    public FoxInternalInventory getInternalInventory() {
        return (FoxInternalInventory) getPatterns();
    }

    @Override
    public int[] getAccessibleSlotsBySide(ForgeDirection whichSide) {
        return new int[0];
    }

}
