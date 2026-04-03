package foxiwhitee.FoxDarkMagic.item;

import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxLib.items.ModItemBlock;
import net.minecraft.block.Block;

public class AllItemBlock extends ModItemBlock {
    public AllItemBlock(Block b) {
        super(b);
        this.enableTooltips = DarkConfig.enableTooltips;
    }
}
