package foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.arcane;

import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.integrations.appeng.AE2Integration;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class TileHybridArcaneAssembler extends TileArcaneAssembler {
    public TileHybridArcaneAssembler() {
        super(DarkConfig.assemblerArcaneHybridSpeed, 27);
    }

    @Override
    public int getMaxStoredVis() {
        return DarkConfig.assemblerArcaneHybridStorage * 100;
    }

    @Override
    public int patternsYStartPos() {
        return 68;
    }

    @Override
    public int getBaseDiscount() {
        return DarkConfig.assemblerArcaneHybridDiscount;
    }

    @Override
    public String getGuiName() {
        return "guiHybridArcaneAssembler";
    }

    @Nullable
    @Override
    protected ItemStack getItemFromTile(Object obj) {
        return new ItemStack(AE2Integration.hybridArcaneAssembler);
    }
}
