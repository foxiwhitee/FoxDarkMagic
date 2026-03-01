package foxiwhitee.FoxDarkMagic.integrations.nei.helpers;

import codechicken.nei.PositionedStack;
import com.djgiannuzz.thaumcraftneiplugin.ModItems;
import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import foxiwhitee.FoxDarkMagic.DarkCore;
import foxiwhitee.FoxLib.nei.Draw;
import foxiwhitee.FoxLib.nei.UniversalRecipeHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.client.gui.GuiResearchRecipe;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class AspectInItemsHelper {
    private static boolean discovered(Aspect aspect) {
        return Thaumcraft.proxy.playerKnowledge.hasDiscoveredAspect(Minecraft.getMinecraft().getSession().getUsername(), aspect);
    }

    public static void processInputs(ItemsWithAspect aspect, List<PositionedStack> stacks) {
        if (discovered(aspect.aspect)) {
            for (int i = 0; i < aspect.stacks.size(); i++) {
                int x = 3 + i % 9 * 18;
                int y = 43 + i / 9 * 18;
                stacks.add(new PositionedStack(aspect.stacks.get(i), x, y));
            }
        }
    }

    public static void processOutputs(ItemsWithAspect aspect, List<PositionedStack> stacks) {
        if (discovered(aspect.aspect)) {
            final ItemStack aspectStack = new ItemStack(ModItems.itemAspect);
            ItemAspect.setAspect(aspectStack, aspect.aspect);
            stacks.add(new PositionedStack(aspectStack, 75, 5));
        }
    }

    @SuppressWarnings("unused")
    public static void drawBG(Draw<ItemsWithAspect> draw, UniversalRecipeHandler<ItemsWithAspect>.CachedUniversalRecipe recipe) {
        draw.bind(Thaumcraft.MODID.toLowerCase(), "gui/gui_researchbook_overlay.png");

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        int textureSize = 16;
        float scaleFactor = 1.75F;
        int x = 83;
        int y = 13;
        GL11.glTranslatef(x, y, 0);
        GL11.glScalef(scaleFactor, scaleFactor, 1.0F);

        GL11.glTranslatef(-0.07F, 0.1F, 0);
        draw.drawSome(-textureSize / 2, -textureSize / 2, 20, 3, 16, 16);
        GL11.glTranslatef(0.07F, -0.1F, 0);

        GL11.glScalef(1 / scaleFactor, 1 / scaleFactor, 1.0F);
        GL11.glTranslatef(-x, -y, 0);
        GL11.glDisable(GL11.GL_BLEND);

        draw.bind(DarkCore.MODID, "gui/guiNeiItemStackBackground.png");
        draw.drawSome(
            2,
            42,
            0,
            0,
            163,
            74);
    }

    @SuppressWarnings("unused")
    public static void processLoading(List<ItemsWithAspect> aspects, UniversalRecipeHandler<ItemsWithAspect>.CacheBuilder builder, ItemStack stack) {
        if (stack.getItem() instanceof ItemAspect) {
            Aspect aspect = ItemAspect.getAspects(stack).getAspects()[0];

            if (discovered(aspect)) {
                List<ItemStack> containingItemStacks = findContainingItemStacks(aspect);
                if (!containingItemStacks.isEmpty()) {
                    List<List<ItemStack>> lists = splitItemStacks(containingItemStacks);
                    for (List<ItemStack> list : lists) {
                        builder.invoke(new ItemsWithAspect(aspect, list));
                    }
                }
            }
        }
    }

    private static List<ItemStack> findContainingItemStacks(Aspect aspect) {
        List<ItemStack> stacks = new ArrayList<>();
        List<String> list = Thaumcraft.proxy.getScannedObjects().get(Minecraft.getMinecraft().getSession().getUsername());

        if (list != null) {
            for (String itemStackCache : list) {
                try {
                    itemStackCache = itemStackCache.substring(1);

                    ItemStack is = GuiResearchRecipe.getFromCache(Integer.parseInt(itemStackCache));
                    if (is == null) {
                        continue;
                    }

                    AspectList tags = ThaumcraftCraftingManager.getObjectTags(is);
                    tags = ThaumcraftCraftingManager.getBonusTags(is, tags);

                    if (tags.size() <= 0) {
                        continue;
                    }

                    ItemStack is2 = is.copy();
                    is2.stackSize = tags.getAmount(aspect);
                    if (is2.stackSize <= 0) {
                        continue;
                    }

                    stacks.add(is2);
                } catch (NumberFormatException ignored) {}
            }
        }

        stacks.sort(Comparator.<ItemStack>comparingInt(itemStack -> itemStack.stackSize).reversed());

        return stacks;
    }

    private static List<List<ItemStack>> splitItemStacks(List<ItemStack> containingItemStacks) {
        List<List<ItemStack>> result = new LinkedList<>();
        int chunkSize = 36;

        for (int i = 0; i < containingItemStacks.size(); i += chunkSize) {
            int end = Math.min(i + chunkSize, containingItemStacks.size());
            result.add(new ArrayList<>(containingItemStacks.subList(i, end)));
        }

        return result;
    }

    public static class ItemsWithAspect {
        private final Aspect aspect;
        private final List<ItemStack> stacks;

        public ItemsWithAspect(Aspect aspect, List<ItemStack> stacks) {
            this.aspect = aspect;
            this.stacks = stacks;
        }
    }
}
