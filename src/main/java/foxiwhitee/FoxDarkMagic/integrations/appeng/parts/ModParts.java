package foxiwhitee.FoxDarkMagic.integrations.appeng.parts;

import appeng.api.config.Upgrades;
import foxiwhitee.FoxDarkMagic.DarkCore;
import foxiwhitee.FoxDarkMagic.integrations.appeng.AE2Integration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

// Taken From Thaumic Energistics
public enum ModParts {

    EssentiaImportBus("aeparts.essentia.FastImportBus", PartFastEssentiaImportBus.class,
        DarkCore.MODID + ".group.essentia.transport", generatePair(Upgrades.CAPACITY, 2),
        generatePair(Upgrades.REDSTONE, 1), generatePair(Upgrades.SPEED, 4)),

    EssentiaExportBus("aeparts.essentia.FastExportBus", PartFastEssentiaExportBus.class,
        DarkCore.MODID + ".group.essentia.transport", generatePair(Upgrades.CAPACITY, 2),
        generatePair(Upgrades.REDSTONE, 1), generatePair(Upgrades.SPEED, 4), generatePair(Upgrades.CRAFTING, 1));

    public static final ModParts[] VALUES = ModParts.values();
    private final String unlocalizedName;
    private final Class<? extends BasePart> partClass;
    private final String groupName;
    private final Map<Upgrades, Integer> upgrades = new HashMap<>();

    ModParts(final String unlocalizedName, final Class<? extends BasePart> partClass,
                        final String groupName) {
        this.unlocalizedName = unlocalizedName;
        this.partClass = partClass;
        this.groupName = groupName;
    }

    @SafeVarargs
    ModParts(final String unlocalizedName, final Class<? extends BasePart> partClass,
             final String groupName, final Pair<Upgrades, Integer>... upgrades) {
        this(unlocalizedName, partClass, groupName);

        for (Pair<Upgrades, Integer> pair : upgrades) {
            this.upgrades.put(pair.getKey(), pair.getValue());
        }
    }

    private static Pair<Upgrades, Integer> generatePair(final Upgrades upgrade, final int maximum) {
        return new ImmutablePair<>(upgrade, maximum);
    }

    public static ModParts getPartFromDamageValue(final ItemStack itemStack) {
        int clamped = MathHelper.clamp_int(itemStack.getItemDamage(), 0, ModParts.VALUES.length - 1);

        return ModParts.VALUES[clamped];
    }

    public BasePart createPartInstance(final ItemStack itemStack) throws Exception {
        BasePart part = this.partClass.getDeclaredConstructor().newInstance();

        part.setupPartFromItem(itemStack);

        return part;
    }

    public String getGroupName() {
        return this.groupName;
    }

    public ItemStack getStack() {
        return new ItemStack(AE2Integration.parts, 1, ordinal());
    }

    public String getUnlocalizedName() {
        return this.unlocalizedName;
    }

    public Map<Upgrades, Integer> getUpgrades() {
        return this.upgrades;
    }
}
