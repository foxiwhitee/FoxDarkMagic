package foxiwhitee.FoxDarkMagic.item;

import foxiwhitee.FoxDarkMagic.DarkCore;
import net.minecraft.item.Item;

public class DefaultItem extends Item {
    protected final String name;

    public DefaultItem(String name, String folder, int maxStackSize) {
        this.setUnlocalizedName(name);
        this.setTextureName(DarkCore.MODID + ":" + folder + name);
        this.setCreativeTab(DarkCore.TAB);
        this.maxStackSize = maxStackSize;
        this.name = name;
    }

    public DefaultItem(String name, String folder) {
        this(name, folder, 64);
    }

    public DefaultItem(String name) {
        this(name, "");
    }

    public DefaultItem(String name, int maxStackSize) {
        this(name, "", maxStackSize);
    }
}
