package foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.assemblers.arcane;

import cpw.mods.fml.client.registry.RenderingRegistry;
import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.arcane.TileUltimateArcaneAssembler;

public class BlockUltimateArcaneAssembler extends BlockArcaneAssembler {
    private static final int renderId = RenderingRegistry.getNextAvailableRenderId();

    public BlockUltimateArcaneAssembler(String name) {
        super(name, TileUltimateArcaneAssembler.class,
            DarkConfig.assemblerArcaneUltimateSpeed, DarkConfig.assemblerArcaneUltimateStorage, DarkConfig.assemblerArcaneUltimateDiscount);
        super.renderId = renderId;
    }
}
