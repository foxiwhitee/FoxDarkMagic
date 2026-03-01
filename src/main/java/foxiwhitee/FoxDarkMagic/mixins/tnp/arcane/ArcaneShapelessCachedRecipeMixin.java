package foxiwhitee.FoxDarkMagic.mixins.tnp.arcane;

import codechicken.nei.PositionedStack;
import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.ArcaneShapelessRecipeHandler;
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
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Mixin(targets = "com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.ArcaneShapelessRecipeHandler$ArcaneShapelessCachedRecipe", remap = false)
public abstract class ArcaneShapelessCachedRecipeMixin implements IAdvancedAspectRecipe {
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

    @Redirect(
        method = "<init>",
        at = @At(
            value = "NEW",
            target = "(Ljava/lang/Object;II)Lcodechicken/nei/PositionedStack;"
        )
    )
    private PositionedStack redirectOutputStack(Object object, int x, int y) {
        return new PositionedStack(object, x + 1, y);
    }

    @Redirect(
        method = "setIngredients",
        at = @At(
            value = "NEW",
            target = "(Ljava/lang/Object;IIZ)Lcodechicken/nei/PositionedStack;"
        )
    )
    private PositionedStack redirectInputStack(Object out, int x, int y, boolean expendable) {
        return new PositionedStack(out, x + 1, y, expendable);
    }

    @Inject(
        method = "<init>",
        at = @At("RETURN")
    )
    @SuppressWarnings("unchecked")
    private void onConstructorReturn(ArcaneShapelessRecipeHandler handler, ShapelessArcaneRecipe recipe, CallbackInfo ci) {
        this.foxDarkMagic$entries = new ArrayList<>();
        String userName = Minecraft.getMinecraft().getSession().getUsername();
        ResearchItem researchItem = ResearchCategories.getResearch(recipe.getResearch());
        if (researchItem != null && researchItem.key != null) {
            foxDarkMagic$entries.add(new ResearchEntryDisplay(researchItem, ThaumcraftApiHelper.isResearchComplete(userName, researchItem.key)));
        }
        try {
            Field field = this.getClass().getSuperclass().getDeclaredField("ingredients");
            field.setAccessible(true);
            ResearchEntryDisplay.addAspectsToIngredientsArcane(aspects, (List<PositionedStack>) field.get(this));
        } catch (Exception ignored) {}
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
     * @reason Checking for aspect content
     */
    @Overwrite
    public boolean contains(Collection<PositionedStack> ingredients, ItemStack ingredient) {
        if (ingredient.getItem() instanceof ItemAspect) {
            Aspect aspect = ItemAspect.getAspects(ingredient).getAspects()[0];
            return this.aspects.aspects.containsKey(aspect);
        }
        for (PositionedStack stack : ingredients) {
            if (stack.contains(ingredient)) {
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("all")
    public List<PositionedStack> getIngredients() {
        if (!this.foxDarkMagic$show) {
            return Collections.emptyList();
        }

        try {
            java.lang.reflect.Field field = this.getClass().getSuperclass().getDeclaredField("ingredients");
            field.setAccessible(true);
            return (List<PositionedStack>) field.get(this);
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }
}
