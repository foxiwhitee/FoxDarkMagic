package foxiwhitee.FoxDarkMagic.integrations.appeng;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import foxiwhitee.FoxDarkMagic.DarkCore;
import foxiwhitee.FoxDarkMagic.config.ContentConfig;
import foxiwhitee.FoxDarkMagic.integrations.IIntegration;
import foxiwhitee.FoxDarkMagic.integrations.Integration;
import foxiwhitee.FoxDarkMagic.integrations.appeng.item.ItemAEPart;
import foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.client.*;
import foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.server.C2SBusCraftingModeChangePacket;
import foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.server.C2SBusRedstoneModeChangePacket;
import foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.server.C2SBusRequestFilterListPacket;
import foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.server.C2SBusVoidModeChangePacket;
import foxiwhitee.FoxDarkMagic.integrations.appeng.utils.GuiHandler;
import foxiwhitee.FoxDarkMagic.tile.TileSingularAlchemicalFurnace;
import foxiwhitee.FoxLib.api.FoxLibApi;
import foxiwhitee.FoxLib.registries.RegisterUtils;
import thaumicenergistics.api.ThEApi;

import java.util.Objects;

@Integration(modid = "thaumicenergistics")
public class AE2Integration implements IIntegration {
    public static final ItemAEPart parts = new ItemAEPart();

    @Override
    public void preInit(FMLPreInitializationEvent paramFMLPreInitializationEvent) {
        if (ContentConfig.enableFastBuses) {
            RegisterUtils.registerItem(parts);
        }
    }

    @Override
    public void init(FMLInitializationEvent paramFMLInitializationEvent) {
        Objects.requireNonNull(ThEApi.instance()).transportPermissions().addAspectStorageTileToExtractPermissions(TileSingularAlchemicalFurnace.class);

        var pr = FoxLibApi.instance.registries().registerPacket();
        pr.register(S2CAspectSlotPacket.class);
        pr.register(S2CBusCraftingModePacket.class);
        pr.register(S2CBusFilterSizePacket.class);
        pr.register(S2CBusHasCraftingCardPacket.class);
        pr.register(S2CBusRedstoneControlledPacket.class);
        pr.register(S2CBusRedstoneModePacket.class);
        pr.register(S2CBusStatePacket.class);
        pr.register(S2CBusVoidModePacket.class);
        pr.register(C2SBusCraftingModeChangePacket.class);
        pr.register(C2SBusRedstoneModeChangePacket.class);
        pr.register(C2SBusRequestFilterListPacket.class);
        pr.register(C2SBusVoidModeChangePacket.class);
    }

    @Override
    public void postInit(FMLPostInitializationEvent paramFMLPostInitializationEvent) {
        NetworkRegistry.INSTANCE.registerGuiHandler(DarkCore.instance, new GuiHandler());
    }
}
