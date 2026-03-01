package foxiwhitee.FoxDarkMagic.mixins.tnp.arcane;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.ArcaneShapelessRecipeHandler;
import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.integrations.nei.api.IAdvancedAspectRecipe;
import foxiwhitee.FoxDarkMagic.integrations.nei.utils.ResearchEntryDisplay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.client.lib.UtilsFX;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import static com.djgiannuzz.thaumcraftneiplugin.nei.NEIHelper.getPrimalAspectListFromAmounts;

@Mixin(value = ArcaneShapelessRecipeHandler.class, remap = false)
public abstract class ArcaneShapelessRecipeHandlerMixin extends TemplateRecipeHandler {

    @Shadow
    protected ArrayList<int[]> aspectsAmount;

    @Shadow
    String username;

    @Shadow
    protected static int[] getAmounts(ShapelessArcaneRecipe recipe) {
        return null;
    }

    @Redirect(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;getDisplayName()Ljava/lang/String;"))
    private String redirectGetDisplayName(EntityClientPlayerMP instance) {
        return Minecraft.getMinecraft().getSession().getUsername();
    }


    /**
     * @author foxiwhitee
     * @reason Centering the boiler texture (because I'm a perfectionist) and to turn crafting on/off
     */
    @Overwrite
    public void drawBackground(int recipe) {
        int x = 36;
        int y = -15;
        boolean shouldShowRecipe;
        TemplateRecipeHandler.CachedRecipe cRecipe = arecipes.get(recipe);
        if (cRecipe instanceof IAdvancedAspectRecipe advancedAspectRecipe) {
            shouldShowRecipe = advancedAspectRecipe.foxDarkMagic$mustShowCraft();
        } else {
            throw new RuntimeException("Incompatible recipe type found: " + cRecipe.getClass());
        }

        UtilsFX.bindTexture("textures/gui/gui_researchbook_overlay.png");
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(3042);
        GL11.glTranslatef((float) x, (float) y, 0.0F);
        GL11.glScalef(1.7F, 1.7F, 1.0F);
        GuiDraw.drawTexturedModalRect(20, 7, 20, 3, 16, 16);
        if (shouldShowRecipe) {
            GuiDraw.drawTexturedModalRect(2, 23, 112, 15, 52, 52);
        }
        GL11.glPopMatrix();

        if (shouldShowRecipe) {
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.4F);
            GL11.glEnable(3042);
            GL11.glTranslatef((float) x - 30, (float) (y + 126), 0.0F);
            GL11.glScalef(2.0F, 2.0F, 1.0F);
            GuiDraw.drawTexturedModalRect(0, 0, 68, 76, 12, 12);
            GL11.glPopMatrix();

            this.foxDarkMagic$drawAspects(recipe);
        }
    }

    @Unique
    public void foxDarkMagic$drawAspects(int recipe) {
        int[] amounts = this.aspectsAmount.get(recipe);
        AspectList aspects = getPrimalAspectListFromAmounts(amounts);

        int baseX = 36;
        int baseY = 115;
        int count = 0;
        int columns = aspects.size();
        int xOffset = (100 - columns * 20) / 2;

        for (int column = 0; column < columns; column++) {
            Aspect aspect = aspects.getAspectsSortedAmount()[count++];
            int posX = baseX + column * 18 + xOffset;
            UtilsFX.drawTag(posX, baseY, aspect, 0, 0, GuiDraw.gui.getZLevel());
        }
    }

    /**
     * @author foxiwhitee
     * @reason We write a text about studying in this function
     */
    @Overwrite
    public void drawExtras(int recipe) {
        if (!((IAdvancedAspectRecipe)arecipes.get(recipe)).foxDarkMagic$mustShowCraft()) {
            String textToDraw = StatCollector.translateToLocal("tooltip.research.missing");
            int y = 38;
            for (String text : Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(textToDraw, 162)) {
                GuiDraw.drawStringC(text, 82, y, 404040, false);
                y += 11;
            }
        }

        GuiDraw.drawString(StatCollector.translateToLocal("tooltip.researchName"), 0, 13, 404040, false);
        int recipeY = 23;
        for (ResearchEntryDisplay entry : ((IAdvancedAspectRecipe)arecipes.get(recipe)).foxDarkMagic$getEntries()) {
            entry.render(0, recipeY);
            recipeY += 13;
        }
    }

