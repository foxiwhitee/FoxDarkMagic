package foxiwhitee.FoxDarkMagic.integrations.appeng.parts;

import appeng.api.AEApi;
import appeng.api.config.Actionable;
import appeng.api.config.SecurityPermissions;
import appeng.api.networking.IGrid;
import appeng.api.networking.crafting.ICraftingGrid;
import appeng.api.networking.crafting.ICraftingLink;
import appeng.api.networking.crafting.ICraftingRequester;
import appeng.api.networking.security.BaseActionSource;
import appeng.api.networking.security.MachineSource;
import appeng.api.parts.IPartCollisionHelper;
import appeng.api.parts.IPartRenderHelper;
import appeng.api.parts.PartItemStack;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IAEStack;
import com.google.common.collect.ImmutableSet;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraftforge.common.util.ForgeDirection;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.TileJarFillableVoid;
import thaumicenergistics.api.grid.IMEEssentiaMonitor;
import thaumicenergistics.client.textures.BlockTextureManager;
import thaumicenergistics.common.integration.tc.EssentiaTileContainerHelper;
import thaumicenergistics.common.items.ItemCraftingAspect;
import thaumicenergistics.common.items.ItemEnum;
import thaumicenergistics.implementaion.ThEMultiCraftingTracker;

// Taken From Thaumic Energistics
public class PartFastEssentiaExportBus extends PartFastEssentiaBus implements ICraftingRequester {
    private final ThEMultiCraftingTracker craftingTracker = new ThEMultiCraftingTracker(this, 9);
    private static final String NBT_KEY_VOID = "IsVoidAllowed";
    private final BaseActionSource mySrc;
    private boolean isVoidAllowed = false;
    private boolean isCraftingOnly = false;

    public PartFastEssentiaExportBus() {
        super(ModParts.EssentiaExportBus, SecurityPermissions.EXTRACT);
        this.mySrc = new MachineSource(this);
    }

    @Override
    public boolean aspectTransferAllowed(final Aspect aspect) {
        return true;
    }

    @Override
    public boolean doWork(final int amountToFillContainer) {
        if (this.facingContainer == null) {
            return false;
        }

        for (int slot = 0; slot < filteredAspects.size(); ++slot) {
            Aspect filterAspect = filteredAspects.get(slot);
            if (filterAspect == null) {
                continue;
            }

            if (EssentiaTileContainerHelper.INSTANCE
                .injectEssentiaIntoContainer(this.facingContainer, 1, filterAspect, Actionable.SIMULATE) <= 0) {
                if (!((this.isVoidAllowed)
                    && (EssentiaTileContainerHelper.INSTANCE.getAspectInContainer(this.facingContainer)
                    == filterAspect))) {
                    continue;
                }
            }

            IMEEssentiaMonitor essMonitor = this.getGridBlock().getEssentiaMonitor();
            if (essMonitor == null) {
                return false;
            }
            if (isCraftingOnly()) {
                handleCrafting(amountToFillContainer, slot, filterAspect);
                continue;
            }

            long extractedAmount = essMonitor.extractEssentia(
                filterAspect,
                amountToFillContainer,
                Actionable.SIMULATE,
                this.asMachineSource,
                true);

            if (extractedAmount <= 0) {
                if (hasCraftingCard) {
                    handleCrafting(amountToFillContainer, slot, filterAspect);
                }
                continue;
            }

            long filledAmount = injectAspect(Actionable.MODULATE, filterAspect, extractedAmount);
            if (filledAmount <= 0) {
                continue;
            }

            return true;
        }

        return false;
    }

    private void handleCrafting(int amountToFillContainer, int slot, Aspect filterAspect) {
        IGrid grid = getGridNode().getGrid();
        final ICraftingGrid cg = grid.getCache(ICraftingGrid.class);
        IAEItemStack result = AEApi.instance().storage()
            .createItemStack(ItemCraftingAspect.createStackForAspect(filterAspect, amountToFillContainer));
        this.craftingTracker
            .handleCrafting(slot, amountToFillContainer, result, getHostTile().getWorldObj(), grid, cg, this.mySrc);
    }

    @Override
    public void getBoxes(final IPartCollisionHelper helper) {
        helper.addBox(4.0F, 4.0F, 12.0F, 12.0F, 12.0F, 13.5F);
        helper.addBox(5.0F, 5.0F, 13.5F, 11.0F, 11.0F, 15.0F);
        helper.addBox(6.0F, 6.0F, 15.0F, 10.0F, 10.0F, 16.0F);
    }

