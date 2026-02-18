package foxiwhitee.FoxDarkMagic.utils;

import appeng.core.Api;
import appeng.items.misc.ItemEncodedPattern;
import foxiwhitee.FoxDarkMagic.item.ItemInfinityStorageUpgrade;
import foxiwhitee.FoxDarkMagic.item.ItemStackUpgrade;
import foxiwhitee.FoxLib.container.slots.SlotFiltered;
import foxiwhitee.FoxLib.utils.helpers.ItemStackUtil;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.blocks.ItemJarFilled;
import thaumcraft.common.blocks.ItemJarNode;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

import java.util.Objects;
import java.util.function.Predicate;

public enum Filters {
    PATTERNS("patterns", stack -> itemInstanceof(stack, ItemEncodedPattern.class)),
    EMPTY_PATTERNS("patternsEmpty", stack -> ItemStackUtil.stackEquals(stack, Api.INSTANCE.definitions().materials().blankPattern().maybeStack(1).orNull())),
    SINISTER_NODE("sinisterNode", stack -> itemInstanceof(stack, ItemJarNode.class) && ((ItemJarNode) Objects.requireNonNull(stack.getItem())).getNodeType(stack) == NodeType.DARK),
    JARS("jars", stack -> itemInstanceof(stack, ItemJarFilled.class)),
    STACK_UPGRADE("magicStack", stack -> itemInstanceof(stack, ItemStackUpgrade.class)),
    INF_STORAGE_UPGRADE("safInfAspStorage", stack -> itemInstanceof(stack, ItemInfinityStorageUpgrade.class)),
    ITEM_WITH_ASPECT("aspectItem", stack -> {
        AspectList al = ThaumcraftCraftingManager.getObjectTags(stack);
        al = ThaumcraftCraftingManager.getBonusTags(stack, al);
        return al.visSize() > 0;
    });

    private final String filter;
    Filters(String id, Predicate<ItemStack> filter) {
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
