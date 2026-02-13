package foxiwhitee.FoxDarkMagic.tile;

import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.helpers.FoxEssentiaHelper;
import foxiwhitee.FoxLib.tile.FoxBaseInvTile;
import foxiwhitee.FoxLib.tile.event.TileEvent;
import foxiwhitee.FoxLib.tile.event.TileEventType;
import foxiwhitee.FoxLib.tile.inventory.FoxInternalInventory;
import foxiwhitee.FoxLib.tile.inventory.InvOperation;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.WorldCoordinates;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.blocks.ItemJarNode;

import java.util.ArrayList;

public class TileMatterDistorter extends FoxBaseInvTile implements IAspectContainer {
    private final FoxInternalInventory inventory = new FoxInternalInventory(this, 1, 1);
    private final AspectList aspectList = new AspectList();
    private boolean hasNode;
    private int tick;

    public TileMatterDistorter() {

    }

    @TileEvent(TileEventType.TICK)
    public void tick() {
        if (worldObj.isRemote || !hasNode) {
            return;
        }

        if (tick++ >= DarkConfig.matterDistorterUpdateTicks) {
            tick = 0;
            ArrayList<WorldCoordinates> foundSources = FoxEssentiaHelper.getSourcesList(this, ForgeDirection.UNKNOWN, 3);

            if (!foundSources.isEmpty()) {
                for (Aspect aspect : Aspect.aspects.values()) {
                    if (FoxEssentiaHelper.drainEssentia(this, aspect, DarkConfig.matterDistorterConsume, ForgeDirection.UNKNOWN, DarkConfig.matterDistorterRange, false)) {
                        this.aspectList.add(aspect, DarkConfig.matterDistorterConsume);
                    }
                }
            }
        }

        if (aspectList.aspects.isEmpty()) {
            return;
        }

        ItemStack node = inventory.getStackInSlot(0);
        if (node.getItem() instanceof ItemJarNode item) {
            AspectList list = item.getAspects(node);
            for (Aspect aspect : aspectList.getAspects()) {
                if (aspectList.aspects == null || aspect == null) {
                    continue;
                }
                if (aspectList.aspects.get(aspect) >= DarkConfig.matterDistorterConsume) {
                    list.add(aspect, DarkConfig.matterDistorterGenerate);
                    aspectList.remove(aspect, DarkConfig.matterDistorterConsume);
                }
            }
            item.setAspects(node, list);
        }

        markForUpdate();
    }

    @TileEvent(TileEventType.SERVER_NBT_WRITE)
    public void writeToNbt(NBTTagCompound data) {
        aspectList.writeToNBT(data);
        data.setBoolean("hasNode", hasNode);
        data.setInteger("tick", tick);
    }

    @TileEvent(TileEventType.SERVER_NBT_READ)
    public void readFromNbt(NBTTagCompound data) {
        aspectList.readFromNBT(data);
        hasNode = data.getBoolean("hasNode");
        tick = data.getInteger("tick");
    }

    @Override
    public void onChunkUnload() {
        FoxEssentiaHelper.removeSource(this);
        super.onChunkUnload();
    }

    @Override
    public FoxInternalInventory getInternalInventory() {
        return inventory;
    }

    @Override
    public int[] getAccessibleSlotsBySide(ForgeDirection forgeDirection) {
        return new int[0];
    }

    @Override
    public void onChangeInventory(IInventory iInventory, int i, InvOperation invOperation, ItemStack itemStack, ItemStack itemStack1) {
        if (iInventory == inventory) {
            if (inventory.getStackInSlot(0) == null) {
                hasNode = false;
            } else {
                ItemStack node = inventory.getStackInSlot(0);
                if (node.getItem() instanceof ItemJarNode item) {
                    if (item.getNodeType(node) == NodeType.DARK && item.getAspects(node) != null) {
                        hasNode = true;
                    }
                }
            }
        }
    }

    @Override
    public AspectList getAspects() {
        return aspectList ;
    }

    @Override
    public void setAspects(AspectList aspectList) {

    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return true;
    }

    @Override
    public int addToContainer(Aspect aspect, int i) {
        return 0;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int i) {
        return false;
    }

    @Override
    public boolean takeFromContainer(AspectList aspectList) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int i) {
        return false;
    }

    @Override
    public boolean doesContainerContain(AspectList aspectList) {
        return false;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return 0;
    }
}
