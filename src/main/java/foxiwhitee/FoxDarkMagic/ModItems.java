package foxiwhitee.FoxDarkMagic;

import foxiwhitee.FoxDarkMagic.config.ContentConfig;
import foxiwhitee.FoxDarkMagic.item.ItemInfinityStorageUpgrade;
import foxiwhitee.FoxDarkMagic.item.ItemStackUpgrade;
import foxiwhitee.FoxLib.registries.RegisterUtils;
import net.minecraft.item.Item;

public class ModItems {
    public static final Item stackUpgrade = new ItemStackUpgrade("stackUpgrade");
    public static final Item infinityStorageUpgrade = new ItemInfinityStorageUpgrade("infinityStorageUpgrade");

    public static void registerItems() {
        if (ContentConfig.enableStackUpgrade) {
            RegisterUtils.registerItem(stackUpgrade);
        }
        if (ContentConfig.enableInfinityStorage) {
            RegisterUtils.registerItem(infinityStorageUpgrade);
        }
    }
}
