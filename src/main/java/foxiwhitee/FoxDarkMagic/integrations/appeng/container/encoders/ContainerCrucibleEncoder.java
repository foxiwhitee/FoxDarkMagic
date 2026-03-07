package foxiwhitee.FoxDarkMagic.integrations.appeng.container.encoders;

import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders.TileUniversalPatternEncoder;
import foxiwhitee.FoxLib.container.slots.SlotFake;
import foxiwhitee.FoxLib.container.slots.SlotFiltered;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import static foxiwhitee.FoxDarkMagic.utils.Filters.EMPTY_PATTERNS;
import static foxiwhitee.FoxDarkMagic.utils.Filters.PATTERNS;

public class ContainerCrucibleEncoder extends ContainerUniversalEncoder {
    public ContainerCrucibleEncoder(EntityPlayer player, TileUniversalPatternEncoder tileEntity) {
        super(player, tileEntity);

        bindPlayerInventory(28, 142);

        addSlotToContainer(new SlotFiltered(EMPTY_PATTERNS.getFilter(), tileEntity.getInternalInventory(), 0, 163, 55));
        addSlotToContainer(new SlotFiltered(PATTERNS.getFilter(), tileEntity.getInternalInventory(), 1, 163, 101));

        addSlotToContainer(new Slot(tileEntity.getOutputInventory(), 0, 138, 78) {
            @Override
            public boolean isItemValid(ItemStack stack) {
                return false;
            }

            @Override
            public boolean canTakeStack(EntityPlayer p_82869_1_) {
                return false;
            }
        });

        addSlotToContainer(new SlotFake(tileEntity.getCraftingInventory(), 0, 71, 78));
    }
}
