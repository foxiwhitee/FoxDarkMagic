package foxiwhitee.FoxDarkMagic;

import foxiwhitee.FoxDarkMagic.blocks.BlockStabilizer;
import foxiwhitee.FoxDarkMagic.config.ContentConfig;
import foxiwhitee.FoxDarkMagic.tile.TileStabilizer;
import foxiwhitee.FoxLib.registries.RegisterUtils;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

public class ModBlocks {
    public static final Block stabilizer = new BlockStabilizer("stabilizer");

    public static void registerBlocks() {
        if (ContentConfig.enableStabilizer) {
            RegisterUtils.registerBlock(stabilizer);
            RegisterUtils.registerTile(TileStabilizer.class);
        }
    }

}
