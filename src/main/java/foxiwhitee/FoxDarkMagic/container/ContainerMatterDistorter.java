package foxiwhitee.FoxDarkMagic.container;

import foxiwhitee.FoxDarkMagic.tile.TileMatterDistorter;
import foxiwhitee.FoxDarkMagic.utils.Filters;
import foxiwhitee.FoxLib.container.FoxBaseContainer;
import foxiwhitee.FoxLib.container.slots.SlotFiltered;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerMatterDistorter extends FoxBaseContainer {
    public ContainerMatterDistorter(EntityPlayer player, TileMatterDistorter tileEntity) {
        super(player, tileEntity);

        bindPlayerInventory(45, 163);

        addSlotToContainer(new SlotFiltered(Filters.SINISTER_NODE.getFilter(), tileEntity.getInternalInventory(), 0, 125, 88));
    }
}
