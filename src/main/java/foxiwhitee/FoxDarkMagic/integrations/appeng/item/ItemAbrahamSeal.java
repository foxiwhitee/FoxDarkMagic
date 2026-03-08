package foxiwhitee.FoxDarkMagic.integrations.appeng.item;

import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.item.DefaultItem;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemAbrahamSeal extends DefaultItem {
    public ItemAbrahamSeal(String name) {
        super(name, "upgrades/", 1);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer p_77624_2_, List<String> list, boolean p_77624_4_) {
        if (DarkConfig.enableTooltips) {
            list.add(LocalizationUtils.localize("tooltip.abrahamSeal.description.1"));
            list.add(LocalizationUtils.localize("tooltip.abrahamSeal.description.2"));
        }
    }
}
