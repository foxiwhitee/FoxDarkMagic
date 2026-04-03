package foxiwhitee.FoxDarkMagic.blocks;

import cpw.mods.fml.client.registry.RenderingRegistry;
import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.helpers.FoxEssentiaHelper;
import foxiwhitee.FoxDarkMagic.tile.TileMatterDistorter;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

public class BlockMatterDistorter extends ThaumBlock {
    private final static int renderId = RenderingRegistry.getNextAvailableRenderId();

    public BlockMatterDistorter(String name) {
        super(name, TileMatterDistorter.class);
        setLightLevel(1);
        super.renderId = renderId;
        setBlockTextureName("thaumcraft:arcane_stone");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
        list.add(LocalizationUtils.localize("tooltip.matterDistorter.description.1", DarkConfig.matterDistorterGenerate, DarkConfig.matterDistorterConsume));
        list.add(LocalizationUtils.localize("tooltip.matterDistorter.description.2", DarkConfig.matterDistorterRange));
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

}
