package foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers;

import appeng.items.misc.ItemEncodedPattern;
import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.integrations.appeng.AE2Integration;
import foxiwhitee.FoxDarkMagic.integrations.appeng.item.patterns.ItemEncodedInfusionPattern;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class TileInfusionAssembler extends TileEssentiaAssembler {
    public TileInfusionAssembler() {
        super(DarkConfig.assemblerInfusionSpeed);
    }

    @Override
    protected Class<? extends ItemEncodedPattern> getPattenClass() {
        return ItemEncodedInfusionPattern.class;
    }

    @Nullable
    @Override
    protected ItemStack getItemFromTile(Object obj) {
        return new ItemStack(AE2Integration.infusionAssembler);
    }
}
