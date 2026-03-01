package foxiwhitee.FoxDarkMagic.mixins.tnp.crucible;

import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import com.djgiannuzz.thaumcraftneiplugin.ModItems;
import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.CrucibleRecipeHandler;
import foxiwhitee.FoxDarkMagic.integrations.nei.api.IAdvancedAspectRecipe;
import foxiwhitee.FoxDarkMagic.integrations.nei.utils.ResearchEntryDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(targets = "com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.CrucibleRecipeHandler$CrucibleCachedRecipe", remap = false)
public abstract class CrucibleCachedRecipeMixin implements IAdvancedAspectRecipe {

    @Shadow
    public PositionedStack result;
    @Shadow
    private AspectList aspects;
    @Unique
    private List<PositionedStack> foxDarkMagic$ingredients = new ArrayList<>();
    @Unique
    private boolean foxDarkMagic$show;
    @Unique
    private List<ResearchEntryDisplay> foxDarkMagic$entries;

    @Inject(
        method = "<init>(Lcom/djgiannuzz/thaumcraftneiplugin/nei/recipehandler/CrucibleRecipeHandler;Lthaumcraft/api/crafting/CrucibleRecipe;)V",
        at = @At("RETURN")
    )
    private void onConstructorReturn(CrucibleRecipeHandler handler, CrucibleRecipe recipe, CallbackInfo ci) {
        ResearchItem researchItem = ResearchCategories.getResearch(recipe.key);
        this.foxDarkMagic$entries = new ArrayList<>();
        if (researchItem != null && researchItem.key != null) {
            foxDarkMagic$entries.add(new ResearchEntryDisplay(researchItem, ThaumcraftApiHelper.isResearchComplete(Minecraft.getMinecraft().getSession().getUsername(), researchItem.key)));
        }
    }

    /**
     * @author foxiwhitee
     * @reason Adding items as aspects
     */
    @Overwrite
    protected void setAspectList(AspectList aspects) {
        this.aspects = aspects;
        if (aspects == null || aspects.size() == 0) return;

        int totalAspects = aspects.size();
        Aspect[] sortedAspects = aspects.getAspectsSortedAmount();

        int rows = (totalAspects + 2) / 3;

        final int xBase = 37;
        final int yBase = 86 - (10 * rows);

        int count = 0;

        for (int row = 0; row < rows; row++) {
            int columnsInRow = Math.min(totalAspects - (row * 3), 3);

            int rowOffsetX = (100 - columnsInRow * 20) / 2;
            int posY = yBase + (row * 20);

            for (int col = 0; col < columnsInRow; col++) {
                Aspect aspect = sortedAspects[count++];
                int amount = aspects.getAmount(aspect);
                int posX = xBase + (col * 20) + rowOffsetX;

                ItemStack stack = new ItemStack(ModItems.itemAspect, amount, 1);
                ItemAspect.setAspect(stack, aspect);

                this.foxDarkMagic$ingredients.add(new PositionedStack(stack, posX, posY, false));
            }
        }
    }

    /**
     * @author foxiwhitee
     * @reason Add an item to the list
     */
    @Overwrite
    protected void setIngredient(Object in) {
        if (in != null && NEIServerUtils.extractRecipeItems(in).length > 0) {
            PositionedStack stack = new PositionedStack(in, 56, 30, false);
            stack.setMaxSize(1);
            this.foxDarkMagic$ingredients.add(stack);
        }
    }

    @Redirect(
        method = "setResult(Lnet/minecraft/item/ItemStack;)V",
        at = @At(
            value = "NEW",
            target = "(Ljava/lang/Object;IIZ)Lcodechicken/nei/PositionedStack;"
        )
    )
    private PositionedStack redirectResultStack(Object out, int x, int y, boolean expendable) {
        return new PositionedStack(out, 77, y, expendable);
    }

    @Override
    public boolean foxDarkMagic$mustShowCraft() {
        return foxDarkMagic$show;
    }

    @Override
    public void foxDarkMagic$setMustShowCraft(boolean mustShowCraft) {
        this.foxDarkMagic$show = mustShowCraft;
    }

    @Override
    public List<ResearchEntryDisplay> foxDarkMagic$getEntries() {
        return foxDarkMagic$entries;
    }

    /**
     * @author foxiwhitee
     * @reason We do it for everyone
     */
    @Overwrite
    public void computeVisuals() {
        this.foxDarkMagic$ingredients.forEach(PositionedStack::generatePermutations);
    }

    /**
     * @author foxiwhitee
     * @reason Input and aspects in one list as items
     */
    @Overwrite
    public List<PositionedStack> getIngredients() {
        if (!foxDarkMagic$show) {
            return Collections.emptyList();
        }
        return foxDarkMagic$ingredients;
    }

    /**
     * @author foxiwhitte
     * @reason The old "ingredient" change is no longer used
     */
    @Overwrite
    public boolean isValid() {
        return !this.foxDarkMagic$ingredients.isEmpty() && this.result != null;
    }

    /**
     * @author foxiwhitee
     * @reason Not necessary because there are aspects in the ingredients
     */
    @Overwrite
    public PositionedStack getIngredient() {
        return null;
    }
}
