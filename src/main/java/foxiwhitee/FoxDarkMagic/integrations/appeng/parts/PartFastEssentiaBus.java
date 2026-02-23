package foxiwhitee.FoxDarkMagic.integrations.appeng.parts;

import appeng.api.AEApi;
import appeng.api.config.RedstoneMode;
import appeng.api.config.SecurityPermissions;
import appeng.api.definitions.IMaterials;
import appeng.api.networking.IGridNode;
import appeng.api.networking.security.MachineSource;
import appeng.api.networking.ticking.IGridTickable;
import appeng.api.networking.ticking.TickRateModulation;
import appeng.api.networking.ticking.TickingRequest;
import appeng.api.parts.PartItemStack;
import appeng.parts.automation.StackUpgradeInventory;
import appeng.parts.automation.UpgradeInventory;
import appeng.tile.inventory.IAEAppEngInventory;
import appeng.tile.inventory.InvOperation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.integrations.appeng.client.gui.GuiFastEssentiaBus;
import foxiwhitee.FoxDarkMagic.integrations.appeng.container.ContainerFastEssentiaBus;
import foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.client.S2CAspectSlotPacket;
import foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.client.S2CBusStatePacket;
import foxiwhitee.FoxLib.network.NetworkManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumicenergistics.common.integration.tc.EssentiaItemContainerHelper;
import thaumicenergistics.common.network.IAspectSlotPart;
import thaumicenergistics.common.registries.EnumCache;
import thaumicenergistics.common.utils.EffectiveSide;

import java.util.ArrayList;
import java.util.List;

// Taken From Thaumic Energistics
public abstract class PartFastEssentiaBus extends BasePart implements IGridTickable, IAspectSlotPart, IAEAppEngInventory {
    private final int baseTransferPerSecond;
    private final int additionalTransferPerSecond;
    private static final int MINIMUM_TICKS_PER_OPERATION = 10;
    private static final int MAXIMUM_TICKS_PER_OPERATION = 40;
    private final int maximumTransferPerSecond;
    private static final int MINIMUM_TRANSFER_PER_SECOND = 1;
    private static final int MAX_FILTER_SIZE = 9;
    private static final int BASE_SLOT_INDEX = 4;
    private static final int[] TIER2_INDEXS = { 0, 2, 6, 8 };
    private static final int[] TIER1_INDEXS = { 1, 3, 5, 7 };
    private static final int UPGRADE_INVENTORY_SIZE = 4;
    private static final double IDLE_POWER_DRAIN = 0.7;
    private static final RedstoneMode DEFAULT_REDSTONE_MODE = RedstoneMode.IGNORE;
    private static final String NBT_KEY_REDSTONE_MODE = "redstoneMode", NBT_KEY_FILTER_NUMBER = "AspectFilter#", NBT_KEY_UPGRADE_INV = "upgradeInventory";
    private boolean lastRedstone;
    private int[] availableFilterSlots = { BASE_SLOT_INDEX };
    private final UpgradeInventory upgradeInventory = new StackUpgradeInventory(this.associatedItem, this, UPGRADE_INVENTORY_SIZE);
    private final List<ContainerFastEssentiaBus> listeners = new ArrayList<>();
    private RedstoneMode redstoneMode = DEFAULT_REDSTONE_MODE;
    protected MachineSource asMachineSource;
    protected List<Aspect> filteredAspects = new ArrayList<>(MAX_FILTER_SIZE);
    protected IAspectContainer facingContainer;
    protected byte filterSize;
    protected byte upgradeSpeedCount = 0;
    protected boolean redstoneControlled;
    protected boolean hasCraftingCard = false;

    public PartFastEssentiaBus(final ModParts associatedPart, final SecurityPermissions... interactionPermissions) {
        super(associatedPart, interactionPermissions);
        this.baseTransferPerSecond = DarkConfig.fastBusesBaseSpeed;
        this.additionalTransferPerSecond = DarkConfig.fastBusesAdditionallySpeed;
        this.maximumTransferPerSecond = DarkConfig.fastBusesMaxSpeed;

        for (int index = 0; index < MAX_FILTER_SIZE; index++) {
            this.filteredAspects.add(null);
        }

        this.asMachineSource = new MachineSource(this);
    }

