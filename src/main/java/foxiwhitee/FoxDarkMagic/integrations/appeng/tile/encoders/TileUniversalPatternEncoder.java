package foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders;

import appeng.api.storage.data.IAEItemStack;
import appeng.api.util.AECableType;
import appeng.api.util.DimensionalCoord;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.inventory.AppEngInternalAEInventory;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.inventory.InvOperation;
import appeng.util.item.AEItemStack;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.TileAENetworkInvOrientable;
import foxiwhitee.FoxLib.api.IHasNeiOverlay;
import foxiwhitee.FoxLib.utils.helpers.ItemStackUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public abstract class TileUniversalPatternEncoder extends TileAENetworkInvOrientable implements IHasNeiOverlay {
    private final AppEngInternalInventory inventory = new AppEngInternalInventory(this, 2);
    private final AppEngInternalInventory output = new AppEngInternalInventory(this, 1);
    private final AppEngInternalAEInventory crafting;

    public TileUniversalPatternEncoder(int craftingInventorySize) {
        this.crafting = new AppEngInternalAEInventory(this, craftingInventorySize);
    }

    public void encode() {
        if (inventory.getStackInSlot(0) == null || inventory.getStackInSlot(0).stackSize < 1) {
            return;
        }
        ItemStack output = new ItemStack(getEncodedPattern());
        if (this.inventory.getStackInSlot(1) != null && !ItemStackUtil.stackEquals(output, this.inventory.getStackInSlot(1))) {
            return;
        }
        IAEItemStack[] in = this.getInputs();
        IAEItemStack out = AEItemStack.create(this.output.getStackInSlot(0));
        if (in.length > 0 && out != null) {
            NBTTagCompound encodedValue = new NBTTagCompound();
            NBTTagList tagIn = new NBTTagList();
            NBTTagList tagOut = new NBTTagList();

            for(IAEItemStack i : in) {
                tagIn.appendTag(this.createItemTag(i));
            }

            tagOut.appendTag(this.createItemTag(out));

            encodedValue.setBoolean("crafting", false);
            encodedValue.setBoolean("beSubstitute", false);
            encodedValue.setBoolean("substitute", false);

            encodedValue.setTag("in", tagIn);
            encodedValue.setTag("out", tagOut);
            afterEncoding(encodedValue);
            output.setTagCompound(encodedValue);

            if (inventory.getStackInSlot(1) != null && inventory.getStackInSlot(1).stackSize < 64) {
                inventory.getStackInSlot(1).stackSize++;
                inventory.getStackInSlot(0).stackSize--;
            } else if (inventory.getStackInSlot(1) == null) {
                inventory.setInventorySlotContents(1, output);
                inventory.getStackInSlot(0).stackSize--;
            }
            if (inventory.getStackInSlot(0).stackSize <= 0) {
                inventory.setInventorySlotContents(0, null);
            }
        }
    }

    protected void afterEncoding(NBTTagCompound data) {}

    private NBTBase createItemTag(IAEItemStack i) {
        NBTTagCompound c = new NBTTagCompound();
        if (i != null) {
            i.writeToNBT(c);
        }
        return c;
    }

    private IAEItemStack[] getInputs() {
        List<IAEItemStack> in = new ArrayList<>();
        for (int i = 0; i < crafting.getSizeInventory(); i++) {
            IAEItemStack ais = crafting.getAEStackInSlot(i);
            if (ais == null) {
                continue;
            }
            IAEItemStack con = null;
            for (IAEItemStack st : in) {
                if (ItemStackUtil.stackEquals(ais.getItemStack(), st.getItemStack())) {
                    con = st;
                    break;
                }
            }
            if (con != null) {
                con.setStackSize(con.getStackSize() + ais.getStackSize());
            } else {
                in.add(ais.copy());
            }
        }
        return in.toArray(new IAEItemStack[0]);
    }

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeToNbt_(NBTTagCompound data) {
        crafting.writeToNBT(data, "crafting");
        output.writeToNBT(data, "output");
    }

    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readFromNbt_(NBTTagCompound data) {
        crafting.readFromNBT(data, "crafting");
        output.readFromNBT(data, "output");
    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Override
    public AECableType getCableConnectionType(ForgeDirection dir) {
        return AECableType.SMART;
    }

    @Override
    public AppEngInternalInventory getInternalInventory() {
        return inventory;
    }

    public AppEngInternalInventory getOutputInventory() {
        return output;
    }

    public AppEngInternalAEInventory getCraftingInventory() {
        return crafting;
    }

    @Override
    public void onChangeInventory(IInventory inv, int slot, InvOperation mc, ItemStack removed, ItemStack added) {
        if (inv == crafting) {
            updateRecipe();
        }
    }

    @Override
    public int[] getAccessibleSlotsBySide(ForgeDirection whichSide) {
        return new int[0];
    }

    protected abstract Item getEncodedPattern();

    public abstract void updateRecipe();
}
