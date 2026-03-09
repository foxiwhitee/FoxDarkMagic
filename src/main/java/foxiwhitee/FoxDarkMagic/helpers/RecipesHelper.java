package foxiwhitee.FoxDarkMagic.helpers;

import foxiwhitee.FoxLib.utils.helpers.ItemStackUtil;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagByte;
import net.minecraftforge.oredict.OreDictionary;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.wands.WandCap;
import thaumcraft.api.wands.WandRod;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.wands.ItemWandCasting;

import java.lang.reflect.Field;
import java.util.*;

public class RecipesHelper {
    public static Map.Entry<ItemStack, AspectList> matchesWand(IInventory inv) {
        ItemStack cap1 = inv.getStackInSlot(2);
        ItemStack cap2 = inv.getStackInSlot(6);
        ItemStack rod = inv.getStackInSlot(4);
        if (inv.getStackInSlot(0) == null &&
            inv.getStackInSlot(1) == null &&
            inv.getStackInSlot(3) == null &&
            inv.getStackInSlot(5) == null &&
            inv.getStackInSlot(7) == null &&
            inv.getStackInSlot(8) == null) {
            return checkMatch(cap1, cap2, rod);
        }
        return null;
    }

    private static Map.Entry<ItemStack, AspectList> checkMatch(ItemStack cap1, ItemStack cap2, ItemStack rod) {
        ItemStack out = null;
        AspectList as = new AspectList();
        String bc = null;
        String br = null;
        int cc = 0;
        int cr = 0;
        if (cap1 != null && rod != null && ItemStackUtil.stackEquals(cap1, cap2)) {
            for(WandCap wc : WandCap.caps.values()) {
                if (ItemStackUtil.stackEquals(cap1, wc.getItem())) {
                    bc = wc.getTag();
                    cc = wc.getCraftCost();
                    break;
                }
            }

            for(WandRod wr : WandRod.rods.values()) {
                if (ItemStackUtil.stackEquals(rod, wr.getItem())) {
                    br = wr.getTag();
                    cr = wr.getCraftCost();
                    break;
                }
            }

            if (bc != null && br != null && (!br.equals("wood") || !bc.equals("iron"))) {
                int cost = cc * cr;
                out = new ItemStack(ConfigItems.itemWandCasting, 1, cost);
                if (out.getItem() != null) {
                    ((ItemWandCasting) out.getItem()).setCap(out, WandCap.caps.get(bc));
                    ((ItemWandCasting) out.getItem()).setRod(out, WandRod.rods.get(br));
                }
                for(Aspect asp : Aspect.getPrimalAspects()) {
                    as.add(asp, cost);
                }
            }
        }
        if (out != null) {
            return new AbstractMap.SimpleEntry<>(out, as);
        } else {
            return null;
        }
    }

    public static Map.Entry<ItemStack, AspectList> matchesSceptre(IInventory inv) {
        ItemStack cap1 = inv.getStackInSlot(1);
        ItemStack cap2 = inv.getStackInSlot(5);
        ItemStack cap3 = inv.getStackInSlot(6);
        ItemStack rod = inv.getStackInSlot(4);
        ItemStack focus = inv.getStackInSlot(2);
        if (inv.getStackInSlot(0) == null &&
            inv.getStackInSlot(8) == null &&
            inv.getStackInSlot(3) == null &&
            inv.getStackInSlot(7) == null) {
            return checkMatch(cap1, cap2, cap3, rod, focus);
        }
        return null;
    }

    private static Map.Entry<ItemStack, AspectList> checkMatch(ItemStack cap1, ItemStack cap2, ItemStack cap3, ItemStack rod, ItemStack focus) {
        ItemStack out = null;
        AspectList as = new AspectList();
        String bc = null;
        String br = null;
        int cc = 0;
        int cr = 0;
        if (cap1 != null && rod != null && focus != null && ItemStackUtil.stackEquals(focus, new ItemStack(ConfigItems.itemResource, 1, 15)) && ItemStackUtil.stackEquals(cap1, cap2) && ItemStackUtil.stackEquals(cap1, cap3)) {
            for(WandCap wc : WandCap.caps.values()) {
                if (ItemStackUtil.stackEquals(cap1, wc.getItem())) {
                    bc = wc.getTag();
                    cc = wc.getCraftCost();
                    break;
                }
            }

            for(WandRod wr : WandRod.rods.values()) {
                if (ItemStackUtil.stackEquals(rod, wr.getItem())) {
                    br = wr.getTag();
                    cr = wr.getCraftCost();
                    break;
                }
            }

            if (bc != null && br != null) {
                int cost = (int)((float)(cc * cr) * 1.5F);
                out = new ItemStack(ConfigItems.itemWandCasting, 1, cost);
                if (out.getItem() != null) {
                    ((ItemWandCasting) out.getItem()).setCap(out, WandCap.caps.get(bc));
                    ((ItemWandCasting) out.getItem()).setRod(out, WandRod.rods.get(br));
                }
                out.setTagInfo("sceptre", new NBTTagByte((byte)1));

                for(Aspect asp : Aspect.getPrimalAspects()) {
                    as.add(asp, cost);
                }
            }
        }

        if (out != null) {
            return new AbstractMap.SimpleEntry<>(out, as);
        } else {
            return null;
        }
    }

    public static boolean matchesInfusion(IInventory inventory, InfusionRecipe recipe) {
        if (recipe.getRecipeInput() == null || inventory.getStackInSlot(0) == null) {
            return false;
        }
        ItemStack i2 = inventory.getStackInSlot(0);
        if (recipe.getRecipeInput().getItemDamage() == 32767) {
            i2.setItemDamage(32767);
        }

        if (!areItemStacksEqual(i2, recipe.getRecipeInput())) {
            return false;
        } else {
            List<ItemStack> ii = new ArrayList<>();

            for (int i = 1; i < inventory.getSizeInventory(); i++) {
                ItemStack st = inventory.getStackInSlot(i);
                if (st != null) {
                    ii.add(st);
                }
            }

            for(ItemStack comp : recipe.getComponents()) {
                boolean b = false;

                for(int a = 0; a < ii.size(); ++a) {
                    i2 = ii.get(a).copy();
                    if (comp.getItemDamage() == 32767) {
                        i2.setItemDamage(32767);
                    }

                    if (areItemStacksEqual(i2, comp)) {
                        ii.remove(a);
                        b = true;
                        break;
                    }
                }

                if (!b) {
                    return false;
                }
            }

            return ii.isEmpty();
        }
    }

    private static boolean areItemStacksEqual(ItemStack i1, ItemStack i2) {
        int[] ids = OreDictionary.getOreIDs(i1);
        if (ids.length > 0) {
            boolean b = false;
            for (int i : ids) {
                String key = OreDictionary.getOreName(i);
                if (key != null) {
                    b |= ItemStackUtil.matchesStackAndOther(i2, key);
                }
            }
            if (b) {
                return true;
            }
        }
        return ItemStackUtil.stackEquals(i1, i2);
    }

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

            if (tcRecipe.catalystMatches(input)) {
                list.add(tcRecipe);
            }
        }
        return list;
    }

}
