package foxiwhitee.FoxDarkMagic.mixins.tnp.arcane;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.djgiannuzz.thaumcraftneiplugin.nei.NEIHelper;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.ArcaneShapedRecipeHandler;
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
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.items.wands.ItemWandCasting;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import static com.djgiannuzz.thaumcraftneiplugin.nei.NEIHelper.getPrimalAspectListFromAmounts;

@Mixin(value = ArcaneShapedRecipeHandler.class, remap = false)
public abstract class ArcaneShapedRecipeHandlerMixin extends TemplateRecipeHandler {

    @Shadow
    protected ArrayList<int[]> aspectsAmount;

    @Shadow
    String username;

    @Shadow
    protected static int[] getAmounts(ShapedArcaneRecipe recipe) {
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
    private TemplateRecipeHandler.CachedRecipe foxDarkMagic$createInnerRecipe(WandRod rod, WandCap cap, ItemStack result, boolean isScepter) {
        try {
            Class<?> clazz = Class.forName("com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.ArcaneShapedRecipeHandler$ArcaneWandChachedRecipe");
            Constructor<?> constructor = clazz.getDeclaredConstructor(ArcaneShapedRecipeHandler.class, WandRod.class, WandCap.class, ItemStack.class, boolean.class);

            constructor.setAccessible(true);

            return (CachedRecipe) constructor.newInstance((ArcaneShapedRecipeHandler)(Object)this, rod, cap, result, isScepter);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("all")
    private TemplateRecipeHandler.CachedRecipe foxDarkMagic$createInnerRecipe(ShapedArcaneRecipe r) {
        try {
            Class<?> clazz = Class.forName("com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.ArcaneShapedRecipeHandler$ArcaneShapedCachedRecipe");
            Constructor<?> constructor = clazz.getDeclaredConstructor(ArcaneShapedRecipeHandler.class, ShapedArcaneRecipe.class);

            constructor.setAccessible(true);

            return (CachedRecipe) constructor.newInstance((ArcaneShapedRecipeHandler)(Object)this, r);
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
                IAdvancedAspectRecipe wandRec = null;
                if (o instanceof ShapedArcaneRecipe tcRecipe) {
                    if (tcRecipe.getRecipeOutput().getItem() instanceof ItemWandCasting wand) {
                        WandRod rod = wand.getRod(tcRecipe.getRecipeOutput());
                        WandCap cap = wand.getCap(tcRecipe.getRecipeOutput());
                        boolean shouldShowRecipe = false;
                        if (!wand.isSceptre(tcRecipe.getRecipeOutput()) || foxDarkMagic$shouldShowRecipe("SCEPTRE")) {
                            if (foxDarkMagic$shouldShowRecipe(cap.getResearch()) && foxDarkMagic$shouldShowRecipe(rod.getResearch())) {
                                shouldShowRecipe = true;
                            }
                        }
                        if (rod != null || cap != null) {
                            wandRec = (IAdvancedAspectRecipe) foxDarkMagic$createInnerRecipe(rod, cap, tcRecipe.getRecipeOutput(), false);
                            wandRec.foxDarkMagic$setMustShowCraft(shouldShowRecipe);
                        }
                    }
                    boolean shouldShowRecipe = foxDarkMagic$shouldShowRecipe(tcRecipe.getResearch());
                    IAdvancedAspectRecipe recipe = (IAdvancedAspectRecipe) foxDarkMagic$createInnerRecipe(tcRecipe);
                    recipe.foxDarkMagic$setMustShowCraft(shouldShowRecipe);
                    if (wandRec != null) {
                        recipe.foxDarkMagic$getEntries().addAll(wandRec.foxDarkMagic$getEntries());
                    }
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
        if (result.getItem() instanceof ItemWandCasting wand) {
            WandRod rod = wand.getRod(result);
            WandCap cap = wand.getCap(result);
            boolean shouldShowRecipe;
            if (!wand.isSceptre(result) || foxDarkMagic$shouldShowRecipe("SCEPTRE")) {
                if (foxDarkMagic$shouldShowRecipe(cap.getResearch())
                    && foxDarkMagic$shouldShowRecipe(rod.getResearch())) {
                    shouldShowRecipe = true;
                } else {
                    shouldShowRecipe = false;
                }
            } else {
                shouldShowRecipe = false;
            }

            IAdvancedAspectRecipe recipe = (IAdvancedAspectRecipe) foxDarkMagic$createInnerRecipe(rod, cap, result, wand.isSceptre(result));
            recipe.computeVisuals();
            recipe.foxDarkMagic$setMustShowCraft(shouldShowRecipe);
            this.arecipes.add((CachedRecipe) recipe);
            this.aspectsAmount.add(NEIHelper.getWandAspectsWandCost(result));


            boolean isSceptre = wand.isSceptre(result);

            ((List<Object>) ThaumcraftApi.getCraftingRecipes()).stream().filter(o -> o instanceof ShapedArcaneRecipe)
                .filter(r -> {
                    ItemStack output = ((ShapedArcaneRecipe) r).output;
                    if (!(output.getItem() instanceof ItemWandCasting)) return false;
                    if (isSceptre != wand.isSceptre(output)) return false;
                    if (output.getItem().getClass() != result.getItem().getClass()) return false;
                    WandRod outputRod = wand.getRod(output);
                    WandCap outputCap = wand.getCap(output);
                    return outputRod.getTag().equals(rod.getTag()) && outputCap.getTag().equals(cap.getTag());
                }).forEach(o -> {
                    ShapedArcaneRecipe arcaneRecipe = (ShapedArcaneRecipe) o;
                    IAdvancedAspectRecipe r = (IAdvancedAspectRecipe) foxDarkMagic$createInnerRecipe(arcaneRecipe);
                    r.foxDarkMagic$setMustShowCraft(shouldShowRecipe);
                    IAdvancedAspectRecipe wandRec = (IAdvancedAspectRecipe) foxDarkMagic$createInnerRecipe(rod, cap, result, false);
                    wandRec.foxDarkMagic$setMustShowCraft(shouldShowRecipe);
                    r.foxDarkMagic$getEntries().addAll(wandRec.foxDarkMagic$getEntries());
                    r.computeVisuals();
                    this.arecipes.add((CachedRecipe) r);
                    this.aspectsAmount.add(getAmounts(arcaneRecipe));
                });
        } else {
            for (Object o : ThaumcraftApi.getCraftingRecipes()) {
                if (o instanceof ShapedArcaneRecipe shapedArcaneRecipe) {
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
    }

    /**
     * @author foxiwhitee
     * @reason Method update via recipe update
     */
    @Overwrite
    @SuppressWarnings("all")
    public void loadUsageRecipes(ItemStack ingredient) {
        for (Object o : ThaumcraftApi.getCraftingRecipes()) {
            if (o instanceof ShapedArcaneRecipe tcRecipe) {
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