    @Override
    public List<String> handleTooltip(GuiRecipe<?> gui, List<String> list, int recipeIndex) {
        if (GuiContainerManager.shouldShowTooltip(gui) && list.isEmpty()) {
            Point mousePos = GuiDraw.getMousePosition();

            for (ResearchEntryDisplay entry : ((IAdvancedAspectRecipe)arecipes.get(recipeIndex)).foxDarkMagic$getEntries()) {
                Rectangle rect = entry.getBounds(gui, recipeIndex);
                if (rect.contains(mousePos)) {
                    entry.buildTooltip(list);
                }
            }
        }
        return super.handleTooltip(gui, list, recipeIndex);
    }

    @Unique
    private boolean foxDarkMagic$shouldShowRecipe(String key) {
        return ThaumcraftApiHelper.isResearchComplete(username, key) || DarkConfig.shouldShowArcaneRecipes;
    }

    @SuppressWarnings("all")
    private TemplateRecipeHandler.CachedRecipe foxDarkMagic$createInnerRecipe(ShapelessArcaneRecipe r) {
        try {
            Class<?> clazz = Class.forName("com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.ArcaneShapelessRecipeHandler$ArcaneShapelessCachedRecipe");
            Constructor<?> constructor = clazz.getDeclaredConstructor(ArcaneShapelessRecipeHandler.class, ShapelessArcaneRecipe.class);

            constructor.setAccessible(true);

            return (CachedRecipe) constructor.newInstance((ArcaneShapelessRecipeHandler)(Object)this, r);
        } catch (Exception e) {
            return null;
        }
    }


    /**
     * @author foxiwhitee
     * @reason Method update via recipe update
     */
    @Overwrite
    @SuppressWarnings("all")
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(this.getOverlayIdentifier())) {
            for (Object o : ThaumcraftApi.getCraftingRecipes()) {
                if (o instanceof ShapelessArcaneRecipe tcRecipe) {
                    boolean shouldShowRecipe = foxDarkMagic$shouldShowRecipe(tcRecipe.getResearch());
                    IAdvancedAspectRecipe recipe = (IAdvancedAspectRecipe) foxDarkMagic$createInnerRecipe(tcRecipe);
                    recipe.foxDarkMagic$setMustShowCraft(shouldShowRecipe);
                    if (recipe.isValid()) {
                        recipe.computeVisuals();
                        this.arecipes.add((CachedRecipe) recipe);
                        this.aspectsAmount.add(getAmounts(tcRecipe));
                    }
                }
            }
        } else if (outputId.equals("item")) {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    /**
     * @author foxiwhitee
     * @reason Method update via recipe update
     */
    @Overwrite
    @SuppressWarnings("all")
    public void loadCraftingRecipes(ItemStack result) {
        for (Object o : ThaumcraftApi.getCraftingRecipes()) {
            if (o instanceof ShapelessArcaneRecipe shapedArcaneRecipe) {
                boolean shouldShowRecipe = foxDarkMagic$shouldShowRecipe(shapedArcaneRecipe.getResearch());

                IAdvancedAspectRecipe recipe = (IAdvancedAspectRecipe) foxDarkMagic$createInnerRecipe(shapedArcaneRecipe);
                recipe.foxDarkMagic$setMustShowCraft(shouldShowRecipe);

                if (recipe.isValid() && NEIServerUtils
                    .areStacksSameTypeCraftingWithNBT(shapedArcaneRecipe.getRecipeOutput(), result)) {
                    recipe.computeVisuals();
                    this.arecipes.add((CachedRecipe) recipe);
                    this.aspectsAmount.add(getAmounts(shapedArcaneRecipe));
                }
            }
        }
    }

    /**
     * @author foxiwhitee
     * @reason Method update via recipe update
     */
    @Overwrite
    @SuppressWarnings("all")
    public void loadUsageRecipes(ItemStack ingredient) {
        for (Object o : ThaumcraftApi.getCraftingRecipes()) {
            if (o instanceof ShapelessArcaneRecipe tcRecipe) {
                IAdvancedAspectRecipe recipe = (IAdvancedAspectRecipe) foxDarkMagic$createInnerRecipe(tcRecipe);
                recipe.foxDarkMagic$setMustShowCraft(true);
                if (recipe.isValid() && ((CachedRecipe) recipe).containsWithNBT(recipe.getIngredients(), ingredient) && foxDarkMagic$shouldShowRecipe(tcRecipe.getResearch())) {
                    recipe.computeVisuals();
                    recipe.foxDarkMagic$setIngredientPermutation(recipe.getIngredients(), ingredient);
                    this.arecipes.add((CachedRecipe) recipe);
                    this.aspectsAmount.add(getAmounts(tcRecipe));
                }
            }
        }
    }
}
