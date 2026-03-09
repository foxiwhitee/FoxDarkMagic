package foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.arcane;

import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.integrations.appeng.AE2Integration;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class TileBasicArcaneAssembler extends TileArcaneAssembler {
    public TileBasicArcaneAssembler() {
        super(DarkConfig.assemblerArcaneBasicSpeed, 9);
    }

    @Override
    public int getMaxStoredVis() {
        return DarkConfig.assemblerArcaneBasicStorage * 100;
    }

    @Override
    public int patternsYStartPos() {
        return 86;
    }

    @Override
    public String getGuiName() {
        return "guiBasicArcaneAssembler";
    }

    @Override
    public int getBaseDiscount() {
        return DarkConfig.assemblerArcaneBasicDiscount;
    }

    @Nullable
    @Override
    protected ItemStack getItemFromTile(Object obj) {
        return new ItemStack(AE2Integration.basicArcaneAssembler);
    }
}
