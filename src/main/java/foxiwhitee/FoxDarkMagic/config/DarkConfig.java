package foxiwhitee.FoxDarkMagic.config;

import foxiwhitee.FoxLib.config.Config;
import foxiwhitee.FoxLib.config.ConfigValue;

@Config(folder = "Fox-Mods", name = "FoxDarkMagic")
public class DarkConfig {
    @ConfigValue(desc = "Enable tooltips?")
    public static boolean enableTooltips = true;

}
