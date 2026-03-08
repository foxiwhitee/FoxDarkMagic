package foxiwhitee.FoxDarkMagic.integrations.appeng.utils;

import foxiwhitee.FoxDarkMagic.integrations.appeng.item.ItemAbrahamSeal;
import foxiwhitee.FoxLib.container.slots.SlotFiltered;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public enum FiltersAE2 {
    ABRAHAM_SEAL("abrahamSeal", stack -> itemInstanceof(stack, ItemAbrahamSeal.class));

    private final String filter;
    FiltersAE2(String id, Predicate<ItemStack> filter) {
        this.filter = id;
        SlotFiltered.filters.put(id, filter);
    }

    public String getFilter() {
        return filter;
    }

    private static boolean itemInstanceof(ItemStack stack, Class<?>... classes) {
        boolean b = false;
        for (Class<?> clazz : classes) {
            if (clazz.isInstance(stack.getItem())) {
                b = true;
                break;
            }
        }
        return b;
    }
}
