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
}
