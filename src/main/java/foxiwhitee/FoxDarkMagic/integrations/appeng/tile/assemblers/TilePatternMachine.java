package foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers;

import appeng.api.config.Actionable;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.implementations.tiles.ICraftingMachine;
import appeng.api.networking.IGridNode;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.networking.crafting.ICraftingProvider;
import appeng.api.networking.crafting.ICraftingProviderHelper;
import appeng.api.networking.events.MENetworkCraftingPatternChange;
import appeng.api.networking.events.MENetworkEvent;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.storage.data.IAEStack;
import appeng.api.util.DimensionalCoord;
import appeng.api.util.IInterfaceViewable;
import appeng.container.ContainerNull;
import appeng.crafting.MECraftingInventory;
import appeng.items.misc.ItemEncodedPattern;
import appeng.me.GridAccessException;
import appeng.me.cluster.implementations.CraftingCPUCluster;
import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.inventory.AppEngInternalInventory;
import appeng.tile.inventory.InvOperation;
import foxiwhitee.FoxDarkMagic.integrations.appeng.helpers.CrafterHelper;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.TileAENetworkInvOrientable;
import foxiwhitee.FoxLib.integration.applied.api.crafting.ICraftingCPUClusterAccessor;
import foxiwhitee.FoxLib.integration.applied.api.crafting.IPreCraftingMedium;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings("unused")
public abstract class TilePatternMachine extends TileAENetworkInvOrientable implements ICraftingMachine, ICraftingProvider, IGridTickable, IInterfaceViewable, IPreCraftingMedium {
    protected final AppEngInternalInventory patterns = new AppEngInternalInventory(this, 36, 1);
    protected List<ICraftingPatternDetails> patternList;
    protected ICraftingPatternDetails activePattern;
    protected long craftCount;
    protected InventoryCrafting craftingGrid;
    protected final List<IAEStack<?>> needSend = new ArrayList<>();
    protected final MachineSource source = new MachineSource(this);

    public TilePatternMachine() {

    }

    @Override
    public DimensionalCoord getLocation() {
        return new DimensionalCoord(this);
    }

    @Override
    public ItemStack getSelfRep() {
        return getItemFromTile(this);
    }

    @Override
    public ItemStack getDisplayRep() {
        return getItemFromTile(this);
    }

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeToNbt_(NBTTagCompound data) {
        patterns.writeToNBT(data, "patterns");
        if (this.craftingGrid != null) {
            data.setInteger("invC_size", this.craftingGrid.getSizeInventory());
            for (int i = 0; i < this.craftingGrid.getSizeInventory(); i++) {
                ItemStack is = this.craftingGrid.getStackInSlot(i);
                if (is != null) {
                    NBTTagCompound tag = new NBTTagCompound();
                    is.writeToNBT(tag);
                    data.setTag("invC_" + i, tag);
                }
            }
        }
        if (this.activePattern != null) {
            ItemStack pattern = this.activePattern.getPattern();
            if (pattern != null) {
                NBTTagCompound compound = new NBTTagCompound();
                pattern.writeToNBT(compound);
                data.setTag("activePattern", compound);
            }
        }
        data.setLong("craftCount", craftCount);
        CrafterHelper.writeToNbtNeedItems(data, this.needSend);
    }

    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readFromNbt_(NBTTagCompound data) {
        patterns.readFromNBT(data, "patterns");
        if (data.hasKey("invC_size")) {
            int size = data.getInteger("invC_size");
            this.craftingGrid = new InventoryCrafting(new ContainerNull(), size, 1);
            for (int i = 0; i < size; i++) {
                if (data.hasKey("invC_" + i)) {
                    NBTTagCompound tag = data.getCompoundTag("invC_" + i);
                    this.craftingGrid.setInventorySlotContents(i, ItemStack.loadItemStackFromNBT(tag));
                }
            }
        }
        if (data.hasKey("activePattern")) {
            ItemStack myPat = ItemStack.loadItemStackFromNBT(data.getCompoundTag("activePattern"));
            if (myPat != null) {
                Item var4 = myPat.getItem();
                if (var4 instanceof ItemEncodedPattern iep) {
                    World w = this.getWorldObj();
                    ICraftingPatternDetails ph = iep.getPatternForItem(myPat, w);
                    if (ph != null && ph.isCraftable()) {
                        this.activePattern = ph;
                    }
                }
            }
        }
        craftCount = data.getLong("craftCount");
        CrafterHelper.readFromNbtNeedItems(data, this.needSend);
    }

    @Override
    public boolean pushPattern(ICraftingPatternDetails iCraftingPatternDetails, InventoryCrafting inventoryCrafting, ForgeDirection forgeDirection) {
        return false;
    }

    @Override
    public boolean acceptsPlans() {
        return false;
    }

    @Override
    public void provideCrafting(ICraftingProviderHelper helper) {
        if (patternList != null) {
            patternList.stream()
                .filter(pattern -> !pattern.isCraftable())
                .forEach(pattern -> helper.addCraftingOption(this, pattern));
        }
    }

    @Override
    public void onReady() {
        super.onReady();
        updatePatternList();
    }

    protected void addPattern(ItemStack stack) {
        if (stack == null || !(stack.getItem() instanceof ICraftingPatternItem)) {
            return;
        }
        ICraftingPatternDetails pattern = ((ICraftingPatternItem) stack.getItem()).getPatternForItem(stack, worldObj);
        if (pattern != null) {
            if (patternList == null) {
                patternList = new LinkedList<>();
            }
            patternList.add(pattern);
        }
    }

