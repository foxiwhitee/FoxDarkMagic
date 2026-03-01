package foxiwhitee.FoxDarkMagic.mixins.tnp.arcane;

import codechicken.nei.PositionedStack;
import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.ArcaneShapedRecipeHandler;
import foxiwhitee.FoxDarkMagic.integrations.nei.api.IAdvancedAspectRecipe;
import foxiwhitee.FoxDarkMagic.integrations.nei.utils.ResearchEntryDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Mixin(targets = "com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.ArcaneShapedRecipeHandler$ArcaneWandChachedRecipe", remap = false)
public abstract class ArcaneWandChachedRecipeMixin implements IAdvancedAspectRecipe {
    @Shadow
    protected AspectList aspects;
    @Unique
    private boolean foxDarkMagic$show;
    @Unique
    private List<ResearchEntryDisplay> foxDarkMagic$entries;

    @Redirect(
        method = "<init>(Lcom/djgiannuzz/thaumcraftneiplugin/nei/recipehandler/ArcaneShapedRecipeHandler;Lthaumcraft/api/wands/WandRod;Lthaumcraft/api/wands/WandCap;Lnet/minecraft/item/ItemStack;Z)V",
        at = @At(value = "INVOKE", target = "Lcom/djgiannuzz/thaumcraftneiplugin/nei/NEIHelper;addAspectsToIngredients(Lthaumcraft/api/aspects/AspectList;Ljava/util/List;I)V")
    )
    private void redirectAddAspects(AspectList sx, List<PositionedStack> sy, int stack) {}

    @Inject(
        method = "<init>(Lcom/djgiannuzz/thaumcraftneiplugin/nei/recipehandler/ArcaneShapedRecipeHandler;Lthaumcraft/api/wands/WandRod;Lthaumcraft/api/wands/WandCap;Lnet/minecraft/item/ItemStack;Z)V",
        at = @At("RETURN")
    )
    @SuppressWarnings("unchecked")
    private void onConstructorReturn(ArcaneShapedRecipeHandler recipe, WandRod rod, WandCap cap, ItemStack result, boolean isScepter, CallbackInfo ci) {
        this.foxDarkMagic$entries = new ArrayList<>();
        String userName = Minecraft.getMinecraft().getSession().getUsername();
        if (isScepter) {
            foxDarkMagic$entries.add(new ResearchEntryDisplay(ResearchCategories.getResearch("SCEPTRE"), ThaumcraftApiHelper.isResearchComplete(userName, "SCEPTRE")));
        }
        if (cap != null && !cap.getResearch().isEmpty()) {
            foxDarkMagic$entries.add(new ResearchEntryDisplay(ResearchCategories.getResearch(cap.getResearch()), ThaumcraftApiHelper.isResearchComplete(userName, cap.getResearch())));
        }
        if (rod != null && !rod.getResearch().isEmpty()) {
            foxDarkMagic$entries.add(new ResearchEntryDisplay(ResearchCategories.getResearch(rod.getResearch()), ThaumcraftApiHelper.isResearchComplete(userName, rod.getResearch())));
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

    @Override
    @SuppressWarnings("all")
    public AspectList getAspectList() {
        return aspects;
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

    @Dynamic
    @Inject(
        method = "getIngredients()Ljava/util/List;",
        at = @At("HEAD"),
        cancellable = true,
        remap = false
    )
    private void onGetIngredients(CallbackInfoReturnable<List<?>> cir) {
        if (!this.foxDarkMagic$show) {
            cir.setReturnValue(Collections.emptyList());
        }
    }
}
