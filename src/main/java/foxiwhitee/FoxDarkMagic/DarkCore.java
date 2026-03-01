package foxiwhitee.FoxDarkMagic;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import foxiwhitee.FoxDarkMagic.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

import static foxiwhitee.FoxDarkMagic.DarkCore.*;

@Mod(modid = MODID, name = MODNAME, version = VERSION, dependencies = DEPEND)
public class DarkCore {
    public static final String
        MODID = "foxdarkmagic",
        MODNAME = "FoxDarkMagic",
        VERSION = "1.0.0",
        DEPEND = "required-after:Thaumcraft;required-after:foxlib;after:appliedenergistics2;after:thaumicenergistics;";

    public static final CreativeTabs TAB = new CreativeTabs("FOX_DARK_MAGIC_TAB") {
        @Override
        public Item getTabIconItem() {
            return Item.getItemFromBlock(Blocks.bedrock);
        }
    };

    @Mod.Instance(MODID)
    public static DarkCore instance;

    @SidedProxy(clientSide = "foxiwhitee.FoxDarkMagic.proxy.ClientProxy", serverSide = "foxiwhitee.FoxDarkMagic.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        proxy.preInit(e);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        proxy.postInit(e);
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        proxy.afterInit(event);
    }
}