    protected void updatePatternList() {
        if (!getProxy().isReady()) {
            return;
        }
        Boolean[] tracked = new Boolean[getPatterns().getSizeInventory()];
        Arrays.fill(tracked, false);
        if (patternList != null) {
            patternList.removeIf(pattern -> {
                for (int i = 0; i < getPatterns().getSizeInventory(); i++) {
                    if (pattern.getPattern() == getPatterns().getStackInSlot(i)) {
                        tracked[i] = true;
                        return false;
                    }
                }
                return true;
            });
        }
        for (int i = 0; i < tracked.length; i++) {
            if (!tracked[i]) {
                addPattern(getPatterns().getStackInSlot(i));
            }
        }
        try {
            getProxy().getGrid().postEvent(new MENetworkCraftingPatternChange(this, getProxy().getNode()));
        } catch (GridAccessException ignored) {}
    }

    @Override
    public boolean pushPattern(ICraftingPatternDetails iCraftingPatternDetails, InventoryCrafting inventoryCrafting) {
        return false;
    }

    @Override
    public boolean isBusy() {
        return activePattern != null;
    }

    @Override
    public TickingRequest getTickingRequest(IGridNode iGridNode) {
        return new TickingRequest(1, 1, false, false);
    }

    private ItemStack getContainerItem(final ItemStack stackInSlot) {
        if (stackInSlot == null) {
            return null;
        }

        final Item i = stackInSlot.getItem();
        if (i == null || !i.hasContainerItem(stackInSlot)) {
            return null;
        }

        ItemStack ci = i.getContainerItem(stackInSlot.copy());
        if (ci != null && ci.isItemStackDamageable() && ci.getItemDamage() > ci.getMaxDamage()) {
            ci = null;
        }

        return ci;
    }

    @Override
    public TickRateModulation tickingRequest(IGridNode node, int ticksSinceLastCall) {
        if (activePattern != null && needSend.isEmpty()) {
            List<IAEStack<?>> outputs = CrafterHelper.calculateOutputs(activePattern, craftCount, craftingGrid);
            needSend.addAll(outputs);
        }
        CrafterHelper.trySendItems(getProxy(), source, needSend);
        if (needSend.isEmpty()) {
            activePattern = null;
            craftCount = 0;
        }
        return TickRateModulation.IDLE;
    }

    protected void afterCrafting() {
        this.activePattern = null;
        this.craftCount = 0;
        this.craftingGrid = null;
    }

    @Override
    public int rows() {
        return 4;
    }

    @Override
    public int rowSize() {
        return 9;
    }

    @Override
    public IInventory getPatterns() {
        return patterns;
    }

    @Override
    public TileEntity getTileEntity() {
        return this;
    }

    @Override
    public boolean shouldDisplay() {
        return true;
    }

    @Override
    public void onChangeInventory(IInventory iInventory, int i, InvOperation invOperation, ItemStack itemStack, ItemStack itemStack1) {
        if (iInventory == getPatterns()) {
            updatePatternList();
        }
    }

    protected abstract long getMaxCount();

    protected abstract boolean isValidCraft(ICraftingPatternDetails pattern);

    @Override
    public boolean pushPattern(ICraftingPatternDetails pattern, InventoryCrafting ic, CraftingCPUCluster cluster) {
        if (patternList == null || !patternList.contains(pattern) || pattern.isCraftable()) {
            return false;
        }
        if (!isValidCraft(pattern)) {
            return false;
        }
        ICraftingCPUClusterAccessor accessor = (ICraftingCPUClusterAccessor) ((Object) cluster);
        MECraftingInventory inventory = cluster.getInventory();

        long pendingRequests = accessor.getWaitingFor(pattern);
        long maxToProcess = Math.min(pendingRequests - 1, getMaxCount());

        if (maxToProcess < 0) {
            return false;
        }

        long finalCraftCount = maxToProcess;

        for (IAEStack<?> input : pattern.getCondensedAEInputs()) {
            IAEStack<?> request = input.copy();
            request.setStackSize(request.getStackSize() * maxToProcess);

            IAEStack<?> availableStack = inventory.extractItems(request, Actionable.SIMULATE, cluster.getActionSource());
            long availableCount = (availableStack == null) ? 0 : availableStack.getStackSize();

            if (request.getStackSize() > availableCount) {
                finalCraftCount = Math.min(finalCraftCount, availableCount / input.getStackSize());
            }
        }

        if (finalCraftCount >= 1) {
            for (IAEStack<?> input : pattern.getCondensedAEInputs()) {
                IAEStack<?> toExtract = input.copy();
                toExtract.setStackSize(toExtract.getStackSize() * finalCraftCount);
                inventory.extractItems(toExtract, Actionable.MODULATE, cluster.getActionSource());
            }

            for (IAEStack<?> output : pattern.getCondensedAEOutputs()) {
                IAEStack<?> result = output.copy();
                result.setStackSize(result.getStackSize() * finalCraftCount);

                accessor.callPostChange(result, cluster.getActionSource());
                accessor.getWaitingFor().add(result.copy());
                accessor.callPostCraftingStatusChange(result.copy());
            }

            accessor.setWaitingFor(pattern, pendingRequests - finalCraftCount);
        }

        this.activePattern = pattern;
        this.craftCount = finalCraftCount + 1;
        this.craftingGrid = ic;

        return true;
    }

    @MENetworkEventSubscribe
    public void onNetworkChange(MENetworkEvent event) {
        try {
            getProxy().getGrid().postEvent(new MENetworkCraftingPatternChange(this, getProxy().getNode()));
        } catch (GridAccessException ignored) {
        }
    }

    @Override
    public String getName() {
        return Objects.requireNonNull(getItemFromTile(this)).getUnlocalizedName();
    }

    @Nullable
    @Override
    protected abstract ItemStack getItemFromTile(Object obj);
}
