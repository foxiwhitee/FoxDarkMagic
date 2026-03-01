package foxiwhitee.FoxDarkMagic.integrations.nei;

import foxiwhitee.FoxDarkMagic.DarkCore;
import foxiwhitee.FoxDarkMagic.integrations.nei.helpers.AspectInItemsHelper;
import foxiwhitee.FoxDarkMagic.integrations.nei.helpers.AspectRecipeHelper;
import foxiwhitee.FoxLib.nei.NeiConfiguration;
import foxiwhitee.FoxLib.nei.NeiConfigurationMethod;
import foxiwhitee.FoxLib.nei.NeiProcessor;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static foxiwhitee.FoxDarkMagic.proxy.ClientProxy.allAspects;

@NeiConfiguration(modID = DarkCore.MODID, modVersion = DarkCore.VERSION, modName = DarkCore.MODNAME, needMods = "thaumcraftneiplugin")

@SuppressWarnings("unused")
public class DarkConfiguration extends NeiProcessor {

    @NeiConfigurationMethod
    public void aspectRecipe() {
        var handler = this.<Aspect>newRecipeHandler("aspectRecipes")
            .recipes(allAspects)
            .perPage(7)
            .processInputs(AspectRecipeHelper::processInputs)
            .processOutputs(AspectRecipeHelper::processOutputs)
            .drawBG().addMethod(AspectRecipeHelper::drawBG)
            .processLoading(AspectRecipeHelper::processLoading)
            .processUsage(AspectRecipeHelper::processUsage)
            .register();

        configuration()
            .addHandlerInfo(handler.getHandlerId(), "Thaumcraft:ItemResearchNotes", 192, 43, 7);
    }

    @NeiConfigurationMethod
    public void aspectInItems() {
        var handler = this.<AspectInItemsHelper.ItemsWithAspect>newRecipeHandler("aspectInItems")
            .recipes(allAspects.stream().map(aspect -> new AspectInItemsHelper.ItemsWithAspect(aspect, new ArrayList<>())).collect(Collectors.toList()))
            .perPage(2)
            .processInputs(AspectInItemsHelper::processInputs)
            .processOutputs(AspectInItemsHelper::processOutputs)
            .drawBG().addMethod(AspectInItemsHelper::drawBG)
            .processLoading(AspectInItemsHelper::processLoading)
            .processUsage((l, c, i) -> {})
            .register();

        configuration()
            .addHandlerInfo(handler.getHandlerId(), "Thaumcraft:ItemResearchNotes", 192, 147, 2);
    }
}