    @Override
    public IIcon getBreakingTexture() {
        return BlockTextureManager.ESSENTIA_EXPORT_BUS.getTextures()[2];
    }

    @Override
    public boolean isVoidAllowed() {
        return this.isVoidAllowed;
    }

    @Override
    public boolean isCraftingOnly() {
        return isCraftingOnly;
    }

    @Override
    public void setCraftingOnly(boolean c) {
        isCraftingOnly = c;
    }


    @Override
    public void readFromNBT(final NBTTagCompound data) {
        super.readFromNBT(data);

        if (data.hasKey(NBT_KEY_VOID)) {
            this.isVoidAllowed = data.getBoolean(NBT_KEY_VOID);
        }
        this.craftingTracker.readFromNBT(data);
        if (data.hasKey("isCraftingOnly")) {
            isCraftingOnly = data.getBoolean("isCraftingOnly");
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventory(final IPartRenderHelper helper, final RenderBlocks renderer) {
        Tessellator ts = Tessellator.instance;

        IIcon busSideTexture = BlockTextureManager.ESSENTIA_EXPORT_BUS.getTextures()[3];

        helper.setTexture(busSideTexture);

        helper.setBounds(4.0F, 4.0F, 12.0F, 12.0F, 12.0F, 12.5F);
        helper.renderInventoryBox(renderer);

        helper.setTexture(BlockTextureManager.ESSENTIA_EXPORT_BUS.getTextures()[2]);

        helper.setBounds(4.0F, 4.0F, 12.5F, 12.0F, 12.0F, 13.5F);
        helper.renderInventoryBox(renderer);

        helper.setBounds(5.0F, 5.0F, 13.5F, 11.0F, 11.0F, 14.5F);
        helper.renderInventoryBox(renderer);

        helper.setTexture(busSideTexture);

        helper.setBounds(5.0F, 5.0F, 14.5F, 11.0F, 11.0F, 15.0F);
        helper.renderInventoryBox(renderer);

        helper.setTexture(
            busSideTexture,
            busSideTexture,
            busSideTexture,
            BlockTextureManager.ESSENTIA_EXPORT_BUS.getTexture(),
            busSideTexture,
            busSideTexture);

        helper.setBounds(6.0F, 6.0F, 15.0F, 10.0F, 10.0F, 16.0F);
        helper.renderInventoryBox(renderer);

        helper.setInvColor(BasePart.INVENTORY_OVERLAY_COLOR);
        ts.setBrightness(0xF000F0);
        IIcon faceOverlayTexture = BlockTextureManager.ESSENTIA_EXPORT_BUS.getTextures()[1];
        helper.renderInventoryFace(faceOverlayTexture, ForgeDirection.UP, renderer);
        helper.renderInventoryFace(faceOverlayTexture, ForgeDirection.DOWN, renderer);
        helper.renderInventoryFace(faceOverlayTexture, ForgeDirection.EAST, renderer);
        helper.renderInventoryFace(faceOverlayTexture, ForgeDirection.WEST, renderer);

        helper.setBounds(6.0F, 6.0F, 11.0F, 10.0F, 10.0F, 12.0F);
        this.renderInventoryBusLights(helper, renderer);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderStatic(final int x, final int y, final int z, final IPartRenderHelper helper,
                             final RenderBlocks renderer) {
        Tessellator ts = Tessellator.instance;

        IIcon busSideTexture = BlockTextureManager.ESSENTIA_EXPORT_BUS.getTextures()[3];

        helper.setTexture(busSideTexture);

        helper.setBounds(4.0F, 4.0F, 12.0F, 12.0F, 12.0F, 12.5F);
        helper.renderBlock(x, y, z, renderer);

        helper.renderForPass(1);

        helper.setTexture(BlockTextureManager.ESSENTIA_EXPORT_BUS.getTextures()[2]);

        helper.setBounds(4.0F, 4.0F, 12.5F, 12.0F, 12.0F, 13.5F);
        helper.renderBlock(x, y, z, renderer);

        helper.setBounds(5.0F, 5.0F, 13.5F, 11.0F, 11.0F, 14.5F);
        helper.renderBlock(x, y, z, renderer);

        helper.renderForPass(0);

        helper.setTexture(busSideTexture);

        helper.setBounds(5.0F, 5.0F, 14.5F, 11.0F, 11.0F, 15.0F);
        helper.renderBlock(x, y, z, renderer);

        helper.setTexture(
            busSideTexture,
            busSideTexture,
            busSideTexture,
            BlockTextureManager.ESSENTIA_EXPORT_BUS.getTextures()[0],
            busSideTexture,
            busSideTexture);

        helper.setBounds(6.0F, 6.0F, 15.0F, 10.0F, 10.0F, 16.0F);
        helper.renderBlock(x, y, z, renderer);

        ts.setColorOpaque_I(this.getHost().getColor().blackVariant);

        if (this.isActive()) {
            Tessellator.instance.setBrightness(BasePart.ACTIVE_FACE_BRIGHTNESS);
        }

        IIcon faceOverlayTexture = BlockTextureManager.ESSENTIA_EXPORT_BUS.getTextures()[1];
        helper.renderFace(x, y, z, faceOverlayTexture, ForgeDirection.UP, renderer);
        helper.renderFace(x, y, z, faceOverlayTexture, ForgeDirection.DOWN, renderer);
        helper.renderFace(x, y, z, faceOverlayTexture, ForgeDirection.EAST, renderer);
        helper.renderFace(x, y, z, faceOverlayTexture, ForgeDirection.WEST, renderer);

        helper.setBounds(6.0F, 6.0F, 11.0F, 10.0F, 10.0F, 12.0F);
        this.renderStaticBusLights(x, y, z, helper, renderer);
    }

    public void toggleVoidMode() {
        // Swap void modes
        this.isVoidAllowed = !this.isVoidAllowed;
    }

    public void toggleCraftingMode() {
        isCraftingOnly = !isCraftingOnly;
    }

    @Override
    public void writeToNBT(final NBTTagCompound data, final PartItemStack saveType) {
        super.writeToNBT(data, saveType);

        boolean doSave = (saveType == PartItemStack.World);
        if (!doSave) {
            for (Aspect aspect : this.filteredAspects) {
                if (aspect != null) {
                    doSave = true;
                    break;
                }
            }
        }

        if (doSave) {
            data.setBoolean(NBT_KEY_VOID, isVoidAllowed);
            this.craftingTracker.writeToNBT(data);
            data.setBoolean("isCraftingOnly", isCraftingOnly);
        }
    }

    @Override
    public ImmutableSet<ICraftingLink> getRequestedJobs() {
        return this.craftingTracker.getRequestedJobs();
    }

    @Override
    public IAEStack<?> injectCraftedItems(final ICraftingLink link, final IAEStack<?> items, final Actionable mode) {
        if (items instanceof IAEItemStack stack) {
            if (getGridNode().isActive() && ItemEnum.CRAFTING_ASPECT.getItem() == stack.getItem()) {
                Aspect a = ItemCraftingAspect.getAspect(stack.getItemStack());
                if (a != null) {
                    long toFill = stack.getStackSize();
                    long filledAmount = injectAspect(mode, a, toFill);
                    IAEItemStack c = stack.copy();
                    c.setStackSize(toFill - filledAmount);
                    return c;
                }
            }
        }
        return items;
    }

    private long injectAspect(Actionable mode, Aspect a, long extractedAmount) {
        long filledAmount;
        if (this.isVoidAllowed && (this.facingContainer instanceof TileJarFillableVoid)) {
            filledAmount = extractedAmount;
        } else {
            filledAmount = EssentiaTileContainerHelper.INSTANCE
                .injectEssentiaIntoContainer(this.facingContainer, (int) extractedAmount, a, Actionable.SIMULATE);
        }

        if (filledAmount <= 0) {
            return 0;
        }

        if (mode == Actionable.MODULATE) {
            long actualFilledAmount = EssentiaTileContainerHelper.INSTANCE
                .injectEssentiaIntoContainer(this.facingContainer, (int) filledAmount, a, Actionable.MODULATE);

            if (!this.isVoidAllowed) {
                filledAmount = actualFilledAmount;
            }
            getGridBlock().getEssentiaMonitor()
                .extractEssentia(a, filledAmount, Actionable.MODULATE, this.asMachineSource, true);
        }
        return filledAmount;
    }

    @Override
    public void jobStateChange(final ICraftingLink link) {
        this.craftingTracker.jobStateChange(link);
    }
}
