package foxiwhitee.FoxDarkMagic.mixins.tnp.infusion;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.recipe.GuiRecipe;
import codechicken.nei.recipe.TemplateRecipeHandler;
import com.djgiannuzz.thaumcraftneiplugin.nei.NEIHelper;
import com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.InfusionRecipeHandler;
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
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.client.lib.UtilsFX;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Mixin(value = InfusionRecipeHandler.class, remap = false)
public abstract class InfusionRecipeHandlerMixin extends TemplateRecipeHandler {

    @Shadow
    protected ArrayList<AspectList> aspectsAmount;

    @Shadow
    String username;

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
        return ThaumcraftApiHelper.isResearchComplete(username, key) || DarkConfig.shouldShowInfusionRecipes;
    }

    @Override
    public int getRecipeHeight(int recipe) {
        final AspectList aspects = this.aspectsAmount.get(recipe);
        final int rows = (int) Math.ceil((double) aspects.size() / 7);
        return 152 + (rows - 1) * 20;
    }

    /**
     * @author foxiwhitee
     * @reason Turn crafting on/off
     */
    @Overwrite
    public void drawBackground(int recipeIndex) {
        IAdvancedAspectRecipe recipe = (IAdvancedAspectRecipe) arecipes.get(recipeIndex);
        int x = 34;
        int y = -24;
        UtilsFX.bindTexture("textures/gui/gui_researchbook_overlay.png");
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(3042);
        GL11.glTranslatef((float)x, (float)(y + 19), 0.0F);
        GL11.glScalef(1.75F, 1.75F, 1.0F);
        GuiDraw.drawTexturedModalRect(0, 0, 0, 3, 56, 17);
        if (recipe.foxDarkMagic$mustShowCraft()) {
            GL11.glTranslatef(0.0F, 19.0F, 0.0F);
            GuiDraw.drawTexturedModalRect(0, 0, 200, 77, 60, 44);
            GL11.glPopMatrix();
            this.drawAspects(recipeIndex, x - 7, y - 20);
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
        int rows = (int) Math.ceil((double) aspects.size() / 7);
        int baseX = x + 8;
        int baseY = y + 173;
        int count = 0;
        for (int row = 0; row < rows; row++) {
            int reversedRow = -row + rows - 1;
            int columns = (aspects.size() + reversedRow) / rows;
            int xOffset = (100 - columns * 20) / 2;
            for (int column = 0; column < columns; column++) {
                Aspect aspect = aspects.getAspectsSortedAmount()[count++];
                int posX = baseX + column * 20 + xOffset;
                int posY = baseY + row * 20;
                UtilsFX.drawTag(posX, posY, aspect, 0, 0, GuiDraw.gui.getZLevel());
            }
        }
    }

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

    /**
     * @author foxiwhitee
     * @reason Moved down and added instability number
     */
    @Overwrite
    public void drawInstability(int recipe, int x, int y) {
        CachedRecipe infusionRecipe = this.arecipes.get(recipe);
        int instability = 0;
        try {
            Class<?> clazz = infusionRecipe.getClass();
            Field field = clazz.getDeclaredField("instability");
            field.setAccessible(true);
            instability = field.getInt(infusionRecipe);

        } catch (NoSuchFieldException | IllegalAccessException ignored) {}

        int inst = Math.min(5, instability / 2);
        String text = StatCollector.translateToLocal("tc.inst." + inst) + " (" + instability + ")";
        GuiDraw.drawString(text, x + 56 - GuiDraw.fontRenderer.getStringWidth(text) / 2, y + 263, 16777215, false);
    }

    @SuppressWarnings("all")
    private TemplateRecipeHandler.CachedRecipe foxDarkMagic$createInnerRecipe(InfusionRecipe recipe) {
        try {
            Class<?> clazz = Class.forName("com.djgiannuzz.thaumcraftneiplugin.nei.recipehandler.InfusionRecipeHandler$InfusionCachedRecipe");
            Constructor<?> constructor = clazz.getDeclaredConstructor(InfusionRecipeHandler.class, InfusionRecipe.class);
            constructor.setAccessible(true);
            return (TemplateRecipeHandler.CachedRecipe) constructor.newInstance((InfusionRecipeHandler)(Object)this, recipe);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @author foxiwhitee
     * @reason Method update via InfusionCachedRecipe update
     */
    @Overwrite
    @SuppressWarnings("all")
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals(this.getOverlayIdentifier())) {
            for (Object o : ThaumcraftApi.getCraftingRecipes()) {
                if (o instanceof InfusionRecipe tcRecipe) {
                    if (tcRecipe.getRecipeInput() == null || ((tcRecipe.getRecipeOutput() instanceof ItemStack stack && stack.getItem() == null) ? tcRecipe.getRecipeOutput() : NEIHelper.getAssociatedItemStack(tcRecipe.getRecipeOutput())) == null) {
                        continue;
                    }
                    boolean shouldShowRecipe = foxDarkMagic$shouldShowRecipe(tcRecipe.getResearch());
                    IAdvancedAspectRecipe recipe = (IAdvancedAspectRecipe) foxDarkMagic$createInnerRecipe(tcRecipe);
                    recipe.foxDarkMagic$setMustShowCraft(shouldShowRecipe);
                    if (recipe.isValid()) {
                        recipe.computeVisuals();
                        this.arecipes.add((CachedRecipe) recipe);
                        this.aspectsAmount.add(recipe.getAspectList());
                    }
                }
            }
        } else if (outputId.equals("item")) {
            this.loadCraftingRecipes((ItemStack) results[0]);
        }
    }

    /**
     * @author foxiwhitee
     * @reason Method update via InfusionCachedRecipe update
     */
    @Overwrite
    @SuppressWarnings("all")
    public void loadCraftingRecipes(ItemStack result) {
        for (InfusionRecipe tcRecipe : RecipesHelper.getInfusionRecipesByOutput(result)) {
            boolean shouldShowRecipe = foxDarkMagic$shouldShowRecipe(tcRecipe.getResearch());
            IAdvancedAspectRecipe recipe = (IAdvancedAspectRecipe) foxDarkMagic$createInnerRecipe(tcRecipe);
            recipe.foxDarkMagic$setMustShowCraft(shouldShowRecipe);
            recipe.computeVisuals();
            this.arecipes.add((CachedRecipe) recipe);
            this.aspectsAmount.add(recipe.getAspectList());
        }
    }

    /**
     * @author foxiwhitee
     * @reason Method update via InfusionCachedRecipe update
     */
    @Overwrite
    @SuppressWarnings("all")
    public void loadUsageRecipes(ItemStack ingredient) {
        List<InfusionRecipe> tcRecipeList = RecipesHelper.getInfusionRecipesByInput(ingredient);

        for (InfusionRecipe tcRecipe : tcRecipeList) {
            if (tcRecipe != null && foxDarkMagic$shouldShowRecipe(tcRecipe.getResearch())) {
                IAdvancedAspectRecipe recipe = (IAdvancedAspectRecipe) foxDarkMagic$createInnerRecipe(tcRecipe);
                recipe.foxDarkMagic$setMustShowCraft(true);
                recipe.computeVisuals();
                recipe.foxDarkMagic$setIngredientPermutation(recipe.getIngredients(), ingredient);
                this.arecipes.add((CachedRecipe) recipe);
                this.aspectsAmount.add(recipe.getAspectList());
            }
        }
    }
}
