package foxiwhitee.FoxDarkMagic.config;

import foxiwhitee.FoxLib.config.Config;
import foxiwhitee.FoxLib.config.ConfigValue;

@Config(folder = "Fox-Mods", name = "FoxDarkMagic")
public class DarkConfig {
    @ConfigValue(desc = "Enable tooltips?")
    public static boolean enableTooltips = true;


    @ConfigValue(category = "MatterDistorter", name = "ticksToUpdate", desc = "How many ticks will a block consume essence?")
    public static int matterDistorterUpdateTicks = 5;

    @ConfigValue(category = "MatterDistorter", name = "range", desc = "Radius in which the block consumes essence")
    public static int matterDistorterRange = 3;

    @ConfigValue(category = "MatterDistorter", name = "consume", desc = "The number of aspects that a block consumes at a time and that it needs for generation in a node")
    public static int matterDistorterConsume = 10;

    @ConfigValue(category = "MatterDistorter", name = "generate", desc = "Number of aspects that generate a block in a node")
    public static int matterDistorterGenerate = 1;


    @ConfigValue(category = "Furnace", name = "ticksNeed", desc = "Essence melting speed")
    public static int singularArcaneFurnaceTicksNeed = 100;

    @ConfigValue(category = "Furnace", name = "storage", desc = "Maximum storage amount of one essence")
    public static int singularArcaneFurnaceStorage = 42_949_672;
}
