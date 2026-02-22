package foxiwhitee.FoxDarkMagic.tile;

import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.network.ByteBufUtils;
import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.helpers.FoxEssentiaHelper;
import foxiwhitee.FoxDarkMagic.item.ItemInfinityStorageUpgrade;
import foxiwhitee.FoxDarkMagic.item.ItemStackUpgrade;
import foxiwhitee.FoxLib.tile.FoxBaseInvTile;
import foxiwhitee.FoxLib.tile.event.TileEvent;
import foxiwhitee.FoxLib.tile.event.TileEventType;
import foxiwhitee.FoxLib.tile.inventory.FoxInternalInventory;
import foxiwhitee.FoxLib.tile.inventory.InvOperation;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IAspectSource;
import thaumcraft.common.blocks.ItemJarFilled;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumicenergistics.api.storage.IAspectStorage;

import java.util.List;

@Optional.Interface(modid = "thaumicenergistics", iface = "thaumicenergistics.api.storage.IAspectStorage")
public class TileSingularAlchemicalFurnace extends FoxBaseInvTile implements IAspectContainer, IAspectSource, IAspectStorage {
    private static int[] slots;
    private final FoxInternalInventory inventory = new FoxInternalInventory(this, 20);
    private final FoxInternalInventory upgrades = new FoxInternalInventory(this, 2, 1);
    private final FoxInternalInventory jars = new FoxInternalInventory(this, 7, 1);
    private final AspectList aspectList = new AspectList();
    private int tick;
    private int maxStore;
    private final int ticksNeed;
    private boolean stack;
    private Aspect selected;

    public TileSingularAlchemicalFurnace() {
        this.maxStore = DarkConfig.singularArcaneFurnaceStorage;
        this.ticksNeed = DarkConfig.singularArcaneFurnaceTicksNeed;

        if (slots == null) {
            slots = new int[inventory.getSizeInventory()];
            for (int i = 0; i < inventory.getSizeInventory(); i++) {
                slots[i] = i;
            }
        }
    }

    @TileEvent(TileEventType.TICK)
    public void tick() {
        if (worldObj.isRemote) {
            return;
        }

        if (selected != null) {
            boolean needUpdate = false;
            int have = aspectList.getAmount(selected);
            for (ItemStack stack : jars.toArray()) {
                if (stack != null && have > 0) {
                    if (stack.getItem() instanceof ItemJarFilled jar) {
                        AspectList aspects = jar.getAspects(stack);
                        if (aspects != null) {
                            if (aspects.size() == 1) {
                                Aspect aspect = aspects.getAspects()[0];
                                int count = aspects.getAmount(aspect);
                                if (aspect == selected && count < 64) {
                                    int canSend = Math.min(64, Math.min(64 - count, have));
                                    aspects.aspects.put(aspect, canSend + count);
                                    jar.setAspects(stack, aspects);
                                    have -= canSend;
                                    aspectList.remove(selected, canSend);
                                    needUpdate = true;
                                }
                            }
                        } else {
                            aspects = new AspectList();
                            int canSend = Math.min(64, have);
                            aspects.aspects.put(selected, canSend);
                            jar.setAspects(stack, aspects);
                            have -= canSend;
                            aspectList.remove(selected, canSend);
                            needUpdate = true;
                        }
                    }
                }
            }
            if (needUpdate) {
                markForUpdate();
            }
        }

        if (!isNotEmpty(inventory) || !canSmeltAny(inventory)) {
            int oldTick = tick;
            tick = 0;
            if (oldTick != ticksNeed) {
                markForUpdate();
            }
            return;
        }
        if (tick++ >= ticksNeed) {
            tick = 0;
            smeltAllIfCan(inventory);
        }

        markForUpdate();
    }

