package foxiwhitee.FoxDarkMagic.integrations.appeng;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import foxiwhitee.FoxDarkMagic.config.ContentConfig;
import foxiwhitee.FoxDarkMagic.integrations.IIntegration;
import foxiwhitee.FoxDarkMagic.integrations.Integration;
import foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.encoders.BlockArcaneEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.container.encoders.ContainerArcaneEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.item.ItemAEPart;
import foxiwhitee.FoxDarkMagic.integrations.appeng.item.patterns.ItemEncodedArcanePattern;
import foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.server.*;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders.TileArcaneEncoder;
import foxiwhitee.FoxDarkMagic.tile.TileSingularAlchemicalFurnace;
import foxiwhitee.FoxLib.api.FoxLibApi;
import foxiwhitee.FoxLib.registries.RegisterUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import thaumicenergistics.api.ThEApi;

import java.util.Objects;

@Integration(modid = "thaumicenergistics")
public class AE2Integration implements IIntegration {
    public static final ItemAEPart parts = new ItemAEPart();
    public static final Item encodedArcanePattern = new ItemEncodedArcanePattern("encodedArcanePattern");

    public static final Block arcaneEncoder = new BlockArcaneEncoder("arcaneEncoder");

    @Override
    public void preInit(FMLPreInitializationEvent paramFMLPreInitializationEvent) {
        if (ContentConfig.enableBasicArcaneMolecularAssembler || ContentConfig.enableAdvancedArcaneMolecularAssembler || ContentConfig.enableHybridArcaneMolecularAssembler || ContentConfig.enableUltimateArcaneMolecularAssembler) {
            RegisterUtils.registerItem(encodedArcanePattern);
            RegisterUtils.registerBlock(arcaneEncoder);
            RegisterUtils.registerTile(TileArcaneEncoder.class);
        }
        if (ContentConfig.enableFastBuses) {
            RegisterUtils.registerItem(parts);
        }
    }

    @Override
    public void init(FMLInitializationEvent paramFMLInitializationEvent) {
        FoxLibApi.instance.registries().registerGui()
                .register(BlockArcaneEncoder.class, TileArcaneEncoder.class, ContainerArcaneEncoder.class);
        Objects.requireNonNull(ThEApi.instance()).transportPermissions().addAspectStorageTileToExtractPermissions(TileSingularAlchemicalFurnace.class);

        var pr = FoxLibApi.instance.registries().registerPacket();
        pr.register(C2SEncodePacket.class);
    }

    @Override
    public void postInit(FMLPostInitializationEvent paramFMLPostInitializationEvent) {

    }
}
