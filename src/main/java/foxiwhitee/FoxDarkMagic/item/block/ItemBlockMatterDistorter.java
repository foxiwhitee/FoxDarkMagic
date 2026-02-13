package foxiwhitee.FoxDarkMagic.item.block;

import foxiwhitee.FoxDarkMagic.ModBlocks;
import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxLib.items.ModItemBlock;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockMatterDistorter extends ModItemBlock {
    public ItemBlockMatterDistorter(Block b) {
        super(b);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer p_77624_2_, List<String> list, boolean p_77624_4_) {
        if (DarkConfig.enableTooltips) {
            if (isBlock(ModBlocks.matterDistorter)) {
                list.add(LocalizationUtils.localize("tooltip.matterDistorter.description.1", DarkConfig.matterDistorterGenerate, DarkConfig.matterDistorterConsume));
                list.add(LocalizationUtils.localize("tooltip.matterDistorter.description.2", DarkConfig.matterDistorterRange));
            }
        }
    }
}
