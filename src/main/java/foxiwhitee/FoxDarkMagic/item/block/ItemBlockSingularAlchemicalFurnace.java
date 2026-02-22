package foxiwhitee.FoxDarkMagic.item.block;

import foxiwhitee.FoxDarkMagic.ModBlocks;
import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxLib.items.ModItemBlock;
import foxiwhitee.FoxLib.utils.helpers.EnergyUtility;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockSingularAlchemicalFurnace extends ModItemBlock {
    public ItemBlockSingularAlchemicalFurnace(Block b) {
        super(b);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
        if (DarkConfig.enableTooltips) {
            if (isBlock(ModBlocks.singularAlchemicalFurnace)) {
                list.add(LocalizationUtils.localize("tooltip.furnace.description.1"));
                list.add(LocalizationUtils.localize("tooltip.furnace.description.2", EnergyUtility.formatNumber(DarkConfig.singularArcaneFurnaceStorage)));
                list.add(LocalizationUtils.localize("tooltip.furnace.description.3"));
            }
        }
    }
}
