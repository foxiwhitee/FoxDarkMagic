package foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.encoders;

import foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.AppliedBlock;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders.TileArcaneEncoder;

public class BlockArcaneEncoder extends AppliedBlock {
    public BlockArcaneEncoder(String name) {
        super(name, TileArcaneEncoder.class);
    }

    @Override
    public String getFolder() {
        return "encoders/";
    }
}
