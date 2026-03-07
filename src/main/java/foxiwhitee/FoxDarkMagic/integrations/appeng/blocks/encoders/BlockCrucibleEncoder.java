package foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.encoders;

import foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.AppliedBlock;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders.TileCrucibleEncoder;

public class BlockCrucibleEncoder extends AppliedBlock {
    public BlockCrucibleEncoder(String name) {
        super(name, TileCrucibleEncoder.class);
    }

    @Override
    public String getFolder() {
        return "encoders/";
    }
}
