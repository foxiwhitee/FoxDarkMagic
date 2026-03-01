package foxiwhitee.FoxDarkMagic.integrations.appeng.item.patterns;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import foxiwhitee.FoxDarkMagic.integrations.appeng.helpers.AspectPatternHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemEncodedArcanePattern extends AbstractItemEncodedPattern {
    public ItemEncodedArcanePattern(String name) {
        super(name);
    }

    @Override
    public ICraftingPatternDetails getPatternForItem(ItemStack is, World w) {
        try {
            return new AspectPatternHelper(is, w);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    protected String aspectsLocalizationKey() {
        return "tooltip.pattern.vis";
    }
}
