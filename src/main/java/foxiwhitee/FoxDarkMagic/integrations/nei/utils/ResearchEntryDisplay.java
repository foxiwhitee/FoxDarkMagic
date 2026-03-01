package foxiwhitee.FoxDarkMagic.integrations.nei.utils;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.GuiRecipe;
import com.djgiannuzz.thaumcraftneiplugin.ModItems;
import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.lib.research.ResearchManager;

import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Field;
import java.util.List;

public class ResearchEntryDisplay {
    private static final ResourceLocation DEFAULT_ICON = new ResourceLocation("thaumcraft", "textures/items/thaumonomicon.png");

    private final ResearchItem researchItem;
    private final boolean unlocked;
    private final String categoryDisplayName;
    private final ResourceLocation categoryIcon;

    private int lastRenderX;
    private int lastRenderY;

    public ResearchEntryDisplay(ResearchItem item, boolean isUnlocked) {
        this.researchItem = item;
        this.unlocked = isUnlocked;

        if (item != null) {
            this.categoryDisplayName = ResearchCategories.getCategoryName(item.category);
            ResearchCategoryList catList = ResearchCategories.getResearchList(item.category);
            this.categoryIcon = (catList != null && catList.icon != null) ? catList.icon : DEFAULT_ICON;
        } else {
            this.categoryDisplayName = "Unknown";
            this.categoryIcon = DEFAULT_ICON;
        }
    }

    public void buildTooltip(List<String> tooltip) {
        String statusColor = unlocked ? EnumChatFormatting.GREEN.toString() : EnumChatFormatting.RED.toString();
        String researchName = (researchItem != null) ? researchItem.getName() : "N/A";

        tooltip.add(String.format("%s%s%s: %s", EnumChatFormatting.UNDERLINE, statusColor, categoryDisplayName, researchName));

        addPrerequisites(tooltip);

        if (tooltip.size() > 1) {
            tooltip.add(1, "");
        }
    }

    private void addPrerequisites(List<String> tooltip) {
        if (researchItem == null) return;
        try {
            String playerName = Minecraft.getMinecraft().getSession().getUsername();
            getResearchListByName(tooltip, researchItem.parents, playerName, "tooltip.research.requisite.parents");
            getResearchListByName(tooltip, researchItem.parentsHidden, playerName, "tooltip.research.requisite.parentsHidden");
            if (researchItem.getItemTriggers() != null && researchItem.getItemTriggers().length != 0) {
                tooltip.add(StatCollector.translateToLocal("tooltip.research.requisite.item") + ":");
                for (ItemStack itemStack : researchItem.getItemTriggers()) {
                    String displayName = itemStack.getDisplayName();
                    tooltip.add("    " + displayName);
                }
            }
            if (researchItem.getEntityTriggers() != null && researchItem.getEntityTriggers().length != 0) {
                tooltip.add(StatCollector.translateToLocal("tooltip.research.requisite.entity") + ":");
                for (String entityKey : researchItem.getEntityTriggers()) {
                    String entityName = StatCollector.translateToLocal("entity." + entityKey + ".name");
                    tooltip.add("    " + entityName);
                }
            }
            if (researchItem.getAspectTriggers() != null && researchItem.getAspectTriggers().length != 0) {
                tooltip.add(StatCollector.translateToLocal("tooltip.research.requisite.aspect") + ":");
                for (Aspect aspect : researchItem.getAspectTriggers()) {
                    String aspectName = aspect.getName() + " - " + aspect.getLocalizedDescription();
                    tooltip.add("    " + aspectName);
                }
            }
        } catch (Exception e) {
            tooltip.add(EnumChatFormatting.GRAY + "(Error loading requirements)");
        }
    }

    private void getResearchListByName(List<String> list, String[] researchKeys, String playerName, String keysName) {
        if (researchKeys != null && researchKeys.length != 0) {
            int needResearch = 0;
            list.add(StatCollector.translateToLocal(keysName));
            for (String researchKey : researchKeys) {
                String researchName = ResearchCategories.getCategoryName(ResearchCategories.getResearch(researchKey).category) + " : " + ResearchCategories.getResearch(researchKey).getName();
                if (ResearchManager.isResearchComplete(playerName, researchKey)) {
                    if (researchKeys.length <= 10) {
                        researchName = EnumChatFormatting.GREEN + "" + EnumChatFormatting.STRIKETHROUGH + researchName;
                        list.add(EnumChatFormatting.RESET + "    " + researchName);
                    }
                } else {
                    needResearch++;
                    researchName = EnumChatFormatting.RED + researchName;
                    list.add(EnumChatFormatting.RESET + "    " + researchName);
                }
            }
            if (researchKeys.length > 10 && needResearch == 0) {
                list.add(EnumChatFormatting.GREEN + "    " + StatCollector.translateToLocal("tooltip.research.requisite.all"));
            }
        }
    }

    public void render(int x, int y) {
        this.lastRenderX = x;
        this.lastRenderY = y;

        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        UtilsFX.bindTexture(categoryIcon);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        GL11.glTranslated(x, y, 0);
        GL11.glScaled(0.8, 0.8, 1.0);

        UtilsFX.drawTexturedQuadFull(0, 0, 0);

        if (!unlocked) {
            renderLockOverlay();
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private void renderLockOverlay() {
        GL11.glPushMatrix();
        GL11.glTranslated(18, 0, 0);
        GL11.glScaled(1.5, 1.5, 1.0);
        GuiDraw.drawString("!", 0, 0, 0xFF0000, true);
        GL11.glPopMatrix();
    }

    public Rectangle getBounds(GuiRecipe<?> gui, int recipeIndex) {
        Point offset = gui.getRecipePosition(recipeIndex);

        int guiLeft = 0;
        int guiTop = 0;

        try {
            Field fLeft = gui.getClass().getField("guiLeft");
            Field fTop = gui.getClass().getField("guiTop");

            guiLeft = fLeft.getInt(gui);
            guiTop = fTop.getInt(gui);
        } catch (Exception ignored) {}

        return new Rectangle(guiLeft + offset.x + lastRenderX, guiTop + offset.y + lastRenderY, 20, 16);
    }

    public static void addAspectsToIngredientsArcane(AspectList aspects, List<PositionedStack> ingredients) {
        try {
            int baseX = 36;
            int baseY = 115;
            int count = 0;
            int columns = aspects.size();
            int xOffset = (100 - columns * 20) / 2;

            for (int column = 0; column < columns; column++) {
                Aspect aspect = aspects.getAspectsSortedAmount()[count++];
                int posX = baseX + column * 18 + xOffset;
                ItemStack stack = new ItemStack(ModItems.itemAspect, aspects.getAmount(aspect), 1);
                ItemAspect.setAspect(stack, aspect);
                ingredients.add(new PositionedStack(stack, posX, baseY, false));
            }

        } catch (Exception ignored) {}
    }
}
