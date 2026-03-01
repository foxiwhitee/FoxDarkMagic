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

    @ConfigValue(category = "Content", desc = "Enable Moolon Aspect?")
    public static boolean enableMoolonAspect = true;

    @ConfigValue(category = "Content", desc = "Enable Basic Arcane Molecular Assembler?")
    public static boolean enableBasicArcaneMolecularAssembler = true;

    @ConfigValue(category = "Content", desc = "Enable Advanced Arcane Molecular Assembler?")
    public static boolean enableAdvancedArcaneMolecularAssembler = true;

    @ConfigValue(category = "Content", desc = "Enable Hybrid Arcane Molecular Assembler?")
    public static boolean enableHybridArcaneMolecularAssembler = true;

    @ConfigValue(category = "Content", desc = "Enable Ultimate Arcane Molecular Assembler?")
    public static boolean enableUltimateArcaneMolecularAssembler = true;

    @ConfigValue(category = "Content", desc = "Enable Infusion Molecular Assembler?")
    public static boolean enableInfusionMolecularAssembler = true;

    @ConfigValue(category = "Content", desc = "Enable Crucible Molecular Assembler?")
    public static boolean enableCrucibleMolecularAssembler = true;
}
