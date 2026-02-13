package foxiwhitee.FoxDarkMagic.item.block;

import foxiwhitee.FoxDarkMagic.ModBlocks;
import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxLib.items.ModItemBlock;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockStabilizer extends ModItemBlock {
    public ItemBlockStabilizer(Block b) {
        super(b);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer p_77624_2_, List<String> list, boolean p_77624_4_) {
        if (DarkConfig.enableTooltips) {
            if (isBlock(ModBlocks.stabilizer)) {
                list.add(LocalizationUtils.localize("tooltip.stabilizer.description.1"));
                list.add(LocalizationUtils.localize("tooltip.stabilizer.description.2"));
            }
        }
    }
}
