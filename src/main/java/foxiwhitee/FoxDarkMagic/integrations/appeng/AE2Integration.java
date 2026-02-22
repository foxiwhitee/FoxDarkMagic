package foxiwhitee.FoxDarkMagic.integrations.appeng;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import foxiwhitee.FoxDarkMagic.integrations.IIntegration;
import foxiwhitee.FoxDarkMagic.integrations.Integration;
import foxiwhitee.FoxDarkMagic.tile.TileSingularAlchemicalFurnace;
import thaumicenergistics.api.ThEApi;

import java.util.Objects;

@Integration(modid = "thaumicenergistics")
public class AE2Integration implements IIntegration {
    @Override
    public void preInit(FMLPreInitializationEvent paramFMLPreInitializationEvent) {

    }

    @Override
    public void init(FMLInitializationEvent paramFMLInitializationEvent) {
        Objects.requireNonNull(ThEApi.instance()).transportPermissions().addAspectStorageTileToExtractPermissions(TileSingularAlchemicalFurnace.class);
    }

    @Override
    public void postInit(FMLPostInitializationEvent paramFMLPostInitializationEvent) {

    }
}
