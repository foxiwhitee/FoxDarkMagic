package foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.assemblers.arcane;

import cpw.mods.fml.client.registry.RenderingRegistry;
import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.arcane.TileHybridArcaneAssembler;

public class BlockHybridArcaneAssembler extends BlockArcaneAssembler {
    private static final int renderId = RenderingRegistry.getNextAvailableRenderId();

    public BlockHybridArcaneAssembler(String name) {
        super(name, TileHybridArcaneAssembler.class,
            DarkConfig.assemblerArcaneHybridSpeed, DarkConfig.assemblerArcaneHybridStorage, DarkConfig.assemblerArcaneHybridDiscount);
        super.renderId = renderId;
    }
}
