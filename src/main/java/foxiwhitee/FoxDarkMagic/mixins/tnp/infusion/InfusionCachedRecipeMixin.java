package foxiwhitee.FoxDarkMagic.mixins.tnp.infusion;

import codechicken.nei.PositionedStack;
import com.djgiannuzz.thaumcraftneiplugin.ModItems;
import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.InfusionRecipeHandler;
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
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mixin(targets = "com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.InfusionRecipeHandler$InfusionCachedRecipe", remap = false)
public abstract class InfusionCachedRecipeMixin implements IAdvancedAspectRecipe {
    @Shadow
    private List<PositionedStack> ingredients;
    @Shadow
    private PositionedStack result;
    @Shadow
    private AspectList aspects;
    @Unique
    private boolean foxDarkMagic$show;
    @Unique
    private List<ResearchEntryDisplay> foxDarkMagic$entries;

    @Redirect(
        method = "<init>",
        at = @At(value = "INVOKE", target = "Lcom/djgiannuzz/thaumcraftneiplugin/nei/NEIHelper;addAspectsToIngredients(Lthaumcraft/api/aspects/AspectList;Ljava/util/List;I)V")
    )
    private void redirectAddAspects(AspectList sx, List<PositionedStack> sy, int stack) {}

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    @SuppressWarnings("unchecked")
    private void onConstructorReturn(InfusionRecipeHandler handler, InfusionRecipe recipe, CallbackInfo ci) {
        this.foxDarkMagic$entries = new ArrayList<>();
        String userName = Minecraft.getMinecraft().getSession().getUsername();
        ResearchItem researchItem = ResearchCategories.getResearch(recipe.getResearch());
        if (researchItem != null && researchItem.key != null) {
            foxDarkMagic$entries.add(new ResearchEntryDisplay(researchItem, ThaumcraftApiHelper.isResearchComplete(userName, researchItem.key)));
        }
        int rows = (int) Math.ceil((double) aspects.size() / 7);
        int baseX = 35;
        int baseY = 129;
        int count = 0;
        for (int row = 0; row < rows; row++) {
            int reversedRow = -row + rows - 1;
            int columns = (aspects.size() + reversedRow) / rows;
            int xOffset = (100 - columns * 20) / 2;
            for (int column = 0; column < columns; column++) {
                Aspect aspect = aspects.getAspectsSortedAmount()[count++];
                int posX = baseX + column * 20 + xOffset;
                int posY = baseY + row * 20;
                ItemStack stack = new ItemStack(ModItems.itemAspect, aspects.getAmount(aspect), 1);
                ItemAspect.setAspect(stack, aspect);
                this.ingredients.add(new PositionedStack(stack, posX, posY, false));
            }
        }
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

    @Override
    @SuppressWarnings("all")
    public boolean isValid() {
        return !this.ingredients.isEmpty() && this.result != null;
    }

    /**
     * @author foxiwhitee
     * @reason Inputs and aspects in one list as items
     */
    @Overwrite
    public List<PositionedStack> getIngredients() {
        if (!foxDarkMagic$show) {
            return Collections.emptyList();
        }
        return ingredients;
    }
}
