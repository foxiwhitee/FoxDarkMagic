package foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.assemblers;

import cpw.mods.fml.client.registry.RenderingRegistry;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.TileCrucibleAssembler;

public class BlockCrucibleAssembler extends BlockAssembler {
    private static final int renderId = RenderingRegistry.getNextAvailableRenderId();

    public BlockCrucibleAssembler(String name) {
        super(name, TileCrucibleAssembler.class);
        super.renderId = renderId;
    }
}
