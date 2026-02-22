package foxiwhitee.FoxDarkMagic;

import foxiwhitee.FoxDarkMagic.config.ContentConfig;
import foxiwhitee.FoxDarkMagic.item.*;
import foxiwhitee.FoxLib.registries.RegisterUtils;
import net.minecraft.item.Item;

public class ModItems {
    public static final Item stackUpgrade = new ItemStackUpgrade("stackUpgrade");
    public static final Item infinityStorageUpgrade = new ItemInfinityStorageUpgrade("infinityStorageUpgrade");
    public static final Item moonstone = new ItemMoonstone("moonstone");
    public static final Item saturatedKnowledgeFragment = new ItemSaturatedKnowledgeFragment("saturatedKnowledgeFragment");
    public static final Item knowledgeBook = new ItemKnowledgeBook("knowledgeBook");
    public static final Item holyBook = new ItemHolyBook("holyBook");

    public static void registerItems() {
        if (ContentConfig.enableStackUpgrade) {
            RegisterUtils.registerItem(stackUpgrade);
        }
        if (ContentConfig.enableInfinityStorage) {
            RegisterUtils.registerItem(infinityStorageUpgrade);
        }
        if (ContentConfig.enableMoonstone) {
            RegisterUtils.registerItem(moonstone);
        }
        if (ContentConfig.enableSaturatedKnowledgeFragment) {
            RegisterUtils.registerItem(saturatedKnowledgeFragment);
        }
        if (ContentConfig.enableKnowledgeBook) {
            RegisterUtils.registerItem(knowledgeBook);
        }
        if (ContentConfig.enableHolyBook) {
            RegisterUtils.registerItem(holyBook);
        }
    }
}
