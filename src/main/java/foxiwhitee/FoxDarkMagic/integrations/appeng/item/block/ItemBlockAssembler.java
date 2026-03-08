package foxiwhitee.FoxDarkMagic.integrations.appeng.item.block;

import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.integrations.appeng.AE2Integration;
import foxiwhitee.FoxLib.items.ModItemBlock;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockAssembler extends ModItemBlock {
    public ItemBlockAssembler(Block b) {
        super(b);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
        if (DarkConfig.enableTooltips) {
            if (isBlock(AE2Integration.crucibleAssembler)) {
                list.add(LocalizationUtils.localize("tooltip.assembler.description", DarkConfig.assemblerCrucibleSpeed));
            } else if (isBlock(AE2Integration.infusionAssembler)) {
                list.add(LocalizationUtils.localize("tooltip.assembler.description", DarkConfig.assemblerInfusionSpeed));
            }
        }
    }
}
