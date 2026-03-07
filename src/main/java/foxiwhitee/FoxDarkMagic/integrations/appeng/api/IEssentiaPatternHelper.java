package foxiwhitee.FoxDarkMagic.integrations.appeng.api;

import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.storage.data.IAEItemStack;
import thaumicenergistics.common.storage.AEEssentiaStack;

public interface IEssentiaPatternHelper extends ICraftingPatternDetails {
    AEEssentiaStack[] getAspects();
    IAEItemStack[] getOnlyItems();
}
