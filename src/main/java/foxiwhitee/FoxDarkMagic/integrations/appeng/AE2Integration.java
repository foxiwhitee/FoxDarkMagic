package foxiwhitee.FoxDarkMagic.integrations.appeng;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import foxiwhitee.FoxDarkMagic.config.ContentConfig;
import foxiwhitee.FoxDarkMagic.integrations.IIntegration;
import foxiwhitee.FoxDarkMagic.integrations.Integration;
import foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.assemblers.BlockCrucibleAssembler;
import foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.assemblers.BlockInfusionAssembler;
import foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.encoders.BlockArcaneEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.encoders.BlockCrucibleEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.encoders.BlockInfusionEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.client.render.RenderCrucibleAssembler;
import foxiwhitee.FoxDarkMagic.integrations.appeng.client.render.RenderInfusionAssembler;
import foxiwhitee.FoxDarkMagic.integrations.appeng.container.assemblers.ContainerEssetiaAssembler;
import foxiwhitee.FoxDarkMagic.integrations.appeng.container.encoders.ContainerArcaneEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.container.encoders.ContainerCrucibleEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.container.encoders.ContainerInfusionEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.item.ItemAEPart;
import foxiwhitee.FoxDarkMagic.integrations.appeng.item.ItemAbrahamSeal;
import foxiwhitee.FoxDarkMagic.integrations.appeng.item.block.ItemBlockAssembler;
import foxiwhitee.FoxDarkMagic.integrations.appeng.item.patterns.ItemEncodedArcanePattern;
import foxiwhitee.FoxDarkMagic.integrations.appeng.item.patterns.ItemEncodedCruciblePattern;
import foxiwhitee.FoxDarkMagic.integrations.appeng.item.patterns.ItemEncodedInfusionPattern;
import foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.server.*;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.TileCrucibleAssembler;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.TileInfusionAssembler;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders.TileArcaneEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders.TileCrucibleEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders.TileInfusionEncoder;
import foxiwhitee.FoxDarkMagic.tile.TileSingularAlchemicalFurnace;
import foxiwhitee.FoxLib.api.FoxLibApi;
import foxiwhitee.FoxLib.registries.RegisterUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import thaumicenergistics.api.ThEApi;

import java.util.Objects;

@Integration(modid = "thaumicenergistics")
public class AE2Integration implements IIntegration {
    public static final ItemAEPart parts = new ItemAEPart();
    public static final Item encodedArcanePattern = new ItemEncodedArcanePattern("encodedArcanePattern");
    public static final Item encodedCruciblePattern = new ItemEncodedCruciblePattern("encodedCruciblePattern");
    public static final Item encodedInfusionPattern = new ItemEncodedInfusionPattern("encodedInfusionPattern");

    public static final Item abrahamSeal = new ItemAbrahamSeal("abrahamSeal");

    public static final Block arcaneEncoder = new BlockArcaneEncoder("arcaneEncoder");
    public static final Block crucibleEncoder = new BlockCrucibleEncoder("crucibleEncoder");
    public static final Block infusionEncoder = new BlockInfusionEncoder("infusionEncoder");

    public static final Block crucibleAssembler = new BlockCrucibleAssembler("crucibleAssembler");
    public static final Block infusionAssembler = new BlockInfusionAssembler("infusionAssembler");

    @Override
    public void preInit(FMLPreInitializationEvent paramFMLPreInitializationEvent) {
        if (ContentConfig.enableAbrahamSeal) {
            RegisterUtils.registerItem(abrahamSeal);
        }
        if (ContentConfig.enableBasicArcaneMolecularAssembler || ContentConfig.enableAdvancedArcaneMolecularAssembler || ContentConfig.enableHybridArcaneMolecularAssembler || ContentConfig.enableUltimateArcaneMolecularAssembler) {
            RegisterUtils.registerItem(encodedArcanePattern);
            RegisterUtils.registerBlocks(arcaneEncoder);
            RegisterUtils.registerTile(TileArcaneEncoder.class);
        }
        if (ContentConfig.enableCrucibleMolecularAssembler) {
            RegisterUtils.registerItem(encodedCruciblePattern);
            RegisterUtils.registerBlock(crucibleEncoder);
            RegisterUtils.registerBlock(crucibleAssembler, ItemBlockAssembler.class);
            RegisterUtils.registerTile(TileCrucibleEncoder.class);
            RegisterUtils.registerTile(TileCrucibleAssembler.class);
        }
        if (ContentConfig.enableInfusionMolecularAssembler) {
            RegisterUtils.registerItem(encodedInfusionPattern);
            RegisterUtils.registerBlock(infusionEncoder);
            RegisterUtils.registerBlock(infusionAssembler, ItemBlockAssembler.class);
            RegisterUtils.registerTile(TileInfusionEncoder.class);
            RegisterUtils.registerTile(TileInfusionAssembler.class);
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
                .register(BlockInfusionEncoder.class, TileInfusionEncoder.class, ContainerInfusionEncoder.class)
                .register(BlockCrucibleAssembler.class, TileCrucibleAssembler.class, ContainerEssetiaAssembler.class, "GuiCrucibleAssembler")
                .register(BlockInfusionAssembler.class, TileInfusionAssembler.class, ContainerEssetiaAssembler.class, "GuiInfusionAssembler");
        Objects.requireNonNull(ThEApi.instance()).transportPermissions().addAspectStorageTileToExtractPermissions(TileSingularAlchemicalFurnace.class);

        var pr = FoxLibApi.instance.registries().registerPacket();
        pr.register(C2SEncodePacket.class);
        pr.register(C2SChangePagePacket.class);
        if (isClient()) {
            clientInit();
        }
    }

    @SideOnly(Side.CLIENT)
    private void clientInit() {
        if (ContentConfig.enableInfusionMolecularAssembler) {
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(infusionAssembler), new RenderInfusionAssembler());
            ClientRegistry.bindTileEntitySpecialRenderer(TileInfusionAssembler.class, new RenderInfusionAssembler());
        }
        if (ContentConfig.enableCrucibleMolecularAssembler) {
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(crucibleAssembler), new RenderCrucibleAssembler());
            ClientRegistry.bindTileEntitySpecialRenderer(TileCrucibleAssembler.class, new RenderCrucibleAssembler());
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent paramFMLPostInitializationEvent) {

    }
}
