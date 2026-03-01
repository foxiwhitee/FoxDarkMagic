package foxiwhitee.FoxDarkMagic;

import cpw.mods.fml.common.Loader;
import io.github.tox1cozz.mixinbooterlegacy.ILateMixinLoader;
import io.github.tox1cozz.mixinbooterlegacy.LateMixin;

import java.util.Arrays;
import java.util.List;

@LateMixin
@SuppressWarnings("unused")
public class LateMixinLoader implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList(
            "mixins.FoxDarkMagic_Thaumcraft.json",
            "mixins.FoxDarkMagic_TE.json",
            "mixins.FoxDarkMagic_TNP.json"
        );
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        return switch (mixinConfig) {
            case "mixins.FoxDarkMagic_Thaumcraft.json" -> Loader.isModLoaded("Thaumcraft");
            case "mixins.FoxDarkMagic_TE.json" -> Loader.isModLoaded("thaumicenergistics");
            case "mixins.FoxDarkMagic_TNP.json" -> Loader.isModLoaded("thaumcraftneiplugin");
            default -> false;
        };
    }

}
