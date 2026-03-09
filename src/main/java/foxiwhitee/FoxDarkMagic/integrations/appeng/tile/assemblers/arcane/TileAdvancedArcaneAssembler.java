package foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.arcane;

import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.integrations.appeng.AE2Integration;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class TileAdvancedArcaneAssembler extends TileArcaneAssembler {
    public TileAdvancedArcaneAssembler() {
        super(DarkConfig.assemblerArcaneAdvancedSpeed, 18);
    }

    @Override
    public int getMaxStoredVis() {
        return DarkConfig.assemblerArcaneAdvancedStorage * 100;
    }

    @Override
    public int patternsYStartPos() {
        return 77;
    }

    @Override
    public String getGuiName() {
        return "guiAdvancedArcaneAssembler";
    }

    @Override
    public int getBaseDiscount() {
        return DarkConfig.assemblerArcaneAdvancedDiscount;
    }

    @Nullable
    @Override
    protected ItemStack getItemFromTile(Object obj) {
        return new ItemStack(AE2Integration.advancedArcaneAssembler);
    }
}
