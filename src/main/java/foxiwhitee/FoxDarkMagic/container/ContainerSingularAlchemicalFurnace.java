package foxiwhitee.FoxDarkMagic.container;

import foxiwhitee.FoxDarkMagic.tile.TileSingularAlchemicalFurnace;
import foxiwhitee.FoxDarkMagic.utils.Filters;
import foxiwhitee.FoxLib.container.FoxBaseContainer;
import foxiwhitee.FoxLib.container.slots.SlotFiltered;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerSingularAlchemicalFurnace extends FoxBaseContainer {
    private final EntityPlayer player;

    public ContainerSingularAlchemicalFurnace(EntityPlayer player, TileSingularAlchemicalFurnace tileEntity) {
        super(player, tileEntity);
        this.player = player;

        bindPlayerInventory(56, 190);

        addSlotToContainer(new SlotFiltered(Filters.STACK_UPGRADE.getFilter(), tileEntity.getUpgradesInventory(), 0, 46, 148));
        addSlotToContainer(new SlotFiltered(Filters.INF_STORAGE_UPGRADE.getFilter(), tileEntity.getUpgradesInventory(), 1, 226, 148));

        for (int i = 0; i < 7; i++) {
            addSlotToContainer(new SlotFiltered(Filters.JARS.getFilter(), tileEntity.getJarsInventory(), i, 82 + 18 * i, 148));
        }

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 5; ++j) {
                addSlotToContainer(new SlotFiltered(Filters.ITEM_WITH_ASPECT.getFilter(), tileEntity.getInternalInventory(), j + i * 5, 51 + j * 18, 59 + i * 18));
            }
        }
    }

    public EntityPlayer getPlayer() {
        return player;
    }
}
