package foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.encoders;

import foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.AppliedBlock;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders.TileInfusionEncoder;

public class BlockInfusionEncoder extends AppliedBlock {
    public BlockInfusionEncoder(String name) {
        super(name, TileInfusionEncoder.class);
    }

    @Override
    public String getFolder() {
        return "encoders/";
    }
}
