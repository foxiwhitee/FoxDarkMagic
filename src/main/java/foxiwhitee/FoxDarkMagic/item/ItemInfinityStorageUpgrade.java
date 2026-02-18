package foxiwhitee.FoxDarkMagic.item;

import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemInfinityStorageUpgrade extends DefaultItem {
    public ItemInfinityStorageUpgrade(String name) {
        super(name, "upgrades/", 1);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer p_77624_2_, List<String> list, boolean p_77624_4_) {
        if (DarkConfig.enableTooltips) {
            list.add(LocalizationUtils.localize("tooltip.infStorageDesc"));
        }
    }
}
