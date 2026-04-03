package foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.assemblers.arcane;

import cpw.mods.fml.client.registry.RenderingRegistry;
import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.arcane.TileBasicArcaneAssembler;

public class BlockBasicArcaneAssembler extends BlockArcaneAssembler {
    private static final int renderId = RenderingRegistry.getNextAvailableRenderId();

    public BlockBasicArcaneAssembler(String name) {
        super(name, TileBasicArcaneAssembler.class,
            DarkConfig.assemblerArcaneBasicSpeed, DarkConfig.assemblerArcaneBasicStorage, DarkConfig.assemblerArcaneBasicDiscount);
        super.renderId = renderId;
    }
}
