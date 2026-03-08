package foxiwhitee.FoxDarkMagic.integrations.appeng.container.assemblers;

import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.TileEssentiaAssembler;
import foxiwhitee.FoxDarkMagic.utils.Filters;
import foxiwhitee.FoxLib.container.FoxBaseContainer;
import foxiwhitee.FoxLib.container.slots.SlotFiltered;
import net.minecraft.entity.player.EntityPlayer;

import static foxiwhitee.FoxDarkMagic.integrations.appeng.utils.FiltersAE2.ABRAHAM_SEAL;

public class ContainerEssetiaAssembler extends FoxBaseContainer {
    public ContainerEssetiaAssembler(EntityPlayer player, TileEssentiaAssembler tileEntity) {
        super(player, tileEntity);

        bindPlayerInventory(27, 164);

        addSlotToContainer(new SlotFiltered(ABRAHAM_SEAL.getFilter(), tileEntity.getUpgradeInventory(), 0, 107, 135));

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new SlotFiltered(Filters.PATTERNS.getFilter(), tileEntity.getInternalInventory(), j + i * 9, 35 + j * 18, 59 + i * 18));
            }
        }
    }
}
