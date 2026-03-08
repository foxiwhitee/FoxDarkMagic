package foxiwhitee.FoxDarkMagic.integrations.appeng.item.patterns;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import foxiwhitee.FoxDarkMagic.integrations.appeng.api.IEssentiaPatternHelper;
import foxiwhitee.FoxDarkMagic.integrations.appeng.helpers.EssentiaPatternHelper;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import thaumicenergistics.common.storage.AEEssentiaStack;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class ItemEncodedEssentiaPattern extends AbstractItemEncodedPattern {

    public ItemEncodedEssentiaPattern(String name) {
        super(name);
    }

    @Override
    public ICraftingPatternDetails getPatternForItem(ItemStack is, World w) {
        try {
            return new EssentiaPatternHelper(is);
        } catch (final Throwable t) {
            return null;
        }
    }

    @Override
    protected boolean isRecipeBroken(ICraftingPatternDetails details) {
        return !(details instanceof IEssentiaPatternHelper helper) || helper.getAspects().length < 1;
    }

    @Override
    protected void addAdditionallyText(List<String> aspects, ICraftingPatternDetails details) {
        if (details instanceof IEssentiaPatternHelper helper) {
            aspects.add(EnumChatFormatting.DARK_PURPLE + LocalizationUtils.localize("tooltip.pattern.aspects"));
            for (AEEssentiaStack aspect : helper.getAspects()) {

                String itemCountText = NumberFormat.getNumberInstance(locale).format(aspect.getStackSize());
                aspects.add("   " + EnumChatFormatting.WHITE
                    + itemCountText
                    + EnumChatFormatting.RESET
                    + EnumChatFormatting.LIGHT_PURPLE
                    + " "
                    + aspect.getAspect().getName());
            }
        }
    }

    protected boolean addInputInformation(final IAEStack<?>[] inItems, final List<String> in, String ingredients) {
        List<IAEStack<?>> filteredItems = new ArrayList<>();
        for (IAEStack<?> stack : inItems) {
            if (stack instanceof IAEItemStack) {
                filteredItems.add(stack);
            }
        }
        return addInformation(filteredItems.toArray(new IAEStack[0]), in, ingredients, EnumChatFormatting.GREEN);
    }
}
