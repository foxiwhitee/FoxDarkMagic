package foxiwhitee.FoxDarkMagic.proxy;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;
import java.util.List;

public class ClientProxy extends CommonProxy {
    public static final List<Aspect> allAspects = new ArrayList<>();

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }

    @Override
    public void afterInit(FMLLoadCompleteEvent event) {
        super.afterInit(event);
        allAspects.addAll(Aspect.aspects.values());
    }
}
