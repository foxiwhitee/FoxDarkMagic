package foxiwhitee.FoxDarkMagic.integrations.appeng.item.patterns;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import foxiwhitee.FoxDarkMagic.integrations.appeng.api.IAspectPatternHelper;
import foxiwhitee.FoxDarkMagic.integrations.appeng.helpers.AspectPatternHelper;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;

import java.text.NumberFormat;
import java.util.List;

public class ItemEncodedArcanePattern extends AbstractItemEncodedPattern {
    public ItemEncodedArcanePattern(String name) {
        super(name);
    }

    @Override
    public ICraftingPatternDetails getPatternForItem(ItemStack is, World w) {
        try {
            return new AspectPatternHelper(is);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    protected boolean isRecipeBroken(ICraftingPatternDetails details) {
        return !(details instanceof IAspectPatternHelper helper) || helper.getAspects().size() < 1;
    }

    @Override
    protected void addAdditionallyText(List<String> aspects, ICraftingPatternDetails details) {
        if (details instanceof IAspectPatternHelper helper) {
            aspects.add(EnumChatFormatting.DARK_PURPLE + LocalizationUtils.localize("tooltip.pattern.vis"));
            for (Aspect aspect : helper.getAspects().getAspects()) {
                String itemCountText = NumberFormat.getNumberInstance(locale).format(helper.getAspects().getAmount(aspect));
                aspects.add("   " + EnumChatFormatting.WHITE
                    + itemCountText
                    + EnumChatFormatting.RESET
                    + EnumChatFormatting.LIGHT_PURPLE
                    + " "
                    + aspect.getName());
            }
        }
    }
}
