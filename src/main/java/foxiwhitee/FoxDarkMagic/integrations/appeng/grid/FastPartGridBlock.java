package foxiwhitee.FoxDarkMagic.integrations.appeng.grid;

import appeng.api.networking.*;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.parts.PartItemStack;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import foxiwhitee.FoxDarkMagic.integrations.appeng.parts.BasePart;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import thaumicenergistics.api.grid.IEssentiaGrid;
import thaumicenergistics.api.grid.IMEEssentiaMonitor;

import java.util.EnumSet;

// Taken From Thaumic Energistics
public class FastPartGridBlock implements IGridBlock {
    protected BasePart part;

    public FastPartGridBlock(final BasePart part) {
        this.part = part;
    }

    @Override
    public EnumSet<ForgeDirection> getConnectableSides() {
        return EnumSet.noneOf(ForgeDirection.class);
    }

    public IEnergyGrid getEnergyGrid() {
        IGrid grid = this.getGrid();

        if (grid == null) {
            return null;
        }

        return grid.getCache(IEnergyGrid.class);
    }

    public IMEEssentiaMonitor getEssentiaMonitor() {
        IGrid grid = this.getGrid();

        if (grid == null) {
            return null;
        }

        return grid.getCache(IEssentiaGrid.class);
    }

    @Override
    public EnumSet<GridFlags> getFlags() {
        return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
    }

    public final IGrid getGrid() {
        IGridNode node = this.part.getGridNode();

        if (node != null) {
            return node.getGrid();
        }

        return null;
    }

    @Override
    public AEColor getGridColor() {
        return AEColor.Transparent;
    }

    @Override
    public double getIdlePowerUsage() {
        return this.part.getIdlePowerUsage();
    }

    @Override
    public DimensionalCoord getLocation() {
        return this.part.getLocation();
    }

    @Override
    public IGridHost getMachine() {
        return this.part;
    }

    @Override
    public ItemStack getMachineRepresentation() {
        return this.part.getItemStack(PartItemStack.Network);
    }

    public ISecurityGrid getSecurityGrid() {
        IGrid grid = this.getGrid();

        if (grid == null) {
            return null;
        }

        return grid.getCache(ISecurityGrid.class);
    }

    @Override
    public void gridChanged() {}

    @Override
    public boolean isWorldAccessible() {
        return false;
    }

    @Override
    public void onGridNotification(final GridNotification notification) {}

    @Override
    public final void setNetworkStatus(final IGrid grid, final int usedChannels) {}
}
