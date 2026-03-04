package foxiwhitee.FoxDarkMagic.helpers;

import codechicken.nei.NEIServerUtils;
import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import foxiwhitee.FoxLib.utils.helpers.ItemStackUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipesHelper {
    public static boolean matchesArcaneShapeless(IInventory var1, List<Object> input) {
        ArrayList<Object> required = new ArrayList<>(input);

        for (int x = 0; x < 9; ++x) {
            ItemStack slot = var1.getStackInSlot(x);
            if (slot != null) {
                boolean inRecipe = false;

                for (Object next : required) {
                    boolean match = false;
                    if (next instanceof ItemStack stack) {
                        match = ItemStackUtil.stackEquals(stack, slot);
                    } else if (next instanceof List<?> list) {
                        for (Object item : list) {
                            match |= ItemStackUtil.matchesStackAndOther(slot, item);
                        }
                    }

                    if (match) {
                        inRecipe = true;
                        required.remove(next);
                        break;
                    }
                }

                if (!inRecipe) {
                    return false;
                }
            }
        }

        return required.isEmpty();
    }

    public static boolean matchesArcaneShaped(IInventory inv, ShapedArcaneRecipe recipe) {
        try {
            for(int x = 0; x <= 3 - recipe.width; ++x) {
                for(int y = 0; y <= 3 - recipe.height; ++y) {
                    if (checkMatch(inv, recipe, x, y, false)) {
                        return true;
                    }

                    Class<?> clazz = recipe.getClass();

                    Field field = clazz.getDeclaredField("mirrored");

                    field.setAccessible(true);

                    boolean mirrored = (boolean) field.get(recipe);
                    if (mirrored && checkMatch(inv, recipe, x, y, true)) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {}

        return false;
    }

    private static boolean checkMatch(IInventory inv, ShapedArcaneRecipe recipe, int startX, int startY, boolean mirror) {
        for(int x = 0; x < 3; ++x) {
            for(int y = 0; y < 3; ++y) {
                int subX = x - startX;
                int subY = y - startY;
                Object target = null;
                if (subX >= 0 && subY >= 0 && subX < recipe.width && subY < recipe.height) {
                    if (mirror) {
                        target = recipe.input[recipe.width - subX - 1 + subY * recipe.width];
                    } else {
                        target = recipe.input[subX + subY * recipe.width];
                    }
                }

                ItemStack slot = inv.getStackInSlot(x + y * 3);
                if (target instanceof ItemStack stack) {
                    if (!ItemStackUtil.stackEquals(stack, slot)) {
                        return false;
                    }
                } else if (!(target instanceof List<?>)) {
                    if (target == null && slot != null) {
                        return false;
                    }
                } else {
                    boolean matched = false;
                    if (target instanceof List<?> list) {
                        for (Object next : list) {
                            matched |= ItemStackUtil.matchesStackAndOther(slot, next);
                        }
                    }

                    if (!matched) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static List<CrucibleRecipe> getCrucibleRecipesByInput(ItemStack input) {
        List<CrucibleRecipe> list = new ArrayList<>();
        for (Object r : ThaumcraftApi.getCraftingRecipes()) {
            if (r == null) {
                continue;
            }
            if (!(r instanceof CrucibleRecipe tcRecipe)) {
                continue;
            }

            if (input.getItem() instanceof ItemAspect) {
                Aspect aspect = ItemAspect.getAspects(input).getAspects()[0];
                if (tcRecipe.aspects.aspects.containsKey(aspect)) {
                    list.add(tcRecipe);
                }
            } else {
                if (tcRecipe.catalystMatches(input)) {
                    list.add(tcRecipe);
                }
            }
        }
        return list;
    }

    public static List<CrucibleRecipe> getCrucibleRecipesByOutput(ItemStack result) {
        List<CrucibleRecipe> list = new ArrayList<>();
        for (Object r : ThaumcraftApi.getCraftingRecipes()) {
            if (r instanceof CrucibleRecipe recipe && recipe.getRecipeOutput() != null) {
                ItemStack output = recipe.getRecipeOutput();
                if (NEIServerUtils.areStacksSameTypeCraftingWithNBT(output, result)) {
                    list.add(recipe);
                }
            }
        }
        return list;
    }

    public static List<InfusionRecipe> getInfusionRecipesByOutput(ItemStack result) {
        List<InfusionRecipe> list = new ArrayList<>();
        for (Object r : ThaumcraftApi.getCraftingRecipes()) {
            if (r instanceof InfusionRecipe && ((InfusionRecipe) r).getRecipeOutput() instanceof ItemStack output) {
                if (NEIServerUtils.areStacksSameTypeCraftingWithNBT(output, result)) {
                    list.add((InfusionRecipe) r);
                }
            }
        }
        return list;
    }

    public static List<InfusionRecipe> getInfusionRecipesByInput(ItemStack input) {
        final ArrayList<InfusionRecipe> list = new ArrayList<>();

        Aspect inputAspect = null;
        boolean inputIsAspectItem = input != null && input.getItem() instanceof ItemAspect;
        if (inputIsAspectItem) {
            AspectList alt = ItemAspect.getAspects(input);
            if (alt != null && alt.getAspects() != null && alt.getAspects().length > 0) {
                inputAspect = alt.getAspects()[0];
            } else {
                return list;
            }
        }

        for (Object r : ThaumcraftApi.getCraftingRecipes()) {
            if (!(r instanceof InfusionRecipe raw)) continue;

            if (raw.getRecipeOutput() == null) continue;
            Object[] comps = raw.getComponents();
            if (comps == null) continue;

            boolean aspectsContain = false;
            if (inputIsAspectItem && inputAspect != null
                && raw.getAspects() != null
                && raw.getAspects().aspects != null) {
                aspectsContain = raw.getAspects().aspects.containsKey(inputAspect);
            }

            if (inputIsAspectItem) {
                if (aspectsContain) list.add(raw);
            } else {
                boolean centralMatches = ItemStackUtil.matchesStackAndOther(input, raw.getRecipeInput());

                boolean componentMatches = false;
                if (raw.getComponents() != null && raw.getComponents().length == 0) {
                    componentMatches = Arrays.stream(raw.getComponents()).anyMatch(c -> {
                        try {
                            return ItemStackUtil.matchesStackAndOther(input, c);
                        } catch (Throwable t) {
                            return false;
                        }
                    });
                }

                if (centralMatches || componentMatches) list.add(raw);
            }
        }

        return list;
    }
}
