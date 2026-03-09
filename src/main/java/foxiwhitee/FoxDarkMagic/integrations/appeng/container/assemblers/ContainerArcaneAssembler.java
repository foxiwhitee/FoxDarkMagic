package foxiwhitee.FoxDarkMagic.integrations.appeng.container.assemblers;

import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.arcane.TileArcaneAssembler;
import foxiwhitee.FoxDarkMagic.utils.Filters;
import foxiwhitee.FoxLib.container.FoxBaseContainer;
import foxiwhitee.FoxLib.container.slots.SlotFiltered;
import net.minecraft.entity.player.EntityPlayer;
import thaumicenergistics.common.container.slot.SlotArmor;

import static foxiwhitee.FoxDarkMagic.integrations.appeng.utils.FiltersAE2.*;

public class ContainerArcaneAssembler extends FoxBaseContainer {
    public ContainerArcaneAssembler(EntityPlayer player, TileArcaneAssembler tileEntity) {
        super(player, tileEntity);

        bindPlayerInventory(38, 177);

        addSlotToContainer(new SlotFiltered(ABRAHAM_SEAL.getFilter(), tileEntity.getUpgradeInventory(), 0, 183, 135));
        addSlotToContainer(new SlotFiltered(WAND.getFilter(), tileEntity.getWandInventory(), 0, 161, 135));

        for (int index = 0; index < 4; index++) {
            this.addSlotToContainer(new SlotArmor(tileEntity.getArmorInventory(), index, 201, 59 + (index * 18), index, true));
        }

        for (int i = 0; i < tileEntity.rows(); ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new SlotFiltered(Filters.PATTERNS.getFilter(), tileEntity.getInternalInventory(), j + i * 9, 35 + j * 18, tileEntity.patternsYStartPos() + i * 18));
            }
        }
    }
}
