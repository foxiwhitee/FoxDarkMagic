package foxiwhitee.FoxDarkMagic.integrations.appeng.parts;

import appeng.api.AEApi;
import appeng.api.config.SecurityPermissions;
import appeng.api.implementations.IPowerChannelState;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.MENetworkChannelsChanged;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.IActionHost;
import appeng.api.networking.security.ISecurityGrid;
import appeng.api.parts.*;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import foxiwhitee.FoxDarkMagic.integrations.appeng.grid.FastPartGridBlock;
import foxiwhitee.FoxDarkMagic.integrations.appeng.utils.GuiHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import thaumicenergistics.client.textures.BlockTextureManager;
import thaumicenergistics.common.utils.EffectiveSide;
import thaumicenergistics.common.utils.ThELog;
import thaumicenergistics.common.utils.ThEUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Taken From Thaumic Energistics
public abstract class BasePart implements IPart, IGridHost, IActionHost, IPowerChannelState {
    private static final String NBT_KEY_OWNER = "Owner";
    protected static final int INVENTORY_OVERLAY_COLOR = AEColor.Black.blackVariant;
    protected static final int ACTIVE_FACE_BRIGHTNESS = 0xD000D0;
    private final SecurityPermissions[] interactionPermissions;
    private IPartHost host;
    private TileEntity hostTile;
    private ForgeDirection cableSide;
    private boolean isActive;
    private boolean isPowered;
    private IGridNode node;
    private final FastPartGridBlock gridBlock;
    private int ownerID = -1;
    public final ItemStack associatedItem;

    public BasePart(final ModParts associatedPart, final SecurityPermissions... interactionPermissions) {
        this.associatedItem = associatedPart.getStack();

        if ((interactionPermissions != null) && (interactionPermissions.length > 0)) {
            this.interactionPermissions = interactionPermissions;
        } else {
            this.interactionPermissions = null;
        }

        if (EffectiveSide.isServerSide()) {
            this.gridBlock = new FastPartGridBlock(this);
        } else {
            this.gridBlock = null;
        }
    }

    private void updateStatus() {
        if (EffectiveSide.isClientSide()) {
            return;
        }

        if (this.node != null) {
            boolean currentlyActive = this.node.isActive();

            if (currentlyActive != this.isActive) {
                this.isActive = currentlyActive;

                this.host.markForUpdate();
            }
        }

        this.onNeighborChanged();
    }

    protected TileEntity getFacingTile() {
        if (this.hostTile == null) return null;

        World world = this.hostTile.getWorldObj();
        if (world == null)
            return null;

        int x = this.hostTile.xCoord;
        int y = this.hostTile.yCoord;
        int z = this.hostTile.zCoord;

        return world.getTileEntity(x + this.cableSide.offsetX, y + this.cableSide.offsetY, z + this.cableSide.offsetZ);
    }

    @Override
    public void addToWorld() {
        if (EffectiveSide.isClientSide()) {
            return;
        }

        this.node = AEApi.instance().createGridNode(this.gridBlock);
        this.node.setPlayerID(this.ownerID);

        if ((this.hostTile != null) && (this.host != null) && (this.hostTile.getWorldObj() != null)) {
            try {
                this.node.updateState();
            } catch (Exception e) {
                ThELog.error(
                    e,
                    "Part (%s) was unable to update it's node. The part may not function correctly",
                    this.associatedItem.getDisplayName());
            }
        }

        this.updateStatus();
    }

    @Override
    public abstract int cableConnectionRenderTo();

    @Override
    public boolean canBePlacedOn(final BusSupport type) {
        return type == BusSupport.CABLE;
    }

    @Override
    public boolean canConnectRedstone() {
        return false;
    }

    @Override
    public IGridNode getActionableNode() {
        return this.node;
    }

    @Override
    public abstract void getBoxes(IPartCollisionHelper helper);

    @SideOnly(Side.CLIENT)
    @Override
    public abstract IIcon getBreakingTexture();

    @Override
    public AECableType getCableConnectionType(final ForgeDirection dir) {
        return AECableType.SMART;
    }

    public Object getClientGuiElement(final EntityPlayer player) {
        return null;
    }

    @Override
    public void getDrops(final List<ItemStack> drops, final boolean wrenched) {}

    @Override
    public final IGridNode getExternalFacingNode() {
        return null;
    }

    public FastPartGridBlock getGridBlock() {
        return this.gridBlock;
    }

    @Override
    public IGridNode getGridNode() {
        return this.node;
    }

    @Override
    public IGridNode getGridNode(final ForgeDirection direction) {
        return this.node;
    }

    public final IPartHost getHost() {
        return this.host;
    }

