package foxiwhitee.FoxDarkMagic.config;

import foxiwhitee.FoxLib.config.Config;
import foxiwhitee.FoxLib.config.ConfigValue;

@Config(folder = "Fox-Mods", name = "FoxDarkMagic-Content")
public class ContentConfig {

    @ConfigValue(category = "Content", desc = "Enable Stabilizer?")
    public static boolean enableStabilizer = true;

    @ConfigValue(category = "Content", desc = "Enable Matter Distorter?")
    public static boolean enableMatterDistorter = true;

    @ConfigValue(category = "Content", desc = "Enable Singular Arcane Furnace?")
    public static boolean enableSingularArcaneFurnace = true;

    @ConfigValue(category = "Content", desc = "Enable Stack Upgrade?")
    public static boolean enableStackUpgrade = true;

    @ConfigValue(category = "Content", desc = "Enable Infinity Storage?")
    public static boolean enableInfinityStorage = true;

    @ConfigValue(category = "Content", desc = "Enable Charged Arcane Stone?")
    public static boolean enableChargedArcaneStone = true;

    @ConfigValue(category = "Content", desc = "Enable Moonstone?")
    public static boolean enableMoonstone = true;

    @ConfigValue(category = "Content", desc = "Enable Saturated Knowledge Fragment?")
    public static boolean enableSaturatedKnowledgeFragment = true;

    @ConfigValue(category = "Content", desc = "Enable Book of Knowledge?")
    public static boolean enableKnowledgeBook = true;

    @ConfigValue(category = "Content", desc = "Enable Holy Book?")
    public static boolean enableHolyBook = true;

    @ConfigValue(category = "Content", desc = "Enable Fast Buses?")
    public static boolean enableFastBuses = true;
}
