package foxiwhitee.FoxDarkMagic.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import foxiwhitee.FoxDarkMagic.ModBlocks;
import foxiwhitee.FoxDarkMagic.ModItems;
import foxiwhitee.FoxDarkMagic.ModRecipes;
import foxiwhitee.FoxDarkMagic.integrations.IntegrationLoader;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        ModBlocks.registerBlocks();
        ModItems.registerItems();
        IntegrationLoader.preInit(event);
    }

    public void init(FMLInitializationEvent event) {
        IntegrationLoader.init(event);
    }

    public void postInit(FMLPostInitializationEvent event) {
        ModRecipes.initRecipes();
        IntegrationLoader.postInit(event);
    }
}
