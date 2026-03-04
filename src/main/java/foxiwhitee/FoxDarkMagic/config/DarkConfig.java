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


    @ConfigValue(category = "ChargedArcaneStone", name = "removeBlock", desc = "Should the block be removed after the ritual?")
    public static boolean chargedArcaneStoneRemoveAfterRitual = false;


    @ConfigValue(category = "SaturatedKnowledgeFragmentAspects", name = "aspects", desc = "How many aspects will a player receive for 1 item?")
    public static short saturatedKnowledgeFragmentAspects = 25;


    @ConfigValue(category = "FastBuses", name = "baseSpeed", desc = "Base speed of fast import/export essence tires")
    public static int fastBusesBaseSpeed = 512;

    @ConfigValue(category = "FastBuses", name = "additionallySpeed", desc = "Additional speed per boost card for fast essence import/export tiress")
    public static int fastBusesAdditionallySpeed = 2048;


    @ConfigValue(category = "NEI.Recipes.Crucible", name = "shouldShow", desc = "Is it necessary to show the recipe even if it is not studied?")
    public static boolean shouldShowCrucibleRecipes = false;

    @ConfigValue(category = "NEI.Recipes.Arcane", name = "shouldShow", desc = "Is it necessary to show the recipe even if it is not studied?")
    public static boolean shouldShowArcaneRecipes = false;

    @ConfigValue(category = "NEI.Recipes.Infusion", name = "shouldShow", desc = "Is it necessary to show the recipe even if it is not studied?")
    public static boolean shouldShowInfusionRecipes = false;

}
