package foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.arcane;

import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.integrations.appeng.AE2Integration;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class TileUltimateArcaneAssembler extends TileArcaneAssembler {
    public TileUltimateArcaneAssembler() {
        super(DarkConfig.assemblerArcaneUltimateSpeed, 36);
    }

    @Override
    public int getMaxStoredVis() {
        return DarkConfig.assemblerArcaneUltimateStorage * 100;
    }

    @Override
    public int patternsYStartPos() {
        return 59;
    }

    @Override
    public int getBaseDiscount() {
        return DarkConfig.assemblerArcaneUltimateDiscount;
    }

    @Override
    public String getGuiName() {
        return "guiUltimateArcaneAssembler";
    }

    @Nullable
    @Override
    protected ItemStack getItemFromTile(Object obj) {
        return new ItemStack(AE2Integration.ultimateArcaneAssembler);
    }
}
