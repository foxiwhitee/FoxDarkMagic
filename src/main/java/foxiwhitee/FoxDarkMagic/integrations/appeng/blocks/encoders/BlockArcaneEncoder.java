package foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.encoders;

import foxiwhitee.FoxDarkMagic.blocks.ThaumBlock;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders.TileArcaneEncoder;

public class BlockArcaneEncoder extends ThaumBlock {
    public BlockArcaneEncoder(String name) {
        super(name, TileArcaneEncoder.class);
    }

    @Override
    public String getFolder() {
        return "encoders/";
    }
}
