package foxiwhitee.FoxDarkMagic.integrations.appeng.helpers;

import appeng.api.AEApi;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import appeng.container.ContainerNull;
import appeng.util.ItemSorters;
import appeng.util.Platform;
import appeng.util.item.AEItemStack;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static appeng.helpers.PatternHelper.convertToCondensedList;

//Based on the code from AE2 GTNH 803
public class AdvancedPatternHelper implements ICraftingPatternDetails, Comparable<AdvancedPatternHelper> {
    private final ItemStack patternItem;
    private final InventoryCrafting crafting;
    private final InventoryCrafting testFrame;
    private final IAEItemStack correctOutput;
    private final IRecipe standardRecipe;
    private final IAEItemStack[] condensedInputs;
    private final IAEItemStack[] condensedOutputs;
    private final IAEItemStack[] inputs;
    private final IAEItemStack[] outputs;
    private final boolean isCrafting;
    private final boolean canSubstitute;
    private final boolean canBeSubstitute;
    private final Set<TestLookup> failCache = new HashSet<>();
    private final Set<TestLookup> passCache = new HashSet<>();
    private final IAEItemStack pattern;
    private int priority = 0;

    public AdvancedPatternHelper(ItemStack is, World w, int width, int height) {
        final NBTTagCompound encodedValue = is.getTagCompound();

        if (encodedValue == null) {
            throw new IllegalArgumentException("No pattern here!");
        }

        this.crafting = new InventoryCrafting(new ContainerNull(), width, height);
        this.testFrame = new InventoryCrafting(new ContainerNull(), width, height);

        final NBTTagList inTag = encodedValue.getTagList("in", 10);
        final NBTTagList outTag = encodedValue.getTagList("out", 10);
        this.isCrafting = encodedValue.getBoolean("crafting");

        this.canSubstitute = encodedValue.getBoolean("substitute");
        this.canBeSubstitute = encodedValue.getBoolean("beSubstitute");
        this.patternItem = is;
        if (encodedValue.hasKey("author")) {
            final ItemStack forComparison = this.patternItem.copy();
            forComparison.stackTagCompound.removeTag("author");
            this.pattern = AEItemStack.create(forComparison);
        } else {
            this.pattern = AEItemStack.create(is);
        }

        final List<IAEItemStack> in = new ArrayList<>();
        final List<IAEItemStack> out = new ArrayList<>();

        for (int x = 0; x < inTag.tagCount(); x++) {
            final NBTTagCompound tag = inTag.getCompoundTagAt(x);
            final IAEItemStack igs = AEItemStack.loadItemStackFromNBT(tag);

            if (igs == null && !tag.hasNoTags()) {
                throw new IllegalStateException("No pattern here!");
            }
            ItemStack gs = igs == null ? null : igs.getItemStack();

            if (gs != null && (!this.isCrafting || !gs.hasTagCompound())) {
                this.crafting.setInventorySlotContents(x, gs);
                this.markItemAs(x, gs, TestStatus.ACCEPT);
            }

            in.add(AEApi.instance().storage().createItemStack(gs));
            this.testFrame.setInventorySlotContents(x, gs);
        }

        if (this.isCrafting) {
            this.standardRecipe = Platform.findMatchingRecipe(this.crafting, w);

            if (this.standardRecipe != null) {
                this.correctOutput = AEItemStack.create(this.standardRecipe.getCraftingResult(this.crafting));
                out.add(correctOutput);
            } else {
                throw new IllegalStateException("No pattern here!");
            }
        } else {
            this.standardRecipe = null;
            this.correctOutput = null;

            for (int x = 0; x < outTag.tagCount(); x++) {
                final NBTTagCompound tag = outTag.getCompoundTagAt(x);
                final IAEItemStack gs = AEItemStack.loadItemStackFromNBT(tag);

                if (gs != null) {
                    out.add(gs);
                } else if (!tag.hasNoTags()) {
                    throw new IllegalStateException("No pattern here!");
                }
            }
        }

        this.outputs = out.toArray(new IAEItemStack[0]);
        this.inputs = in.toArray(new IAEItemStack[0]);

        this.condensedInputs = convertToCondensedList(this.inputs);
        this.condensedOutputs = convertToCondensedList(this.outputs);

        if (condensedInputs.length == 0 || condensedOutputs.length == 0) {
            throw new IllegalStateException("No pattern here!");
        }
    }

    private void markItemAs(final int slotIndex, final ItemStack i, final TestStatus b) {
        if (b == TestStatus.TEST || i.hasTagCompound()) {
            return;
        }

        (b == TestStatus.ACCEPT ? this.passCache : this.failCache).add(new TestLookup(slotIndex, i));
    }

    @Override
    public ItemStack getPattern() {
        return this.patternItem;
    }

    @Override
    public synchronized boolean isValidItemForSlot(final int slotIndex, final IAEStack<?> i, final World w) {
        if (isCrafting) {
            return isValidItemForSlot(slotIndex, ((IAEItemStack) i).getItemStack(), w);
        } else {
            throw new IllegalStateException("Only crafting recipes supported.");
        }
    }

