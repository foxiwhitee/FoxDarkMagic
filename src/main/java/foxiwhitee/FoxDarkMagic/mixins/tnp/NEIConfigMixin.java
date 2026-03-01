package foxiwhitee.FoxDarkMagic.mixins.tnp;

import codechicken.nei.api.API;
import codechicken.nei.recipe.ICraftingHandler;
import codechicken.nei.recipe.IUsageHandler;
import com.djgiannuzz.thaumcraftneiplugin.nei.NEIConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = NEIConfig.class, remap = false)
public abstract class NEIConfigMixin {
    @Redirect(
        method = "loadConfig",
        at = @At(
            value = "INVOKE",
            target = "Lcodechicken/nei/api/API;registerRecipeHandler(Lcodechicken/nei/recipe/ICraftingHandler;)V"
        )
    )
    private void redirectRecipeRegistration(ICraftingHandler handler) {
        if (!(handler.getClass().getName().endsWith("AspectRecipeHandler"))) {
            API.registerRecipeHandler(handler);
        }
    }

    @Redirect(
        method = "loadConfig",
        at = @At(
            value = "INVOKE",
            target = "Lcodechicken/nei/api/API;registerUsageHandler(Lcodechicken/nei/recipe/IUsageHandler;)V"
        )
    )
    private void redirectUsageRegistration(IUsageHandler handler) {
        if (!(handler.getClass().getName().endsWith("AspectRecipeHandler"))) {
            API.registerUsageHandler(handler);
        }
    }
}
