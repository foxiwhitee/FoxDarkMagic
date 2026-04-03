package foxiwhitee.FoxDarkMagic.integrations.appeng.container.encoders;

import foxiwhitee.FoxLib.container.slots.SlotFake;
import foxiwhitee.FoxLib.container.slots.SlotFiltered;
import foxiwhitee.FoxLib.integration.applied.tile.TileUniversalPatternEncoder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import static foxiwhitee.FoxDarkMagic.utils.Filters.EMPTY_PATTERNS;
import static foxiwhitee.FoxDarkMagic.utils.Filters.PATTERNS;

public class ContainerArcaneEncoder extends ContainerUniversalEncoder {
    public ContainerArcaneEncoder(EntityPlayer player, TileUniversalPatternEncoder tileEntity) {
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

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                addSlotToContainer(new SlotFake(tileEntity.getCraftingInventory(), j + i * 3, 53 + j * 18, 60 + i * 18));
            }
        }
    }
}