    private boolean canDoWork() {
        boolean canWork = true;

        if (this.redstoneControlled) {
            switch (this.getRedstoneMode()) {
                case HIGH_SIGNAL:
                    canWork = this.isReceivingRedstonePower();

                    break;
                case IGNORE:
                    break;

                case LOW_SIGNAL:
                    canWork = !this.isReceivingRedstonePower();

                    break;
                case SIGNAL_PULSE:
                    canWork = false;
                    break;
            }
        }

        return canWork;
    }

    private int getTransferAmountPerSecond() {
        return baseTransferPerSecond + (this.upgradeSpeedCount * additionalTransferPerSecond);
    }

    private void notifyListenersOfFilterAspectChange() {
        for (ContainerFastEssentiaBus listener : this.listeners) {
            listener.setFilteredAspect(this.filteredAspects);
        }
    }

    private void notifyListenersOfFilterSizeChange() {
        for (ContainerFastEssentiaBus listener : this.listeners) {
            listener.setFilterSize(this.filterSize);
        }
    }

    private void notifyListenersOfRedstoneControlledChange() {
        for (ContainerFastEssentiaBus listener : this.listeners) {
            listener.setRedstoneControlled(this.redstoneControlled);
        }
    }

    private void notifyListenersOfHasCraftingCardChange() {
        for (ContainerFastEssentiaBus listener : this.listeners) {
            listener.setHasCraftingCard(this.hasCraftingCard);
        }
    }

    private void notifyListenersOfRedstoneModeChange() {
        for (ContainerFastEssentiaBus listener : this.listeners) {
            listener.setRedstoneMode(this.redstoneMode);
        }
    }

    private void resizeAvailableArray() {
        this.availableFilterSlots = new int[1 + (this.filterSize * 4)];
        this.availableFilterSlots[0] = BASE_SLOT_INDEX;

        if (this.filterSize < 2) {
            for (int tier2Index : TIER2_INDEXS) {
                this.filteredAspects.set(tier2Index, null);
            }

            if (this.filterSize < 1) {
                for (int tier1Index : TIER1_INDEXS) {
                    this.filteredAspects.set(tier1Index, null);
                }
            } else {
                System.arraycopy(TIER1_INDEXS, 0, this.availableFilterSlots, 1, 4);
            }
        } else {
            System.arraycopy(TIER1_INDEXS, 0, this.availableFilterSlots, 1, 4);
            System.arraycopy(TIER2_INDEXS, 0, this.availableFilterSlots, 5, 4);
        }
    }

    private void updateUpgradeState() {
        int oldFilterSize = this.filterSize;

        this.filterSize = 0;
        this.redstoneControlled = false;
        this.upgradeSpeedCount = 0;
        hasCraftingCard = false;

        IMaterials aeMaterals = AEApi.instance().definitions().materials();

        for (int i = 0; i < this.upgradeInventory.getSizeInventory(); i++) {
            ItemStack slotStack = this.upgradeInventory.getStackInSlot(i);

            if (slotStack != null) {
                if (aeMaterals.cardCapacity().isSameAs(slotStack)) {
                    this.filterSize++;
                } else if (aeMaterals.cardRedstone().isSameAs(slotStack)) {
                    this.redstoneControlled = true;
                } else if (aeMaterals.cardSpeed().isSameAs(slotStack)) {
                    this.upgradeSpeedCount++;
                } else if (aeMaterals.cardCrafting().isSameAs(slotStack)) {
                    this.hasCraftingCard = true;
                }
            }
        }

        if (oldFilterSize != this.filterSize) {
            this.resizeAvailableArray();
        }

        if (EffectiveSide.isClientSide()) {
            return;
        }
        if (!hasCraftingCard) setCraftingOnly(false);

        this.notifyListenersOfFilterSizeChange();
        this.notifyListenersOfRedstoneControlledChange();
        this.notifyListenersOfHasCraftingCardChange();
    }

