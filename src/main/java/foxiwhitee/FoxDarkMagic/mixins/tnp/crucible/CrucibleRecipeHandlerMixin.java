package foxiwhitee.FoxDarkMagic.mixins.tnp.crucible;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.CrucibleRecipeHandler;
import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.helpers.RecipesHelper;
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
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.client.lib.UtilsFX;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

@Mixin(value = CrucibleRecipeHandler.class, remap = false)
public abstract class CrucibleRecipeHandlerMixin extends TemplateRecipeHandler {

    @Shadow
    String username;

    @Shadow
    protected ArrayList<AspectList> aspectsAmount;

    @Shadow
    public abstract String getOverlayIdentifier();

    @Redirect(
        method = "<init>",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/entity/EntityClientPlayerMP;getDisplayName()Ljava/lang/String;"))
    private String redirectGetDisplayName(EntityClientPlayerMP instance) {
        return Minecraft.getMinecraft().getSession().getUsername();
    }

    @Unique
    private boolean foxDarkMagic$shouldShowRecipe(String key) {
        return ThaumcraftApiHelper.isResearchComplete(username, key) || DarkConfig.shouldShowCrucibleRecipes;
    }

    @SuppressWarnings("all")
    private TemplateRecipeHandler.CachedRecipe foxDarkMagic$createInnerRecipe(CrucibleRecipe recipe) {
        try {
            Class<?> clazz = Class.forName("com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.CrucibleRecipeHandler$CrucibleCachedRecipe");
            Constructor<?> constructor = clazz.getDeclaredConstructor(CrucibleRecipeHandler.class, CrucibleRecipe.class);
            constructor.setAccessible(true);
            return (TemplateRecipeHandler.CachedRecipe) constructor.newInstance((CrucibleRecipeHandler)(Object)this, recipe);
        } catch (Exception e) {
            return null;
        }
    }

    @Unique
    private ArrayList<TemplateRecipeHandler.CachedRecipe> foxDarkMagic$getRecipes() {
        return arecipes;
    }

    /**
     * @author foxiwhitee
     * @reason Method update via CrucibleCachedRecipe update
     */
    @Overwrite
    @SuppressWarnings("all")
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(this.getOverlayIdentifier())) {
            for (Object o : ThaumcraftApi.getCraftingRecipes()) {
                if (o instanceof CrucibleRecipe tcRecipe) {
                    boolean shouldShowRecipe = foxDarkMagic$shouldShowRecipe(tcRecipe.key);
                    TemplateRecipeHandler.CachedRecipe recipe = foxDarkMagic$createInnerRecipe(tcRecipe);
                    if (((IAdvancedAspectRecipe)recipe).isValid()) {
                        ((IAdvancedAspectRecipe)recipe).computeVisuals();
                        ((IAdvancedAspectRecipe)recipe).foxDarkMagic$setMustShowCraft(shouldShowRecipe);
                        foxDarkMagic$getRecipes().add(recipe);
                        aspectsAmount.add(((IAdvancedAspectRecipe)recipe).getAspectList());
                    }
                }
            }
        } else if (outputId.equals("item")) {
            this.loadCraftingRecipes((ItemStack) results[0]);
        }
    }

    /**
     * @author foxiwhitee
     * @reason Method update via CrucibleCachedRecipe update
     */
    @Overwrite
    @SuppressWarnings("all")
    public void loadCraftingRecipes(ItemStack result) {
        for (CrucibleRecipe tcRecipe : RecipesHelper.getCrucibleRecipesByOutput(result)) {
            boolean shouldShowRecipe = foxDarkMagic$shouldShowRecipe(tcRecipe.key);
            TemplateRecipeHandler.CachedRecipe recipe = foxDarkMagic$createInnerRecipe(tcRecipe);
            ((IAdvancedAspectRecipe)recipe).computeVisuals();
            ((IAdvancedAspectRecipe)recipe).foxDarkMagic$setMustShowCraft(shouldShowRecipe);
            foxDarkMagic$getRecipes().add(recipe);
            aspectsAmount.add(((IAdvancedAspectRecipe)recipe).getAspectList());
        }
    }

    /**
     * @author foxiwhitee
     * @reason Method update via CrucibleCachedRecipe update
     */
    @Overwrite
    @SuppressWarnings("all")
    public void loadUsageRecipes(ItemStack ingredient) {
        List<CrucibleRecipe> tcRecipeList = RecipesHelper.getCrucibleRecipesByInput(ingredient);

        for (CrucibleRecipe tcRecipe : tcRecipeList) {
            if (tcRecipe != null && foxDarkMagic$shouldShowRecipe(tcRecipe.key)) {
                TemplateRecipeHandler.CachedRecipe recipe = foxDarkMagic$createInnerRecipe(tcRecipe);
                ((IAdvancedAspectRecipe)recipe).computeVisuals();
                ((IAdvancedAspectRecipe)recipe).foxDarkMagic$setMustShowCraft(true);
                ((IAdvancedAspectRecipe)recipe).foxDarkMagic$setIngredientPermutation(recipe.getIngredients(), ingredient);
                foxDarkMagic$getRecipes().add(recipe);
                aspectsAmount.add(((IAdvancedAspectRecipe)recipe).getAspectList());
            }
        }
    }

    /**
     * @author foxiwhitee
     * @reason Centering the boiler texture (because I'm a perfectionist) and to turn crafting on/off
     */
    @Overwrite
    public void drawBackground(int recipe) {
        float x = 35.5f;
        float y = 3;
        GL11.glPushMatrix();
        UtilsFX.bindTexture("textures/gui/gui_researchbook_overlay.png");
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(3042);
        GL11.glTranslatef(x, y, 0.0F);
        GL11.glScalef(1.75F, 1.75F, 1.0F);
        GuiDraw.drawTexturedModalRect(0, 0, 0, 3, 56, 17);
        if (((IAdvancedAspectRecipe)foxDarkMagic$getRecipes().get(recipe)).foxDarkMagic$mustShowCraft()) {
            GL11.glTranslatef(0.0F, 26.0F, 0.0F);
            GuiDraw.drawTexturedModalRect(0, 0, 0, 20, 56, 48);
            GL11.glTranslatef(21.0F, -8.0F, 0.0F);
            GuiDraw.drawTexturedModalRect(0, 0, 100, 84, 11, 13);
            GL11.glPopMatrix();
            this.drawAspects(recipe, (int) (x - 6), (int) (y - 24));
        } else {
            GL11.glPopMatrix();
        }
    }

    /**
     * @author foxiwhitee
     * @reason The number is no longer drawn.
     */
    @Overwrite
    public void drawAspects(int recipe, int x, int y) {
        AspectList aspects = this.aspectsAmount.get(recipe);
        int rows = (int) Math.ceil((double) aspects.size() / 3);

        int xBase = x + 8;
        int yBase = y + 107 - (10 * rows);
        int count = 0;

        for (int row = 0; row < rows; row++) {
            int columns = Math.min(aspects.size() - row * 3, 3);
            int offSet = (100 - columns * 20) / 2;
            for (int column = 0; column < columns; column++) {
                Aspect aspect = aspects.getAspectsSortedAmount()[count++];
                int posX = xBase + column * 20 + offSet;
                int posY = yBase + row * 20;
                UtilsFX.drawTag(posX, posY, aspect, 0, 0, GuiDraw.gui.getZLevel());
            }
        }
    }

    /**
     * @author foxiwhitee
     * @reason We write a text about studying in this function
     */
    @Overwrite
    public void drawForeground(int recipe) {
        if (!((IAdvancedAspectRecipe)foxDarkMagic$getRecipes().get(recipe)).foxDarkMagic$mustShowCraft()) {
            String textToDraw = StatCollector.translateToLocal("tooltip.research.missing");
            int y = 38;
            for (String text : Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(textToDraw, 162)) {
                GuiDraw.drawStringC(text, 82, y, 404040, false);
                y += 11;
            }
        }

        GuiDraw.drawString(StatCollector.translateToLocal("tooltip.researchName"), 0, 13, 404040, false);
        int recipeY = 23;
        for (ResearchEntryDisplay entry : ((IAdvancedAspectRecipe)foxDarkMagic$getRecipes().get(recipe)).foxDarkMagic$getEntries()) {
            entry.render(0, recipeY);
            recipeY += 13;
        }
    }

    @Override
    public List<String> handleTooltip(GuiRecipe<?> gui, List<String> list, int recipeIndex) {
        if (GuiContainerManager.shouldShowTooltip(gui) && list.isEmpty()) {
            Point mousePos = GuiDraw.getMousePosition();

            for (ResearchEntryDisplay entry : ((IAdvancedAspectRecipe)foxDarkMagic$getRecipes().get(recipeIndex)).foxDarkMagic$getEntries()) {
                Rectangle rect = entry.getBounds(gui, recipeIndex);
                if (rect.contains(mousePos)) {
                    entry.buildTooltip(list);
                }
            }
        }
        return super.handleTooltip(gui, list, recipeIndex);
    }
}
