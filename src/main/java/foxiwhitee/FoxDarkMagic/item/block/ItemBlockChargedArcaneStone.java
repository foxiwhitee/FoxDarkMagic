package foxiwhitee.FoxDarkMagic.item.block;

import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxLib.items.ModItemBlock;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemBlockChargedArcaneStone extends ModItemBlock {
    public ItemBlockChargedArcaneStone(Block b) {
        super(b);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
        if (DarkConfig.enableTooltips) {
            list.add(LocalizationUtils.localize("tooltip.chargedArcaneStone"));
        }
    }
}
