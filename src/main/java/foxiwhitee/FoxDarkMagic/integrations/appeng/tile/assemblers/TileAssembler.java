package foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers;

import appeng.api.networking.GridFlags;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.AECableType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

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
    public AECableType getCableConnectionType(ForgeDirection dir) {
        return AECableType.SMART;
    }

    @Override
    public IInventory getInternalInventory() {
        return getPatterns();
    }

    @Override
    public int[] getAccessibleSlotsBySide(ForgeDirection whichSide) {
        return new int[0];
    }

    @Override
    public void getDrops(World w, int x, int y, int z, List<ItemStack> drops) {
        super.getDrops(w, x, y, z, drops);
        for (IAEStack<?> stack : needSend) {
            if (stack instanceof IAEItemStack iaeItemStack) {
                drops.add(iaeItemStack.getItemStack());
            }
        }
    }
}
