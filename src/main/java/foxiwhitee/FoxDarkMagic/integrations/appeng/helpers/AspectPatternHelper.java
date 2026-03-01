package foxiwhitee.FoxDarkMagic.integrations.appeng.helpers;

import foxiwhitee.FoxDarkMagic.integrations.appeng.api.IAspectPatternHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import thaumcraft.api.aspects.AspectList;

public class AspectPatternHelper extends AdvancedPatternHelper implements IAspectPatternHelper {
    private final AspectList aspectList;

    public AspectPatternHelper(ItemStack is, World w) {
        super(is, w, 3, 3);
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
