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


    @ConfigValue(category = "Assemblers.Arcane.Basic", name = "speed", desc = "Speed of creating items at a time")
    public static long assemblerArcaneBasicSpeed = 128;

    @ConfigValue(category = "Assemblers.Arcane.Basic", name = "storage", max = "21474836", desc = "How many of each aspect does the block hold?")
    public static int assemblerArcaneBasicStorage = 150;

    @ConfigValue(category = "Assemblers.Arcane.Basic", name = "discount", min = "0", max = "100", desc = "Number of % of axes that will be used according to the standard")
    public static int assemblerArcaneBasicDiscount = 75;


    @ConfigValue(category = "Assemblers.Arcane.Advanced", name = "speed", desc = "Speed of creating items at a time")
    public static long assemblerArcaneAdvancedSpeed = 512;

    @ConfigValue(category = "Assemblers.Arcane.Advanced", name = "storage", max = "21474836", desc = "How many of each aspect does the block hold?")
    public static int assemblerArcaneAdvancedStorage = 960;

    @ConfigValue(category = "Assemblers.Arcane.Advanced", name = "discount", min = "0", max = "100", desc = "Number of % of axes that will be used according to the standard")
    public static int assemblerArcaneAdvancedDiscount = 70;


    @ConfigValue(category = "Assemblers.Arcane.Hybrid", name = "speed", desc = "Speed of creating items at a time")
    public static long assemblerArcaneHybridSpeed = 2048;

    @ConfigValue(category = "Assemblers.Arcane.Hybrid", name = "storage", max = "21474836", desc = "How many of each aspect does the block hold?")
    public static int assemblerArcaneHybridStorage = 2048;

    @ConfigValue(category = "Assemblers.Arcane.Hybrid", name = "discount", min = "0", max = "100", desc = "Number of % of axes that will be used according to the standard")
    public static int assemblerArcaneHybridDiscount = 65;


    @ConfigValue(category = "Assemblers.Arcane.Ultimate", name = "speed", desc = "Speed of creating items at a time")
    public static long assemblerArcaneUltimateSpeed = 32768;

    @ConfigValue(category = "Assemblers.Arcane.Ultimate", name = "storage", max = "21474836", desc = "How many of each aspect does the block hold?")
    public static int assemblerArcaneUltimateStorage = 16384;

    @ConfigValue(category = "Assemblers.Arcane.Ultimate", name = "discount", min = "0", max = "100", desc = "Number of % of axes that will be used according to the standard")
    public static int assemblerArcaneUltimateDiscount = 60;


    @ConfigValue(category = "Assemblers.Crucible", name = "speed", desc = "Speed of creating items at a time")
    public static long assemblerCrucibleSpeed = 4096;

    @ConfigValue(category = "Assemblers.Infusion", name = "speed", desc = "Speed of creating items at a time")
    public static long assemblerInfusionSpeed = 2048;

}
