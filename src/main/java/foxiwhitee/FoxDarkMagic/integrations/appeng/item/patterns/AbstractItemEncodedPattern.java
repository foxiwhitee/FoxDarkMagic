package foxiwhitee.FoxDarkMagic.integrations.appeng.item.patterns;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.storage.data.IAEStack;
import appeng.core.localization.GuiText;
import appeng.items.misc.ItemEncodedPattern;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import codechicken.nei.NEIClientConfig;
import foxiwhitee.FoxDarkMagic.DarkCore;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

import static appeng.helpers.PatternHelper.convertToCondensedAEList;
import static appeng.helpers.UltimatePatternHelper.loadIAEStackFromNBT;

public abstract class AbstractItemEncodedPattern extends ItemEncodedPattern {
    protected static final Locale locale = Locale.getDefault();

    public AbstractItemEncodedPattern(String name) {
        super();
        setUnlocalizedName(name);
        setCreativeTab(DarkCore.TAB);
        setTextureName(DarkCore.MODID + ":patterns/" + name);
    }

    @Override
    public void addCheckedInformation(ItemStack stack, EntityPlayer player, List<String> lines, boolean displayMoreInfo) {
        final NBTTagCompound encodedValue = stack.getTagCompound();

        if (encodedValue == null) {
            lines.add(EnumChatFormatting.RED + GuiText.InvalidPattern.getLocal());
            return;
        }

        final ICraftingPatternDetails details = this.getPatternForItem(stack, player.worldObj);
        final boolean substitute = encodedValue.getBoolean("substitute");
        final boolean beSubstitute = encodedValue.getBoolean("beSubstitute");
        final String author = encodedValue.getString("author");
        IAEStack<?>[] inItems;
        IAEStack<?>[] outItems;

        if (details == null) {
            final ItemStack unknownItem = new ItemStack(Blocks.fire);
            unknownItem.setStackDisplayName(GuiText.UnknownItem.getLocal());

            inItems = convertToCondensedAEList(loadIAEStackFromNBT(encodedValue.getTagList("in", 10), false, unknownItem));
            outItems = convertToCondensedAEList(loadIAEStackFromNBT(encodedValue.getTagList("out", 10), false, unknownItem));
        } else {
            inItems = details.getCondensedAEInputs();
            outItems = details.getCondensedAEOutputs();
        }

        boolean recipeIsBroken = isRecipeBroken(details);
        final List<String> in = new ArrayList<>();
        final List<String> out = new ArrayList<>();
        final List<String> aspects = new ArrayList<>();

        addAdditionallyText(aspects, details);

        final String substitutionLabel = EnumChatFormatting.YELLOW + GuiText.Substitute.getLocal() + " " + EnumChatFormatting.RESET;
        final String beSubstitutionLabel = EnumChatFormatting.YELLOW + GuiText.BeSubstitute.getLocal() + " " + EnumChatFormatting.RESET;
        final String canSubstitute = substitute ? EnumChatFormatting.RED + GuiText.Yes.getLocal() : GuiText.No.getLocal();
        final String canBeSubstitute = beSubstitute ? EnumChatFormatting.RED + GuiText.Yes.getLocal() : GuiText.No.getLocal();
        final String result = (outItems.length > 1 ? EnumChatFormatting.DARK_AQUA + GuiText.Results.getLocal() : EnumChatFormatting.DARK_AQUA + GuiText.Result.getLocal()) + ":" + EnumChatFormatting.RESET;
        final String ingredients = (inItems.length > 1 ? EnumChatFormatting.DARK_GREEN + GuiText.Ingredients.getLocal() : EnumChatFormatting.DARK_GREEN + GuiText.Ingredient.getLocal()) + ": " + EnumChatFormatting.RESET;
        final String holdShift = EnumChatFormatting.GRAY + GuiText.HoldShift.getLocal() + EnumChatFormatting.RESET;
        final String viewPattern = EnumChatFormatting.GRAY + String.format(GuiText.PatternView.getLocal(), NEIClientConfig.getKeyName("gui.pattern_view")) + EnumChatFormatting.RESET;

        recipeIsBroken = addInputInformation(inItems, in, ingredients)
            || recipeIsBroken;
        recipeIsBroken = addInformation(outItems, out, result, EnumChatFormatting.AQUA)
            || recipeIsBroken;

        if (recipeIsBroken) {
            lines.add(EnumChatFormatting.RED + GuiText.InvalidPattern.getLocal());
        } else {
            lines.addAll(out);
            if (GuiScreen.isShiftKeyDown()) {
                lines.addAll(in);
                lines.addAll(aspects);
            } else {
                lines.add(holdShift);
            }

            lines.add(viewPattern);
            lines.add(substitutionLabel + canSubstitute);
            lines.add(beSubstitutionLabel + canBeSubstitute);

            if (!StringUtils.isNullOrEmpty(author)) {
                lines.add(
                    EnumChatFormatting.LIGHT_PURPLE + GuiText.EncodedBy.getLocal(author)
                        + EnumChatFormatting.RESET);
            }
        }
    }

    @SuppressWarnings("all")
    protected boolean addInformation(final IAEStack<?>[] items, final List<String> lines, String label, EnumChatFormatting color) {
        final ItemStack unknownItem = new ItemStack(Blocks.fire);
        boolean recipeIsBroken = false;
        boolean first = true;
        List<IAEStack<?>> itemsList = Arrays.asList(items);
        List<IAEStack<?>> sortedItems = itemsList.stream().sorted(Comparator.comparingLong(IAEStack::getStackSize)).collect(Collectors.toList());
        boolean isFluid;
        EnumChatFormatting oldColor = color;

        for (int i = sortedItems.size() - 1; i >= 0; i--) {
            IAEStack<?> item = sortedItems.get(i);

            if (!recipeIsBroken && item.equals(AEItemStack.create(unknownItem))) {
                recipeIsBroken = true;
            }

            if (item instanceof IAEFluidStack) {
                label = EnumChatFormatting.GOLD + label;
                color = EnumChatFormatting.GOLD;
                isFluid = true;
            } else {
                color = oldColor;
                isFluid = false;
            }

            String itemCountText = NumberFormat.getNumberInstance(locale).format(item.getStackSize());
            String itemText;
            itemText = Platform.getItemDisplayName(item);
            String fullText = "   " + EnumChatFormatting.WHITE
                + itemCountText
                + EnumChatFormatting.RESET
                + (isFluid ? EnumChatFormatting.WHITE + "L " : " ")
                + EnumChatFormatting.RESET
                + color
                + itemText;

            if (first) {
                lines.add(label);
            }

            lines.add(fullText);
            first = false;
        }

        return recipeIsBroken;
    }

    @Override
    public abstract ICraftingPatternDetails getPatternForItem(ItemStack is, World w);

    protected abstract boolean isRecipeBroken(ICraftingPatternDetails details);

    protected abstract void addAdditionallyText(List<String> aspects, ICraftingPatternDetails details);

    protected boolean addInputInformation(final IAEStack<?>[] inItems, final List<String> in, String ingredients) {
        return addInformation(inItems, in, ingredients, EnumChatFormatting.GREEN);
    }
}