    public final TileEntity getHostTile() {
        return this.hostTile;
    }

    public abstract double getIdlePowerUsage();

    @Override
    public ItemStack getItemStack(final PartItemStack type) {
        ItemStack itemStack = this.associatedItem.copy();

        if (type == PartItemStack.Wrench) {
            NBTTagCompound itemNBT = new NBTTagCompound();
            this.writeToNBT(itemNBT, PartItemStack.Wrench);

            if (!itemNBT.hasNoTags()) {
                itemStack.setTagCompound(itemNBT);
            }
        }

        return itemStack;
    }

    @Override
    public abstract int getLightLevel();

    public final DimensionalCoord getLocation() {
        return new DimensionalCoord(
            this.hostTile.getWorldObj(),
            this.hostTile.xCoord,
            this.hostTile.yCoord,
            this.hostTile.zCoord);
    }

    public Object getServerGuiElement(final EntityPlayer player) {
        return null;
    }

    public ForgeDirection getSide() {
        return this.cableSide;
    }

    @Override
    public boolean isActive() {
        if (EffectiveSide.isServerSide()) {
            if (this.node != null) {
                this.isActive = this.node.isActive();
            } else {
                this.isActive = false;
            }
        }

        return this.isActive;
    }

    @Override
    public boolean isLadder(final EntityLivingBase entity) {
        return false;
    }

