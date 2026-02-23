package foxiwhitee.FoxDarkMagic.integrations.appeng.parts;

import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.util.AEColor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.api.grid.IMEEssentiaMonitor;
import thaumicenergistics.client.textures.BlockTextureManager;
import thaumicenergistics.common.integration.tc.EssentiaTileContainerHelper;

// Taken From Thaumic Energistics
public class PartFastEssentiaImportBus extends PartFastEssentiaBus {
    public PartFastEssentiaImportBus() {
        super(ModParts.EssentiaImportBus, SecurityPermissions.INJECT);
    }

    @Override
    public boolean aspectTransferAllowed(final Aspect aspect) {
        boolean noFilters = true;

        if (aspect != null) {
            for (Aspect filterAspect : this.filteredAspects) {
                if (filterAspect != null) {
                    if (aspect == filterAspect) {
                        return true;
                    }

                    noFilters = false;
                }
            }

            return noFilters;
        }

        return false;
    }

    @Override
    public boolean doWork(final int transferAmount) {
        if (this.facingContainer == null) {
            return false;
        }

        Aspect aspect = EssentiaTileContainerHelper.INSTANCE.getAspectInContainer(this.facingContainer);

        if ((aspect == null) || (!this.aspectTransferAllowed(aspect))) {
            return false;
        }

        long drainedAmount = EssentiaTileContainerHelper.INSTANCE
            .extractFromContainer(this.facingContainer, transferAmount, aspect, Actionable.SIMULATE);

        if (drainedAmount <= 0) {
            return false;
        }

        IMEEssentiaMonitor essMonitor = this.getGridBlock().getEssentiaMonitor();
        if (essMonitor == null) {
            return false;
        }

        long rejectedAmount = essMonitor
            .injectEssentia(aspect, drainedAmount, Actionable.SIMULATE, this.asMachineSource, true);

        if (rejectedAmount > 0) {
            int amountInjected = (int) (drainedAmount - rejectedAmount);

            if (amountInjected <= 0) {
                return false;
            }

            drainedAmount = amountInjected;
        }

        essMonitor.injectEssentia(aspect, drainedAmount, Actionable.MODULATE, this.asMachineSource, true);

        EssentiaTileContainerHelper.INSTANCE
            .extractFromContainer(this.facingContainer, transferAmount, aspect, Actionable.MODULATE);

        return true;
    }

    @Override
    public void getBoxes(final IPartCollisionHelper helper) {
        helper.addBox(4.0F, 4.0F, 14.0F, 12.0F, 12.0F, 16.0F);

        helper.addBox(5.0F, 5.0F, 12.0F, 11.0F, 11.0F, 14.0F);

        helper.addBox(6.0D, 6.0D, 11.0D, 10.0D, 10.0D, 12.0D);
    }

    @Override
    public IIcon getBreakingTexture() {
        return BlockTextureManager.ESSENTIA_IMPORT_BUS.getTextures()[2];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventory(final IPartRenderHelper helper, final RenderBlocks renderer) {
        Tessellator ts = Tessellator.instance;

        IIcon busSideTexture = BlockTextureManager.ESSENTIA_IMPORT_BUS.getTextures()[3];

        helper.setTexture(
            busSideTexture,
            busSideTexture,
            busSideTexture,
            BlockTextureManager.ESSENTIA_IMPORT_BUS.getTexture(),
            busSideTexture,
            busSideTexture);

        helper.setBounds(4.0F, 4.0F, 15.0F, 12.0F, 12.0F, 16.0F);
        helper.renderInventoryBox(renderer);

        helper.setTexture(BlockTextureManager.ESSENTIA_IMPORT_BUS.getTextures()[2]);

        helper.setBounds(4.0F, 4.0F, 14.0F, 12.0F, 12.0F, 15.0F);
        helper.renderInventoryBox(renderer);

        helper.setBounds(5.0F, 5.0F, 13.0F, 11.0F, 11.0F, 14.0F);
        helper.renderInventoryBox(renderer);

        helper.setTexture(busSideTexture);

        helper.setBounds(5.0F, 5.0F, 12.0F, 11.0F, 11.0F, 13.0F);
        helper.renderInventoryBox(renderer);

        helper.setBounds(4.0F, 4.0F, 15.0F, 12.0F, 12.0F, 16.0F);
        helper.setInvColor(BasePart.INVENTORY_OVERLAY_COLOR);
        ts.setBrightness(15728880);
        helper.renderInventoryFace(
            BlockTextureManager.ESSENTIA_IMPORT_BUS.getTextures()[1],
            ForgeDirection.SOUTH,
            renderer);

        helper.setBounds(6.0F, 6.0F, 11.0F, 10.0F, 10.0F, 12.0F);
        this.renderInventoryBusLights(helper, renderer);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderStatic(final int x, final int y, final int z, final IPartRenderHelper helper,
                             final RenderBlocks renderer) {
        Tessellator ts = Tessellator.instance;

        IIcon busSideTexture = BlockTextureManager.ESSENTIA_IMPORT_BUS.getTextures()[3];
        helper.setTexture(
            busSideTexture,
            busSideTexture,
            busSideTexture,
            BlockTextureManager.ESSENTIA_IMPORT_BUS.getTextures()[0],
            busSideTexture,
            busSideTexture);

        helper.setBounds(4.0F, 4.0F, 15.0F, 12.0F, 12.0F, 16.0F);
        helper.renderBlock(x, y, z, renderer);

        if (this.getHost().getColor() != AEColor.Transparent) {
            ts.setColorOpaque_I(this.getHost().getColor().blackVariant);
        } else {
            ts.setColorOpaque_I(BasePart.INVENTORY_OVERLAY_COLOR);
        }

        if (this.isActive()) {
            Tessellator.instance.setBrightness(BasePart.ACTIVE_FACE_BRIGHTNESS);
        }

        helper.renderFace(
            x,
            y,
            z,
            BlockTextureManager.ESSENTIA_IMPORT_BUS.getTextures()[1],
            ForgeDirection.SOUTH,
            renderer);

        helper.renderForPass(1);

        helper.setTexture(BlockTextureManager.ESSENTIA_IMPORT_BUS.getTextures()[2]);

        helper.setBounds(4.0F, 4.0F, 14.0F, 12.0F, 12.0F, 15.0F);
        helper.renderBlock(x, y, z, renderer);

        helper.setBounds(5.0F, 5.0F, 13.0F, 11.0F, 11.0F, 14.0F);
        helper.renderBlock(x, y, z, renderer);

        helper.renderForPass(0);

        helper.setTexture(busSideTexture);

        helper.setBounds(5.0F, 5.0F, 12.0F, 11.0F, 11.0F, 13.0F);
        helper.renderBlock(x, y, z, renderer);

        helper.setBounds(6.0F, 6.0F, 11.0F, 10.0F, 10.0F, 12.0F);
        this.renderStaticBusLights(x, y, z, helper, renderer);
    }
}
