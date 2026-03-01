package foxiwhitee.FoxDarkMagic.integrations.nei.api;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import foxiwhitee.FoxDarkMagic.integrations.nei.utils.ResearchEntryDisplay;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.AspectList;

import java.util.Collection;
import java.util.List;

public interface IAdvancedAspectRecipe {
    boolean foxDarkMagic$mustShowCraft();
    void foxDarkMagic$setMustShowCraft(boolean mustShowCraft);
    List<ResearchEntryDisplay> foxDarkMagic$getEntries();
    boolean isValid();
    AspectList getAspectList();
    List<PositionedStack> getIngredients();

    default void foxDarkMagic$setIngredientPermutation(Collection<PositionedStack> ingredients, ItemStack ingredient) {
        if (ingredient.getItem() instanceof ItemAspect) return;
        for (PositionedStack stack : ingredients) {
            for (int i = 0; i < stack.items.length; i++) {
                if (NEIServerUtils.areStacksSameTypeCrafting(ingredient, stack.items[i])) {
                    stack.item = stack.items[i];
                    Items.feather.setDamage(stack.item, Items.feather.getDamage(ingredient));
                    if (ingredient.hasTagCompound()) {
                        stack.item.setTagCompound((NBTTagCompound) ingredient.getTagCompound().copy());
                    }
                    stack.items = new ItemStack[] { stack.item };
                    stack.setPermutationToRender(0);
                    break;
                }
            }
        }
    }

    default void computeVisuals() {

    }
}
