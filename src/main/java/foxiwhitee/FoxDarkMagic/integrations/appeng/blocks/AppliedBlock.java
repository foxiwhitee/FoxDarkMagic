package foxiwhitee.FoxDarkMagic.integrations.appeng.blocks;

import foxiwhitee.FoxDarkMagic.blocks.ThaumBlock;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.TileAENetworkInvOrientable;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;

public class AppliedBlock extends ThaumBlock {
    public AppliedBlock(String name, Class<? extends TileEntity> tileEntityClass) {
        super(name, tileEntityClass);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int b) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileAENetworkInvOrientable invTile) {
            ArrayList<ItemStack> drops = new ArrayList<>();
            invTile.getDrops(world, x, y, z, drops);
            spawnDrops(world, x, y, z, drops);
        }
        super.breakBlock(world, x, y, z, block, b);
    }
}
