package foxiwhitee.FoxDarkMagic.blocks;

import foxiwhitee.FoxDarkMagic.DarkCore;
import foxiwhitee.FoxLib.block.FoxBaseBlock;
import net.minecraft.tileentity.TileEntity;

public abstract class ThaumBlock extends FoxBaseBlock {
    public ThaumBlock(String name, Class<? extends TileEntity> tileEntityClass) {
        super(DarkCore.MODID, name);
        setTileEntityType(tileEntityClass);
        setCreativeTab(DarkCore.TAB);
    }
}
