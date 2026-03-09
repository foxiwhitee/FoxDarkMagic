package foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.assemblers.arcane;

import cpw.mods.fml.client.registry.RenderingRegistry;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.arcane.TileBasicArcaneAssembler;

public class BlockBasicArcaneAssembler extends BlockArcaneAssembler {
    private static final int renderId = RenderingRegistry.getNextAvailableRenderId();

    public BlockBasicArcaneAssembler(String name) {
        super(name, TileBasicArcaneAssembler.class);
        super.renderId = renderId;
    }
}
