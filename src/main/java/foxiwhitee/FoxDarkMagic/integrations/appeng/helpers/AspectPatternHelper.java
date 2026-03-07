package foxiwhitee.FoxDarkMagic.integrations.appeng.helpers;

import appeng.helpers.UltimatePatternHelper;
import foxiwhitee.FoxDarkMagic.integrations.appeng.api.IAspectPatternHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.AspectList;

public class AspectPatternHelper extends UltimatePatternHelper implements IAspectPatternHelper {
    private final AspectList aspectList;

    public AspectPatternHelper(ItemStack is) {
        super(is);
        final NBTTagCompound encodedValue = is.getTagCompound();

        if (encodedValue == null || !encodedValue.hasKey("vis")) {
            throw new IllegalArgumentException("No pattern here!");
        }
        NBTTagCompound tag = encodedValue.getCompoundTag("vis");
        aspectList = new AspectList();
        aspectList.readFromNBT(tag);
    }

    @Override
    public AspectList getAspects() {
        return aspectList;
    }
}
