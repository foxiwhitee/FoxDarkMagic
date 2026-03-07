package foxiwhitee.FoxDarkMagic.integrations.appeng;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import foxiwhitee.FoxDarkMagic.config.ContentConfig;
import foxiwhitee.FoxDarkMagic.integrations.IIntegration;
import foxiwhitee.FoxDarkMagic.integrations.Integration;
import foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.encoders.BlockArcaneEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.encoders.BlockCrucibleEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.encoders.BlockInfusionEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.container.encoders.ContainerArcaneEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.container.encoders.ContainerCrucibleEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.container.encoders.ContainerInfusionEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.item.ItemAEPart;
import foxiwhitee.FoxDarkMagic.integrations.appeng.item.patterns.ItemEncodedArcanePattern;
import foxiwhitee.FoxDarkMagic.integrations.appeng.item.patterns.ItemEncodedEssentiaPattern;
import foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.server.*;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders.TileArcaneEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders.TileCrucibleEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders.TileInfusionEncoder;
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
    public static final Item encodedCruciblePattern = new ItemEncodedEssentiaPattern("encodedCruciblePattern");
    public static final Item encodedInfusionPattern = new ItemEncodedEssentiaPattern("encodedInfusionPattern");

    public static final Block arcaneEncoder = new BlockArcaneEncoder("arcaneEncoder");
    public static final Block crucibleEncoder = new BlockCrucibleEncoder("crucibleEncoder");
    public static final Block infusionEncoder = new BlockInfusionEncoder("infusionEncoder");

    @Override
    public void preInit(FMLPreInitializationEvent paramFMLPreInitializationEvent) {
        if (ContentConfig.enableBasicArcaneMolecularAssembler || ContentConfig.enableAdvancedArcaneMolecularAssembler || ContentConfig.enableHybridArcaneMolecularAssembler || ContentConfig.enableUltimateArcaneMolecularAssembler) {
            RegisterUtils.registerItem(encodedArcanePattern);
            RegisterUtils.registerBlock(arcaneEncoder);
            RegisterUtils.registerTile(TileArcaneEncoder.class);
        }
        if (ContentConfig.enableCrucibleMolecularAssembler) {
            RegisterUtils.registerItem(encodedCruciblePattern);
            RegisterUtils.registerBlock(crucibleEncoder);
            RegisterUtils.registerTile(TileCrucibleEncoder.class);
        }
        if (ContentConfig.enableInfusionMolecularAssembler) {
            RegisterUtils.registerItem(encodedInfusionPattern);
            RegisterUtils.registerBlock(infusionEncoder);
            RegisterUtils.registerTile(TileInfusionEncoder.class);
        }
        if (ContentConfig.enableFastBuses) {
            RegisterUtils.registerItem(parts);
        }
    }

    @Override
    public void init(FMLInitializationEvent paramFMLInitializationEvent) {
        FoxLibApi.instance.registries().registerGui()
                .register(BlockArcaneEncoder.class, TileArcaneEncoder.class, ContainerArcaneEncoder.class)
                .register(BlockCrucibleEncoder.class, TileCrucibleEncoder.class, ContainerCrucibleEncoder.class)
                .register(BlockInfusionEncoder.class, TileInfusionEncoder.class, ContainerInfusionEncoder.class);
        Objects.requireNonNull(ThEApi.instance()).transportPermissions().addAspectStorageTileToExtractPermissions(TileSingularAlchemicalFurnace.class);

        var pr = FoxLibApi.instance.registries().registerPacket();
        pr.register(C2SEncodePacket.class);
        pr.register(C2SChangePagePacket.class);
    }

    @Override
    public void postInit(FMLPostInitializationEvent paramFMLPostInitializationEvent) {

    }
}