    public boolean addFilteredAspectFromItemstack(final EntityPlayer player, final ItemStack itemStack) {
        Aspect itemAspect = EssentiaItemContainerHelper.INSTANCE.getFilterAspectFromItem(itemStack);

        if (itemAspect != null) {
            if (this.filteredAspects.contains(itemAspect)) {
                return true;
            }

            for (int filterIndex : this.availableFilterSlots) {
                if (this.filteredAspects.get(filterIndex) == null) {
                    if (EffectiveSide.isServerSide()) {
                        this.setAspect(filterIndex, itemAspect, player);
                    }

                    return true;
                }
            }
        }

        return false;
    }

    public void addListener(final ContainerFastEssentiaBus container) {
        if (!this.listeners.contains(container)) {
            this.listeners.add(container);
        }
    }

    public abstract boolean aspectTransferAllowed(Aspect aspect);

    @Override
    public int cableConnectionRenderTo() {
        return 5;
    }

    public abstract boolean doWork(int transferAmount);

    @Override
    public Object getClientGuiElement(final EntityPlayer player) {
        return new GuiFastEssentiaBus(this, player);
    }

    @Override
    public void getDrops(final List<ItemStack> drops, final boolean wrenched) {
        for (int slotIndex = 0; slotIndex < UPGRADE_INVENTORY_SIZE; slotIndex++) {
            ItemStack slotStack = this.upgradeInventory.getStackInSlot(slotIndex);

            if ((slotStack != null) && (slotStack.stackSize > 0)) {
                drops.add(slotStack);
            }
        }
    }

    @Override
    public double getIdlePowerUsage() {
        return IDLE_POWER_DRAIN * (1 << upgradeSpeedCount);
    }

    @Override
    public int getLightLevel() {
        return (this.isActive() ? 4 : 0);
    }

    public RedstoneMode getRedstoneMode() {
        return this.redstoneMode;
    }

    @Override
    public Object getServerGuiElement(final EntityPlayer player) {
        return new ContainerFastEssentiaBus(this, player);
    }

    @Override
    public TickingRequest getTickingRequest(final IGridNode arg0) {
        return new TickingRequest(MINIMUM_TICKS_PER_OPERATION, MAXIMUM_TICKS_PER_OPERATION, false, false);
    }

    public UpgradeInventory getUpgradeInventory() {
        return this.upgradeInventory;
    }

    public boolean isVoidAllowed() {
        return false;
    }

    public boolean isCraftingOnly() {
        return false;
    }

    public void setCraftingOnly(boolean c) {}

    @Override
    public boolean onActivate(final EntityPlayer player, final Vec3 position) {
        boolean activated = super.onActivate(player, position);

        this.updateUpgradeState();

        return activated;
    }

    @Override
    public void onChangeInventory(final IInventory inv, final int slot, final InvOperation mc,
                                  final ItemStack removedStack, final ItemStack newStack) {
        if (inv == this.upgradeInventory) {
            this.updateUpgradeState();
        }
    }

    public void onClientRequestChangeRedstoneMode() {
        int nextOrdinal = this.redstoneMode.ordinal() + 1;

        if (nextOrdinal >= EnumCache.AE_REDSTONE_MODES.length) {
            nextOrdinal = 0;
        }

        this.redstoneMode = EnumCache.AE_REDSTONE_MODES[nextOrdinal];
        this.notifyListenersOfRedstoneModeChange();
    }

    public void onClientRequestFilterList(final EntityPlayer player) {
        NetworkManager.instance.sendToPlayer(new S2CAspectSlotPacket(this.filteredAspects), (EntityPlayerMP) player);
        NetworkManager.instance.sendToPlayer(new S2CBusStatePacket(this.redstoneMode, this.filterSize, this.redstoneControlled, hasCraftingCard, isCraftingOnly()), (EntityPlayerMP) player);
    }

