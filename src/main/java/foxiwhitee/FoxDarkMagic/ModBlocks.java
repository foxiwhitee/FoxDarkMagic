package foxiwhitee.FoxDarkMagic;

import foxiwhitee.FoxDarkMagic.blocks.BlockMatterDistorter;
import foxiwhitee.FoxDarkMagic.blocks.BlockStabilizer;
import foxiwhitee.FoxDarkMagic.config.ContentConfig;
import foxiwhitee.FoxDarkMagic.item.block.ItemBlockMatterDistorter;
import foxiwhitee.FoxDarkMagic.item.block.ItemBlockStabilizer;
import foxiwhitee.FoxDarkMagic.tile.TileMatterDistorter;
import foxiwhitee.FoxDarkMagic.tile.TileStabilizer;
import foxiwhitee.FoxLib.client.render.StaticRender;
import foxiwhitee.FoxLib.registries.RegisterUtils;
import net.minecraft.block.Block;

public class ModBlocks {
    public static final Block stabilizer = new BlockStabilizer("stabilizer");

    @StaticRender(modID = DarkCore.MODID, tile = TileMatterDistorter.class,
        model = "models/matterDistorter.obj", texture = "textures/blocks/matterDistorter.png")
    public static final Block matterDistorter = new BlockMatterDistorter("matterDistorter");

    public static void registerBlocks() {
        if (ContentConfig.enableStabilizer) {
            RegisterUtils.registerBlock(stabilizer, ItemBlockStabilizer.class);
            RegisterUtils.registerTile(TileStabilizer.class);
        }
        if (ContentConfig.enableMatterDistorter) {
            RegisterUtils.registerBlock(matterDistorter, ItemBlockMatterDistorter.class);
            RegisterUtils.registerTile(TileMatterDistorter.class);
        }
    }
}
