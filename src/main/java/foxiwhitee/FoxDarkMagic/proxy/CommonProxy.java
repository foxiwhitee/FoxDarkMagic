package foxiwhitee.FoxDarkMagic.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import foxiwhitee.FoxDarkMagic.*;
import foxiwhitee.FoxDarkMagic.blocks.BlockMatterDistorter;
import foxiwhitee.FoxDarkMagic.blocks.BlockSingularAlchemicalFurnace;
import foxiwhitee.FoxDarkMagic.container.ContainerMatterDistorter;
import foxiwhitee.FoxDarkMagic.container.ContainerSingularAlchemicalFurnace;
import foxiwhitee.FoxDarkMagic.entity.ItemEntityMoonStone;
import foxiwhitee.FoxDarkMagic.integrations.IntegrationLoader;
import foxiwhitee.FoxDarkMagic.network.packets.C2SSelectAspectPacket;
import foxiwhitee.FoxDarkMagic.researches.json.ResearchesHandler;
import foxiwhitee.FoxDarkMagic.tile.TileMatterDistorter;
import foxiwhitee.FoxDarkMagic.tile.TileSingularAlchemicalFurnace;
import foxiwhitee.FoxLib.api.FoxLibApi;

public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        ModBlocks.registerBlocks();
        ModItems.registerItems();
        ResearchesHandler.loadResearchesLocations(event);
        ThaumicThinks.init();
        IntegrationLoader.preInit(event);
    }

    public void init(FMLInitializationEvent event) {
        EntityRegistry.registerModEntity(ItemEntityMoonStone.class, "ItemEntityMoonStone", 0, DarkCore.MODID, 82, 3, true);
        FoxLibApi.instance.registries().registerGui()
                .register(BlockMatterDistorter.class, TileMatterDistorter.class, ContainerMatterDistorter.class)
                .register(BlockSingularAlchemicalFurnace.class, TileSingularAlchemicalFurnace.class, ContainerSingularAlchemicalFurnace.class);
        FoxLibApi.instance.registries().registerPacket().register(C2SSelectAspectPacket.class);
        ResearchesHandler.init();
        IntegrationLoader.init(event);
    }

    public void postInit(FMLPostInitializationEvent event) {
        ModRecipes.initRecipes();
        IntegrationLoader.postInit(event);
    }
}