    @Override
    public void onNeighborChanged() {
        if (EffectiveSide.isClientSide()) {
            return;
        }

        this.facingContainer = null;

        TileEntity tileEntity = this.getFacingTile();

        if (tileEntity instanceof IAspectContainer) {
            this.facingContainer = (IAspectContainer) tileEntity;
        }

        if (this.redstoneMode == RedstoneMode.SIGNAL_PULSE) {
            if (this.isReceivingRedstonePower() != this.lastRedstone) {
                this.lastRedstone = this.isReceivingRedstonePower();
                this.doWork(this.getTransferAmountPerSecond());
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void onReceiveFilterList(final List<Aspect> filteredAspects) {
        this.filteredAspects = filteredAspects;
    }

    @SideOnly(Side.CLIENT)
    public void onReceiveFilterSize(final byte filterSize) {
        this.filterSize = filterSize;

        this.resizeAvailableArray();
    }

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);

        if (data.hasKey(NBT_KEY_REDSTONE_MODE)) {
            this.redstoneMode = EnumCache.AE_REDSTONE_MODES[data.getInteger(NBT_KEY_REDSTONE_MODE)];
        }

        for (int index = 0; index < MAX_FILTER_SIZE; index++) {
            if (data.hasKey(NBT_KEY_FILTER_NUMBER + index)) {
                this.filteredAspects.set(index, Aspect.aspects.get(data.getString(NBT_KEY_FILTER_NUMBER + index)));
            }
        }

        if (data.hasKey(NBT_KEY_UPGRADE_INV)) {
            this.upgradeInventory.readFromNBT(data, NBT_KEY_UPGRADE_INV);

            this.updateUpgradeState();
        }
    }

    public void removeListener(final ContainerFastEssentiaBus container) {
        this.listeners.remove(container);
    }

    @Override
    public void saveChanges() {
        this.markForSave();
    }

    @Override
    public final int[] getAvailableAspectSlots() {
        return availableFilterSlots.clone();
    }

    @Override
    public final Aspect getAspect(final int index) {
        return this.filteredAspects.get(index);
    }

    @Override
    public final void setAspect(final int index, final Aspect aspect, final EntityPlayer player) {
        this.filteredAspects.set(index, aspect);
        this.notifyListenersOfFilterAspectChange();
    }

    @Override
    public TickRateModulation tickingRequest(final IGridNode node, final int ticksSinceLastCall) {
        if (this.canDoWork()) {
            int transferAmountPerSecond = this.getTransferAmountPerSecond();

            int transferAmount = (int) (transferAmountPerSecond * (ticksSinceLastCall / 20.F));

            if (transferAmount < MINIMUM_TRANSFER_PER_SECOND) {
                transferAmount = MINIMUM_TRANSFER_PER_SECOND;
            } else if (transferAmount > maximumTransferPerSecond) {
                transferAmount = maximumTransferPerSecond;
            }

            if (this.doWork(transferAmount)) {
                return TickRateModulation.URGENT;
            }
        }

        return TickRateModulation.IDLE;
    }

    @Override
    public void writeToNBT(final NBTTagCompound data, final PartItemStack saveType) {
        super.writeToNBT(data, saveType);

        if ((saveType == PartItemStack.World) || (saveType == PartItemStack.Wrench)) {
            for (int i = 0; i < MAX_FILTER_SIZE; i++) {
                Aspect aspect = this.filteredAspects.get(i);
                if (aspect != null) {
                    data.setString(NBT_KEY_FILTER_NUMBER + i, aspect.getTag());
                }
            }

            if (saveType == PartItemStack.World) {
                if (this.redstoneMode != DEFAULT_REDSTONE_MODE) {
                    data.setInteger(NBT_KEY_REDSTONE_MODE, this.redstoneMode.ordinal());
                }

                if (!this.upgradeInventory.isEmpty()) {
                    this.upgradeInventory.writeToNBT(data, NBT_KEY_UPGRADE_INV);
                }
            }
        }
    }
}
