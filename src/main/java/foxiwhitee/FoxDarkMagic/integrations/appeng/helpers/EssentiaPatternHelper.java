package foxiwhitee.FoxDarkMagic.integrations.appeng.helpers;

import appeng.api.storage.data.IAEItemStack;
import appeng.helpers.UltimatePatternHelper;
import appeng.util.item.AEItemStack;
import foxiwhitee.FoxDarkMagic.integrations.appeng.api.IEssentiaPatternHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import thaumicenergistics.common.storage.AEEssentiaStack;

import java.util.ArrayList;
import java.util.List;

public class EssentiaPatternHelper extends UltimatePatternHelper implements IEssentiaPatternHelper {
    private final AEEssentiaStack[] essentiaStacks;
    private final IAEItemStack[] itemStacks;

    public EssentiaPatternHelper(ItemStack is) {
        super(is);
        final NBTTagCompound encodedValue = is.getTagCompound();

        if (encodedValue == null || !encodedValue.hasKey("in")) {
            throw new IllegalArgumentException("No pattern here!");
        }
        NBTTagList inTag = encodedValue.getTagList("in", 10);
        List<AEEssentiaStack> essentiaStacks = new ArrayList<>();
        List<IAEItemStack> stacks = new ArrayList<>();
        for (int i = 0; i < inTag.tagCount(); i++) {
            NBTTagCompound tag = inTag.getCompoundTagAt(i);
            IAEItemStack itemStack = AEItemStack.loadItemStackFromNBT(tag);
            if (itemStack != null) {
                stacks.add(itemStack);
                continue;
            }
            AEEssentiaStack essentiaStack = AEEssentiaStack.loadStackFromNBT(tag);
            if (essentiaStack != null) {
                essentiaStacks.add(essentiaStack);
            }
        }
        this.essentiaStacks = essentiaStacks.toArray(new AEEssentiaStack[0]);
        this.itemStacks = stacks.toArray(new IAEItemStack[0]);
    }

    @Override
    public AEEssentiaStack[] getAspects() {
        return essentiaStacks;
    }

    @Override
    public IAEItemStack[] getOnlyItems() {
        return itemStacks;
    }
}
