package foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.assemblers.arcane;

import cpw.mods.fml.client.registry.RenderingRegistry;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.arcane.TileAdvancedArcaneAssembler;

public class BlockAdvancedArcaneAssembler extends BlockArcaneAssembler {
    private static final int renderId = RenderingRegistry.getNextAvailableRenderId();

    public BlockAdvancedArcaneAssembler(String name) {
        super(name, TileAdvancedArcaneAssembler.class);
        super.renderId = renderId;
    }
}
