package foxiwhitee.FoxDarkMagic;

import cpw.mods.fml.common.Loader;
import io.github.tox1cozz.mixinbooterlegacy.ILateMixinLoader;
import io.github.tox1cozz.mixinbooterlegacy.LateMixin;

import java.util.Arrays;
import java.util.List;

@LateMixin
@SuppressWarnings("all")
public class LateMixinLoader implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList(
            "mixins.FoxDarkMagic_Thaumcraft.json"
        );
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        return switch (mixinConfig) {
            case "mixins.FoxDarkMagic_Thaumcraft.json" -> Loader.isModLoaded("Thaumcraft");
            default -> false;
        };
    }

}
