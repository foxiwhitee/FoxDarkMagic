package foxiwhitee.FoxDarkMagic.integrations.appeng.parts;

import appeng.api.config.Upgrades;
import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import net.minecraft.item.ItemStack;
import thaumicenergistics.common.parts.PartEssentiaImportBus;

public class PartFastEssentiaImportBus extends PartEssentiaImportBus {
    public PartFastEssentiaImportBus(ItemStack is) {
        super(is);
    }

    @Override
    public int calculateAmountToSend() {
        return DarkConfig.fastBusesBaseSpeed + (this.getInstalledUpgrades(Upgrades.SPEED) * DarkConfig.fastBusesAdditionallySpeed);
    }
}