    private void smeltAllIfCan(FoxInternalInventory inventory) {
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack != null) {
                AspectList al = ThaumcraftCraftingManager.getObjectTags(stack);
                al = ThaumcraftCraftingManager.getBonusTags(stack, al);
                if (al.size() != 0) {
                    int operations = !this.stack ? 1 : Math.min(stack.stackSize, 64);
                    for (int j = 0; j < operations; j++) {
                        boolean cantSmelt = false;
                        for (Aspect aspect : al.getAspects()) {
                            cantSmelt = !(aspectList.getAmount(aspect) + al.getAmount(aspect) <= maxStore);
                        }
                        if (!cantSmelt) {
                            stack.stackSize--;
                            if (stack.stackSize == 0) {
                                inventory.setInventorySlotContents(i, null);
                            }
                            aspectList.add(al);
                        }
                    }
                }
            }
        }
    }

    private boolean isNotEmpty(FoxInternalInventory inventory) {
        for (ItemStack stack : inventory.toArray()) {
            if (stack != null) {
                return true;
            }
        }
        return false;
    }

    private boolean canSmeltAny(FoxInternalInventory inventory) {
        for (ItemStack stack : inventory.toArray()) {
            if (canSmelt(stack)) {
                return true;
            }
        }
        return false;
    }

    private boolean canSmelt(ItemStack stack) {
        if (stack != null) {
            AspectList al = ThaumcraftCraftingManager.getObjectTags(stack);
            al = ThaumcraftCraftingManager.getBonusTags(stack, al);
            if (al.size() != 0) {
                boolean cantSmelt = false;
                for (Aspect aspect : al.getAspects()) {
                    cantSmelt = !(aspectList.getAmount(aspect) + al.getAmount(aspect) <= maxStore);
                }
                return !cantSmelt;
            }
        }
        return false;
    }

    @TileEvent(TileEventType.SERVER_NBT_WRITE)
    public void writeToNbt(NBTTagCompound data) {
        aspectList.writeToNBT(data);
        upgrades.writeToNBT(data, "upgrades");
        jars.writeToNBT(data, "jars");
        data.setInteger("tick", tick);
        data.setInteger("maxStore", maxStore);
        data.setBoolean("stack", stack);
        if (selected != null) {
            data.setString("selected", selected.getTag());
        }
    }

    @TileEvent(TileEventType.SERVER_NBT_READ)
    public void readFromNbt(NBTTagCompound data) {
        aspectList.readFromNBT(data);
        upgrades.readFromNBT(data, "upgrades");
        jars.readFromNBT(data, "jars");
        tick = data.getInteger("tick");
        maxStore = data.getInteger("maxStore");
        stack = data.getBoolean("stack");
        if (data.hasKey("selected")) {
            selected = Aspect.getAspect(data.getString("selected"));
        }
    }

    @TileEvent(TileEventType.CLIENT_NBT_WRITE)
    public void writeToStream(ByteBuf data) {
        data.writeInt(tick);
        data.writeBoolean(selected != null);
        if (selected != null) {
            ByteBufUtils.writeUTF8String(data, selected.getTag());
        }
        FoxEssentiaHelper.writeToStream(data, aspectList);
    }

    @TileEvent(TileEventType.CLIENT_NBT_READ)
    public boolean readFromStream(ByteBuf data) {
        int oldTick = tick;
        Aspect oldSelected = selected;
        tick = data.readInt();
        if (data.readBoolean()) {
            selected = Aspect.getAspect(ByteBufUtils.readUTF8String(data));
        } else {
            selected = null;
        }
        FoxEssentiaHelper.readFromStream(data, aspectList);
        return oldTick != tick || oldSelected != selected;
    }

    @Override
    public FoxInternalInventory getInternalInventory() {
        return inventory;
    }

    public FoxInternalInventory getUpgradesInventory() {
        return upgrades;
    }

    public FoxInternalInventory getJarsInventory() {
        return jars;
    }

    @Override
    public int[] getAccessibleSlotsBySide(ForgeDirection forgeDirection) {
        return slots;
    }

    @Override
    public void onChangeInventory(IInventory iInventory, int i, InvOperation invOperation, ItemStack itemStack, ItemStack itemStack1) {
        if (iInventory == upgrades) {
            this.stack = false;
            this.maxStore = DarkConfig.singularArcaneFurnaceStorage;
            for (ItemStack stack : upgrades.toArray()) {
                if (stack != null) {
                    if (stack.getItem() instanceof ItemStackUpgrade) {
                        this.stack = true;
                    }
                    if (stack.getItem() instanceof ItemInfinityStorageUpgrade) {
                        this.maxStore = Integer.MAX_VALUE;
                    }
                }
            }
            for (Aspect aspect : aspectList.getAspects()) {
                if (aspectList.getAmount(aspect) > maxStore) {
                    aspectList.aspects.put(aspect, maxStore);
                }
            }
            markForUpdate();
        }
    }

    @Override
    public AspectList getAspects() {
        return aspectList;
    }

    @Override
    public void setAspects(AspectList aspectList) {

    }

    @Override
    public boolean doesContainerAccept(Aspect aspect) {
        return false;
    }

    @Override
    public int addToContainer(Aspect aspect, int i) {
        return 0;
    }

    @Override
    public boolean takeFromContainer(Aspect aspect, int i) {
        if (this.aspectList.getAmount(aspect) >= i) {
            this.aspectList.remove(aspect, i);
            this.markForUpdate();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean takeFromContainer(AspectList aspectList) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect aspect, int i) {
        return this.aspectList.getAmount(aspect) >= i;
    }

    @Override
    public boolean doesContainerContain(AspectList aspectList) {
        Aspect[] aspectArray = aspectList.getAspects();

        for (Aspect a : aspectArray) {
            if (this.aspectList.getAmount(a) < aspectList.getAmount(a)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int containerContains(Aspect aspect) {
        return this.aspectList.getAmount(aspect);
    }

    public int getProgress() {
        return tick;
    }

    public int getTicksNeed() {
        return ticksNeed;
    }

    @Override
    public void getDrops(World w, int x, int y, int z, List<ItemStack> drops) {
        super.getDrops(w, x, y, z, drops);
        for (ItemStack stack : jars.toArray()) {
            if (stack != null) {
                drops.add(stack);
            }
        }
        for (ItemStack stack : upgrades.toArray()) {
            if (stack != null) {
                drops.add(stack);
            }
        }
    }

    public Aspect getSelected() {
        return selected;
    }

    public void setSelected(Aspect selected) {
        this.selected = selected;
        markForUpdate();
    }

    @Override
    @Optional.Method(modid = "thaumicenergistics")
    public int getContainerCapacity() {
        return maxStore;
    }

    @Override
    @Optional.Method(modid = "thaumicenergistics")
    public boolean doesShareCapacity() {
        return false;
    }
}
