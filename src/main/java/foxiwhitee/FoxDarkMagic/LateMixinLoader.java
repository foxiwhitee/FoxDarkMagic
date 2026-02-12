package foxiwhitee.FoxDarkMagic;

import cpw.mods.fml.common.Loader;
import io.github.tox1cozz.mixinbooterlegacy.ILateMixinLoader;
import io.github.tox1cozz.mixinbooterlegacy.LateMixin;

import java.util.Arrays;
import java.util.List;

@LateMixin
public class LateMixinLoader implements ILateMixinLoader {

    @Override
    public List<String> getMixinConfigs() {
        return Arrays.asList(
            "mixins.FoxDarkMagic_Thaumcraft.json"
        );
    }

    @Override
    public boolean shouldMixinConfigQueue(String mixinConfig) {
        if (mixinConfig.equals("mixins.FoxDarkMagic_Thaumcraft")) {
            return Loader.isModLoaded("Thaumcraft");
        }
        return true;
    }

}