    @Override
    public synchronized boolean isValidItemForSlot(final int slotIndex, final ItemStack i, final World w) {
        if (!this.isCrafting) {
            throw new IllegalStateException("Only crafting recipes supported.");
        }

        final TestStatus result = this.getStatus(slotIndex, i);

        switch (result) {
            case ACCEPT -> {
                return true;
            }
            case DECLINE -> {
                return false;
            }
            default -> {}
        }

        for (int x = 0; x < this.crafting.getSizeInventory(); x++) {
            this.testFrame.setInventorySlotContents(x, this.crafting.getStackInSlot(x));
        }

        this.testFrame.setInventorySlotContents(slotIndex, i);

        if (this.standardRecipe.matches(this.testFrame, w)) {
            final ItemStack testOutput = this.standardRecipe.getCraftingResult(this.testFrame);

            if (Platform.isSameItemPrecise(this.correctOutput == null ? null : this.correctOutput.getItemStack(), testOutput)) {
                this.testFrame.setInventorySlotContents(slotIndex, this.crafting.getStackInSlot(slotIndex));
                this.markItemAs(slotIndex, i, TestStatus.ACCEPT);
                return true;
            }
        } else {
            final ItemStack testOutput = CraftingManager.getInstance().findMatchingRecipe(this.testFrame, w);

            if (Platform.isSameItemPrecise(this.correctOutput == null ? null : this.correctOutput.getItemStack(), testOutput)) {
                this.testFrame.setInventorySlotContents(slotIndex, this.crafting.getStackInSlot(slotIndex));
                this.markItemAs(slotIndex, i, TestStatus.ACCEPT);
                return true;
            }
        }

        this.markItemAs(slotIndex, i, TestStatus.DECLINE);
        return false;
    }

    @Override
    public boolean isCraftable() {
        return this.isCrafting;
    }

    @Override
    public IAEItemStack[] getInputs() {
        return this.inputs;
    }

    @Override
    public IAEItemStack[] getCondensedInputs() {
        return this.condensedInputs;
    }

    @Override
    public IAEItemStack[] getCondensedOutputs() {
        return this.condensedOutputs;
    }

    @Override
    public IAEItemStack[] getOutputs() {
        return this.outputs;
    }

    @Override
    public boolean canSubstitute() {
        return this.canSubstitute;
    }

    @Override
    public boolean canBeSubstitute() {
        return this.canBeSubstitute;
    }

    @Override
    public ItemStack getOutput(final InventoryCrafting craftingInv, final World w) {
        if (!this.isCrafting) {
            throw new IllegalStateException("Only crafting recipes supported.");
        }

        for (int x = 0; x < craftingInv.getSizeInventory(); x++) {
            if (!this.isValidItemForSlot(x, craftingInv.getStackInSlot(x), w)) {
                return null;
            }
        }

        if (this.outputs != null && this.outputs.length > 0) {
            return this.outputs[0].getItemStack();
        }

        return null;
    }

    private TestStatus getStatus(final int slotIndex, final ItemStack i) {
        if (this.crafting.getStackInSlot(slotIndex) == null) {
            return i == null ? TestStatus.ACCEPT : TestStatus.DECLINE;
        }

        if (i == null) {
            return TestStatus.DECLINE;
        }

        if (i.hasTagCompound()) {
            return TestStatus.TEST;
        }

        if (this.passCache.contains(new TestLookup(slotIndex, i))) {
            return TestStatus.ACCEPT;
        }

        if (this.failCache.contains(new TestLookup(slotIndex, i))) {
            return TestStatus.DECLINE;
        }

        return TestStatus.TEST;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public void setPriority(final int priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(final AdvancedPatternHelper o) {
        return ItemSorters.compareInt(o.priority, this.priority);
    }

    @Override
    public int hashCode() {
        return this.pattern.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }

        final AdvancedPatternHelper other = (AdvancedPatternHelper) obj;

        if (this.pattern != null && other.pattern != null) {
            return this.pattern.equals(other.pattern);
        }
        return false;
    }

    private static final class TestLookup {
        private final int slot;
        private final int ref;
        private final int hash;

        public TestLookup(final int slot, final ItemStack i) {
            this(slot, i.getItem(), i.getItemDamage());
        }

        public TestLookup(final int slot, final Item item, final int dmg) {
            this.slot = slot;
            this.ref = (dmg << Platform.DEF_OFFSET) | (Item.getIdFromItem(item) & 0xffff);
            final int offset = 3 * slot;
            this.hash = (this.ref << offset) | (this.ref >> (offset + 32));
        }

        @Override
        public int hashCode() {
            return this.hash;
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean equality;

            if (obj instanceof TestLookup b) {

                equality = b.slot == this.slot && b.ref == this.ref;
            } else {
                equality = false;
            }

            return equality;
        }
    }

    private enum TestStatus {
        ACCEPT,
        DECLINE,
        TEST
    }
}