    public final boolean isPartUseableByPlayer(final EntityPlayer player) {
        if (EffectiveSide.isClientSide()) {
            return false;
        }

        if ((this.hostTile == null) || (this.host == null)) {
            return false;
        }

        if (!ThEUtils.canPlayerInteractWith(player, this.hostTile)) {
            return false;
        }

        if (this.host.getPart(this.cableSide) != this) {
            return false;
        }

        if (this.interactionPermissions != null) {
            ISecurityGrid sGrid = this.gridBlock.getSecurityGrid();
            if (sGrid == null) {
                return false;
            }

            for (SecurityPermissions perm : this.interactionPermissions) {
                if (!sGrid.hasPermission(player, perm)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean isPowered() {
        try {
            if (EffectiveSide.isServerSide() && (this.gridBlock != null)) {
                IEnergyGrid eGrid = this.gridBlock.getEnergyGrid();
                if (eGrid != null) {
                    this.isPowered = eGrid.isNetworkPowered();
                } else {
                    this.isPowered = false;
                }
            }
        } catch (Exception ignored) {
        }

        return this.isPowered;
    }

    @Override
    public int isProvidingStrongPower() {
        return 0;
    }

    @Override
    public int isProvidingWeakPower() {
        return 0;
    }

    public boolean isReceivingRedstonePower() {
        if (this.host != null) {
            return this.host.hasRedstone(this.cableSide);
        }
        return false;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    public final void markForSave() {
        if (this.host != null) {
            this.host.markForSave();
        }
    }

    @Override
    public boolean onActivate(final EntityPlayer player, final Vec3 position) {
        if (player.isSneaking()) {
            return false;
        }

        if (EffectiveSide.isServerSide()) {
            GuiHandler.launchGui(this, player, this.hostTile.getWorldObj(), this.hostTile.xCoord, this.hostTile.yCoord, this.hostTile.zCoord);
        }

        return true;
    }

    @Override
    public void onEntityCollision(final Entity entity) {}

    @Override
    public void onNeighborChanged() {}

    @Override
    public final void onPlacement(final EntityPlayer player, final ItemStack held, final ForgeDirection side) {
        this.ownerID = AEApi.instance().registries().players().getID(player.getGameProfile());
    }

    @Override
    public boolean onShiftActivate(final EntityPlayer player, final Vec3 position) {
        return false;
    }

    @Override
    public void randomDisplayTick(final World world, final int x, final int y, final int z, final Random r) {}

    @Override
    public void readFromNBT(final NBTTagCompound data) {
        if (data.hasKey(NBT_KEY_OWNER)) {
            this.ownerID = data.getInteger(NBT_KEY_OWNER);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean readFromStream(final ByteBuf stream) {
        boolean oldActive = this.isActive;
        boolean oldPowered = this.isPowered;

        this.isActive = stream.readBoolean();
        this.isPowered = stream.readBoolean();

        return ((oldActive != this.isActive) || (oldPowered != this.isPowered));
    }

    @Override
    public void removeFromWorld() {
        if (this.node != null) {
            this.node.destroy();
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderDynamic(final double x, final double y, final double z, final IPartRenderHelper helper, final RenderBlocks renderer) {}

    @SideOnly(Side.CLIENT)
    @Override
    public abstract void renderInventory(IPartRenderHelper helper, RenderBlocks renderer);

    @SideOnly(Side.CLIENT)
    public void renderInventoryBusLights(final IPartRenderHelper helper, final RenderBlocks renderer) {
        helper.setInvColor(0xFFFFFF);
        IIcon busColorTexture = BlockTextureManager.BUS_COLOR.getTextures()[0];
        IIcon sideTexture = BlockTextureManager.BUS_COLOR.getTextures()[2];
        helper.setTexture(busColorTexture, busColorTexture, sideTexture, sideTexture, busColorTexture, busColorTexture);
        helper.renderInventoryBox(renderer);
        Tessellator.instance.setBrightness(0xD000D0);
        helper.setInvColor(AEColor.Transparent.blackVariant);
        IIcon lightTexture = BlockTextureManager.BUS_COLOR.getTextures()[1];
        helper.renderInventoryFace(lightTexture, ForgeDirection.UP, renderer);
        helper.renderInventoryFace(lightTexture, ForgeDirection.DOWN, renderer);
        helper.renderInventoryFace(lightTexture, ForgeDirection.NORTH, renderer);
        helper.renderInventoryFace(lightTexture, ForgeDirection.EAST, renderer);
        helper.renderInventoryFace(lightTexture, ForgeDirection.SOUTH, renderer);
        helper.renderInventoryFace(lightTexture, ForgeDirection.WEST, renderer);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public abstract void renderStatic(int x, int y, int z, IPartRenderHelper helper, RenderBlocks renderer);

    @SideOnly(Side.CLIENT)
    public void renderStaticBusLights(final int x, final int y, final int z, final IPartRenderHelper helper, final RenderBlocks renderer) {
        IIcon busColorTexture = BlockTextureManager.BUS_COLOR.getTextures()[0];
        IIcon sideTexture = BlockTextureManager.BUS_COLOR.getTextures()[2];
        helper.setTexture(busColorTexture, busColorTexture, sideTexture, sideTexture, busColorTexture, busColorTexture);
        helper.renderBlock(x, y, z, renderer);
        if (this.isActive()) {
            Tessellator.instance.setBrightness(0xD000D0);
            Tessellator.instance.setColorOpaque_I(this.host.getColor().blackVariant);
        } else {
            Tessellator.instance.setColorOpaque_I(0);
        }

        IIcon lightTexture = BlockTextureManager.BUS_COLOR.getTextures()[1];

        helper.renderFace(x, y, z, lightTexture, ForgeDirection.UP, renderer);
        helper.renderFace(x, y, z, lightTexture, ForgeDirection.DOWN, renderer);
        helper.renderFace(x, y, z, lightTexture, ForgeDirection.NORTH, renderer);
        helper.renderFace(x, y, z, lightTexture, ForgeDirection.EAST, renderer);
        helper.renderFace(x, y, z, lightTexture, ForgeDirection.SOUTH, renderer);
        helper.renderFace(x, y, z, lightTexture, ForgeDirection.WEST, renderer);
    }

    @Override
    public boolean requireDynamicRender() {
        return false;
    }

    @Override
    public void securityBreak() {
        List<ItemStack> drops = new ArrayList<>();
        drops.add(this.getItemStack(PartItemStack.Break));
        this.getDrops(drops, false);

        appeng.util.Platform.spawnDrops(
            this.hostTile.getWorldObj(),
            this.hostTile.xCoord,
            this.hostTile.yCoord,
            this.hostTile.zCoord,
            drops);

        this.host.removePart(this.cableSide, false);
    }

    @Override
    public final void setPartHostInfo(final ForgeDirection side, final IPartHost host, final TileEntity tile) {
        this.cableSide = side;
        this.host = host;
        this.hostTile = tile;
    }

    @MENetworkEventSubscribe
    public final void setPower(final MENetworkPowerStatusChange event) {
        this.updateStatus();
    }

    public void setupPartFromItem(final ItemStack itemPart) {
        if (itemPart.hasTagCompound()) {
            this.readFromNBT(itemPart.getTagCompound());
        }
    }

    @MENetworkEventSubscribe
    public void updateChannels(final MENetworkChannelsChanged event) {
        this.updateStatus();
    }

    @Override
    public final void writeToNBT(final NBTTagCompound data) {
        this.writeToNBT(data, PartItemStack.World);
    }

    public void writeToNBT(final NBTTagCompound data, final PartItemStack saveType) {
        if (saveType == PartItemStack.World) {
            data.setInteger(NBT_KEY_OWNER, this.ownerID);
        }
    }

    @Override
    public void writeToStream(final ByteBuf stream) {
        stream.writeBoolean(this.isActive());
        stream.writeBoolean(this.isPowered());
    }
}
