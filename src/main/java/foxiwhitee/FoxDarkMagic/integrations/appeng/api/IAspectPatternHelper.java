package foxiwhitee.FoxDarkMagic.integrations.appeng.api;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import thaumcraft.api.aspects.AspectList;

public interface IAspectPatternHelper extends ICraftingPatternDetails {
    AspectList getAspects();
}
