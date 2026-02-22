package foxiwhitee.FoxDarkMagic.blocks;

import cpw.mods.fml.client.registry.RenderingRegistry;
import foxiwhitee.FoxDarkMagic.tile.TileSingularAlchemicalFurnace;

public class BlockSingularAlchemicalFurnace extends ThaumBlock {
    private final static int renderId = RenderingRegistry.getNextAvailableRenderId();

    public BlockSingularAlchemicalFurnace(String name) {
        super(name, TileSingularAlchemicalFurnace.class);
        setLightLevel(1);
        super.renderId = renderId;
        setBlockTextureName("thaumcraft:arcane_stone");
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
