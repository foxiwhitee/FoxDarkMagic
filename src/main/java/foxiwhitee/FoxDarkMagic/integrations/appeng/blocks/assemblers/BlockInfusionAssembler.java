package foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.assemblers;

import cpw.mods.fml.client.registry.RenderingRegistry;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.TileInfusionAssembler;

public class BlockInfusionAssembler extends BlockAssembler {
    private static final int renderId = RenderingRegistry.getNextAvailableRenderId();

    public BlockInfusionAssembler(String name) {
        super(name, TileInfusionAssembler.class);
        super.renderId = renderId;
    }
}
