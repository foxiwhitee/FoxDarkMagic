package foxiwhitee.FoxDarkMagic.blocks;

import cpw.mods.fml.client.registry.RenderingRegistry;
import foxiwhitee.FoxDarkMagic.client.gui.GuiMatterDistorter;
import foxiwhitee.FoxDarkMagic.container.ContainerMatterDistorter;
import foxiwhitee.FoxDarkMagic.helpers.FoxEssentiaHelper;
import foxiwhitee.FoxDarkMagic.tile.TileMatterDistorter;
import foxiwhitee.FoxLib.utils.handler.SimpleGuiHandler;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

@SimpleGuiHandler(tile = TileMatterDistorter.class, container = ContainerMatterDistorter.class, gui = GuiMatterDistorter.class)
public class BlockMatterDistorter extends ThaumBlock {
    private final static int renderId = RenderingRegistry.getNextAvailableRenderId();

    public BlockMatterDistorter(String name) {
        super(name, TileMatterDistorter.class);
        setLightLevel(1);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile != null) {
            FoxEssentiaHelper.removeSource(tile);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return renderId;
    }
}
