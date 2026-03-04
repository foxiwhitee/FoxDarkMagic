package foxiwhitee.FoxDarkMagic.integrations.appeng.parts;

import appeng.api.config.Upgrades;
import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import net.minecraft.item.ItemStack;
import thaumicenergistics.common.parts.PartEssentiaExportBus;

public class PartFastEssentiaExportBus extends PartEssentiaExportBus {
    public PartFastEssentiaExportBus(ItemStack is) {
        super(is);
    }

    @Override
    public int calculateAmountToSend() {
        return DarkConfig.fastBusesBaseSpeed + (this.getInstalledUpgrades(Upgrades.SPEED) * DarkConfig.fastBusesAdditionallySpeed);
    }
}
