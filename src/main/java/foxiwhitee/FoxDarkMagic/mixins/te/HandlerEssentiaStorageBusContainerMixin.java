package foxiwhitee.FoxDarkMagic.mixins.te;

import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.tile.TileSingularAlchemicalFurnace;
import modtweaker2.mods.thaumcraft.aspect.IAspectStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.aspects.IAspectContainer;

import java.util.List;

@Mixin(targets = "thaumicenergistics.common.inventory.HandlerEssentiaStorageBusContainer", remap = false)
public abstract class HandlerEssentiaStorageBusContainerMixin {
    @Shadow
    private IAspectContainer aspectContainer;

    @Inject(
        method = "getContainerEssentia",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;isEmpty()Z",
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    private void onGetContainerEssentia(CallbackInfoReturnable<List<IAspectStack>> cir) {
        if (DarkConfig.singularArcaneFurnaceIgnoreStorageBus && this.aspectContainer instanceof TileSingularAlchemicalFurnace) {
            cir.setReturnValue(null);
        }
    }
}
