package foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.events.MENetworkCraftingPatternChange;
import appeng.api.storage.data.IAEItemStack;
import appeng.items.misc.ItemEncodedPattern;
import appeng.me.GridAccessException;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import foxiwhitee.FoxDarkMagic.integrations.appeng.api.IEssentiaPatternHelper;
import foxiwhitee.FoxLib.tile.inventory.FoxInternalInventory;
import foxiwhitee.FoxLib.tile.inventory.InvOperation;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class TileEssentiaAssembler extends TileAssembler {
    private final FoxInternalInventory upgrade = new FoxInternalInventory(this, 1, 1);
    private final FoxInternalInventory patternsWithoutEssense = new FoxInternalInventory(this, 36, 1);
    private boolean infinityEssentia;

    public TileEssentiaAssembler(long maxCount) {
        super(maxCount);
    }

    public FoxInternalInventory getUpgradeInventory() {
        return upgrade;
    }

    protected abstract Class<? extends ItemEncodedPattern> getPattenClass();

    @Override
    protected boolean isValidCraft(ICraftingPatternDetails pattern) {
        if (pattern instanceof IEssentiaPatternHelper) {
            return getPattenClass().isAssignableFrom(Objects.requireNonNull(pattern.getPattern().getItem()).getClass());
        }
        return false;
    }

    @Override
    public void onChangeInventory(IInventory iInventory, int i, InvOperation invOperation, ItemStack itemStack, ItemStack itemStack1) {
        if (iInventory == upgrade) {
            boolean oldInfinityEssentia = infinityEssentia;
            infinityEssentia = upgrade.getStackInSlot(0) != null;
            if (oldInfinityEssentia != infinityEssentia) {
                updatePatternList();
            }
        } else if (iInventory == getPatterns()) {
            if (invOperation != InvOperation.markDirty) {
                if (itemStack1 != null) {
                    if (itemStack1.getItem() instanceof ItemEncodedPattern pattern) {
                        ICraftingPatternDetails details = pattern.getPatternForItem(itemStack1, worldObj);
                        if (details instanceof IEssentiaPatternHelper helper) {
                            ItemStack st = itemStack1.copy();
                            NBTTagCompound tag = st.getTagCompound();
                            NBTTagList list = new NBTTagList();
                            for (IAEItemStack stack : helper.getOnlyItems()) {
                                list.appendTag(stack.toNBTGeneric());
                            }
                            tag.setTag("in", list);
                            st.setTagCompound(tag);
                            patternsWithoutEssense.setInventorySlotContents(i, st);
                        } else {
                            patternsWithoutEssense.setInventorySlotContents(i, null);
                        }
                    } else {
                        patternsWithoutEssense.setInventorySlotContents(i, null);
                    }
                } else {
                    patternsWithoutEssense.setInventorySlotContents(i, null);
                }
            }
            updatePatternList();
        }
    }

    protected void updatePatternList() {
        if (!getProxy().isReady()) {
            return;
        }
        IInventory patterns = infinityEssentia ? patternsWithoutEssense : getPatterns();
        Boolean[] tracked = new Boolean[patterns.getSizeInventory()];
        Arrays.fill(tracked, false);
        if (this.processor.getPatternList() != null) {
            this.processor.getPatternList().removeIf(pattern -> {
                for (int i = 0; i < patterns.getSizeInventory(); i++) {
                    if (pattern.getPattern() == patterns.getStackInSlot(i)) {
                        tracked[i] = true;
                        return false;
                    }
                }
                return true;
            });
        }
        for (int i = 0; i < tracked.length; i++) {
            if (!tracked[i]) {
                this.processor.addPattern(patterns.getStackInSlot(i));
            }
        }
        try {
            getProxy().getGrid().postEvent(new MENetworkCraftingPatternChange(this, getProxy().getNode()));
        } catch (GridAccessException ignored) {}
    }

    @Override
    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeToNbt_(NBTTagCompound data) {
        super.writeToNbt_(data);
        upgrade.writeToNBT(data, "upgrade");
        patternsWithoutEssense.writeToNBT(data, "patternsWithoutEssense");
        data.setBoolean("infinityEssentia", infinityEssentia);
    }

    @Override
    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readFromNbt_(NBTTagCompound data) {
        super.readFromNbt_(data);
        upgrade.readFromNBT(data, "upgrade");
        patternsWithoutEssense.readFromNBT(data, "patternsWithoutEssense");
        infinityEssentia = data.getBoolean("infinityEssentia");
    }

    @Override
    public void getDrops(World w, int x, int y, int z, List<ItemStack> drops) {
        super.getDrops(w, x, y, z, drops);
        if (upgrade.getStackInSlot(0) != null) {
            drops.add(upgrade.getStackInSlot(0));
        }
    }
}
