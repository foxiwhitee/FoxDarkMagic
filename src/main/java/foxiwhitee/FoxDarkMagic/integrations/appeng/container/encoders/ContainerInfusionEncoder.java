package foxiwhitee.FoxDarkMagic.integrations.appeng.container.encoders;

import foxiwhitee.FoxLib.container.slots.SlotFake;
import foxiwhitee.FoxLib.container.slots.SlotFiltered;
import foxiwhitee.FoxLib.integration.applied.tile.TileUniversalPatternEncoder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import static foxiwhitee.FoxDarkMagic.utils.Filters.EMPTY_PATTERNS;
import static foxiwhitee.FoxDarkMagic.utils.Filters.PATTERNS;

public class ContainerInfusionEncoder extends ContainerUniversalEncoder {
    private final SlotFake[] circle;
    private final SlotFake centralSlot;

    public ContainerInfusionEncoder(EntityPlayer player, TileUniversalPatternEncoder tileEntity) {
        super(player, tileEntity);

        bindPlayerInventory(43, 172);

        addSlotToContainer(new SlotFiltered(EMPTY_PATTERNS.getFilter(), tileEntity.getInternalInventory(), 0, 190, 70));
        addSlotToContainer(new SlotFiltered(PATTERNS.getFilter(), tileEntity.getInternalInventory(), 1, 190, 116));

        addSlotToContainer(new Slot(tileEntity.getOutputInventory(), 0, 165, 93) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            @Override
            public boolean canTakeStack(EntityPlayer p_82869_1_) {
                return false;
            }
        });

        circle = new SlotFake[16];
        addSlotToContainer(centralSlot = new SlotFake(tileEntity.getCraftingInventory(), 0, 75, 93));
        addSlotToContainer(circle[0] = new SlotFake(tileEntity.getCraftingInventory(), 1, 75, 54));

        for (int i = 2; i < tileEntity.getCraftingInventory().getSizeInventory(); i++) {
            addSlotToContainer(circle[i - 1] = new SlotFake(tileEntity.getCraftingInventory(), i, -9000, -9000));
        }
    }

    public SlotFake[] getCircle() {
        return circle;
    }

    public SlotFake getCentralSlot() {
        return centralSlot;
    }

    @Override
    public ItemStack slotClick(int slotId, int mouseButton, int modifier, EntityPlayer player) {
        if (slotId >= 0 && slotId < this.inventorySlots.size()) {
            Slot slot = this.inventorySlots.get(slotId);
            if (!(slot instanceof SlotFake) || slot.getSlotIndex() != 1) {
                return super.slotClick(slotId, mouseButton, modifier, player);
            }
            ItemStack stackInCursor = player.inventory.getItemStack();

            if (modifier == 0) {
                if (stackInCursor != null) {
                    for (int i = 15; i > 0; i--) {
                        int otherSlot = i - 1;
                        circle[i].putStack(circle[otherSlot].getStack());
                    }
                } else {
                    for (int i = 0; i < 16; i++) {
                        if (i == 15) {
                            circle[i].putStack(null);
                            continue;
                        }
                        int otherSlot = i + 1;
                        circle[i].putStack(circle[otherSlot].getStack());
                    }
                    return null;
                }
            }
        }

        return super.slotClick(slotId, mouseButton, modifier, player);
    }
}
