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

    @ConfigValue(category = "Furnace", name = "ignoreStorageBus", desc = "If true then Essentia Storage Bus will not work with this block")
    public static boolean singularArcaneFurnaceIgnoreStorageBus = false;


    @ConfigValue(category = "ChargedArcaneStone", name = "removeBlock", desc = "Should the block be removed after the ritual?")
    public static boolean chargedArcaneStoneRemoveAfterRitual = false;


    @ConfigValue(category = "SaturatedKnowledgeFragmentAspects", name = "aspects", desc = "How many aspects will a player receive for 1 item?")
    public static short saturatedKnowledgeFragmentAspects = 25;

}
