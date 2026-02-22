package foxiwhitee.FoxDarkMagic.blocks;

import foxiwhitee.FoxDarkMagic.DarkCore;
import foxiwhitee.FoxLib.block.FoxTileBlock;
import net.minecraft.tileentity.TileEntity;

public abstract class ThaumBlock extends FoxTileBlock {
    public ThaumBlock(String name, Class<? extends TileEntity> tileEntityClass) {
        super(DarkCore.MODID, name, tileEntityClass);
        setCreativeTab(DarkCore.TAB);
    }
}
