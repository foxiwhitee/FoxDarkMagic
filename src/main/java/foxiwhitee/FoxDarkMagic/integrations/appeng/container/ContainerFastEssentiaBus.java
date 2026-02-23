package foxiwhitee.FoxDarkMagic.integrations.appeng.container;

import appeng.api.config.RedstoneMode;
import foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.client.*;
import foxiwhitee.FoxDarkMagic.integrations.appeng.parts.PartFastEssentiaBus;
import foxiwhitee.FoxLib.network.NetworkManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.common.container.ContainerWithNetworkTool;

import javax.annotation.Nonnull;
import java.util.List;

// Taken From Thaumic Energistics
public class ContainerFastEssentiaBus extends ContainerWithNetworkTool {
    private static final int NUMBER_OF_UPGRADE_SLOTS = 4;
    private static final int UPGRADE_X_POS = 187;
    private static final int UPGRADE_Y_POS = 8;
    private static final int PLAYER_INV_POSITION_Y = 102;
    private static final int HOTBAR_INV_POSITION_Y = 160;
    private final PartFastEssentiaBus bus;
    private boolean isVoidAllowed;
    private boolean isCraftingOnly;

    public ContainerFastEssentiaBus(final PartFastEssentiaBus part, final EntityPlayer player) {
        super(player);

        this.bus = part;

        this.bindPlayerInventory(player.inventory, PLAYER_INV_POSITION_Y, HOTBAR_INV_POSITION_Y);
        this.addUpgradeSlots(part.getUpgradeInventory(), NUMBER_OF_UPGRADE_SLOTS, UPGRADE_X_POS, UPGRADE_Y_POS);
        this.bindToNetworkTool(player.inventory, part.getHost().getLocation(), 0, 0);
        this.bus.addListener(this);
    }

    @Override
    protected boolean detectAndSendChangesMP(@Nonnull final EntityPlayerMP playerMP) {
        if (this.isVoidAllowed != this.bus.isVoidAllowed()) {
            this.isVoidAllowed = this.bus.isVoidAllowed();
            NetworkManager.instance.sendToPlayer(new S2CBusVoidModePacket(this.isVoidAllowed), (EntityPlayerMP) this.player);
        }

        if (this.isCraftingOnly != this.bus.isCraftingOnly()) {
            this.isCraftingOnly = this.bus.isCraftingOnly();
            NetworkManager.instance.sendToPlayer(new S2CBusCraftingModePacket(this.isCraftingOnly), (EntityPlayerMP) this.player);
        }

        return false;
    }

    @Override
    public boolean canInteractWith(final EntityPlayer player) {
        if (this.bus != null) {
            return this.bus.isPartUseableByPlayer(player);
        }
        return false;
    }

    @Override
    public void onContainerClosed(@Nonnull final EntityPlayer player) {
        if (this.bus != null) {
            this.bus.removeListener(this);
        }
    }

    public void setFilteredAspect(final List<Aspect> filteredAspects) {
        NetworkManager.instance.sendToPlayer(new S2CAspectSlotPacket(filteredAspects), (EntityPlayerMP) player);
    }

    public void setFilterSize(final byte filterSize) {
        NetworkManager.instance.sendToPlayer(new S2CBusFilterSizePacket(filterSize), (EntityPlayerMP) player);
    }

    public void setRedstoneControlled(final boolean isRedstoneControlled) {
        NetworkManager.instance.sendToPlayer(new S2CBusRedstoneControlledPacket(isRedstoneControlled), (EntityPlayerMP) player);
    }

    public void setHasCraftingCard(final boolean hasCraftingCard) {
        NetworkManager.instance.sendToPlayer(new S2CBusHasCraftingCardPacket(hasCraftingCard), (EntityPlayerMP) player);
    }

    public void setRedstoneMode(final RedstoneMode redstoneMode) {
        NetworkManager.instance.sendToPlayer(new S2CBusRedstoneModePacket(redstoneMode), (EntityPlayerMP) player);
    }

    @Override
    public ItemStack transferStackInSlot(final EntityPlayer player, final int slotNumber) {
        Slot slot = this.getSlotOrNull(slotNumber);

        if ((slot != null) && (slot.getHasStack())) {
            if ((this.bus != null) && (this.bus.addFilteredAspectFromItemstack(player, slot.getStack()))) {
                return null;
            }

            return super.transferStackInSlot(player, slotNumber);
        }

        return null;
    }
}
