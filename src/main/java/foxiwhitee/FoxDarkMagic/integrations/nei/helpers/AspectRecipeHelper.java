package foxiwhitee.FoxDarkMagic.integrations.nei.helpers;

import codechicken.nei.PositionedStack;
import com.djgiannuzz.thaumcraftneiplugin.ModItems;
import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import foxiwhitee.FoxLib.nei.Draw;
import foxiwhitee.FoxLib.nei.UniversalRecipeHandler;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.ArrayUtils;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.Thaumcraft;

import java.util.List;

public class AspectRecipeHelper {
    private static boolean discovered(Aspect aspect) {
        return Thaumcraft.proxy.playerKnowledge.hasDiscoveredAspect(Minecraft.getMinecraft().getSession().getUsername(), aspect);
    }

    public static void processInputs(Aspect aspect, List<PositionedStack> stacks) {
        if (!aspect.isPrimal() && discovered(aspect)) {
            Aspect[] components = aspect.getComponents();
            if (discovered(components[0]) && discovered(components[1])) {
                ItemStack firstIngred = new ItemStack(ModItems.itemAspect);
                ItemAspect.setAspect(firstIngred, components[0]);
                ItemStack secondIngred = new ItemStack(ModItems.itemAspect);
                ItemAspect.setAspect(secondIngred, components[1]);

                stacks.add(new PositionedStack(firstIngred, 43 + 32, 6));
                stacks.add(new PositionedStack(secondIngred, 43 + 64, 6));
            }
        }
    }

    public static void processOutputs(Aspect aspect, List<PositionedStack> stacks) {
        if (discovered(aspect)) {
            ItemStack aspectStack = new ItemStack(ModItems.itemAspect);
            ItemAspect.setAspect(aspectStack, aspect);

            if (aspect.isPrimal()) {
                stacks.add(new PositionedStack(aspectStack, 75, 6));
            } else {
                stacks.add(new PositionedStack(aspectStack, 43, 6));
            }
        }
    }

    public static void drawBG(Draw<Aspect> draw, UniversalRecipeHandler<Aspect>.CachedUniversalRecipe recipe) {
        if (recipe.inputs.isEmpty()) {
            String text = LocalizationUtils.localize("tc.aspect.primal");
            int x = 83 - (Minecraft.getMinecraft().fontRenderer.getStringWidth(text) / 2);
            draw.drawString(text, x, 25);
        } else {
            draw.drawString("=", 41 + 24, 10);
            draw.drawString("+", 41 + 56, 10);
        }
    }

    @SuppressWarnings("unused")
    public static void processLoading(List<Aspect> aspects, UniversalRecipeHandler<Aspect>.CacheBuilder builder, ItemStack stack) {
        if (stack.getItem() instanceof ItemAspect) {
            Aspect aspect = ItemAspect.getAspects(stack).getAspects()[0];
            if (discovered(aspect)) {
                builder.invoke(aspect);
            }
        }
    }

    @SuppressWarnings("unused")
    public static void processUsage(List<Aspect> aspects, UniversalRecipeHandler<Aspect>.CacheBuilder builder, ItemStack stack) {
        if (stack.getItem() instanceof ItemAspect) {
            Aspect aspect = ItemAspect.getAspects(stack).getAspects()[0];

            if (discovered(aspect)) {
                for (Aspect compoundAspect : Aspect.getCompoundAspects()) {
                    if (ArrayUtils.contains(compoundAspect.getComponents(), aspect) && discovered(compoundAspect)) {
                        builder.invoke(compoundAspect);
                    }
                }
            }
        }
    }
}
