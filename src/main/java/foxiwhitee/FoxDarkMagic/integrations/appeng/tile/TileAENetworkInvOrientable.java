package foxiwhitee.FoxDarkMagic.integrations.appeng.tile;

import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.grid.AENetworkInvTile;
import foxiwhitee.FoxLib.api.orientable.FastOrientableManager;
import foxiwhitee.FoxLib.api.orientable.IOrientable;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

@SuppressWarnings("unused")
public abstract class TileAENetworkInvOrientable extends AENetworkInvTile implements IOrientable {
    private final int orientableId = FastOrientableManager.nextId();

    @Override
    public ForgeDirection getForward() { return FastOrientableManager.getForward(orientableId); }

    @Override
    public ForgeDirection getUp() { return FastOrientableManager.getUp(orientableId); }

    @Override
    public void setOrientation(ForgeDirection forward, ForgeDirection up) {
        FastOrientableManager.set(orientableId, forward, up);
        this.markForUpdate();
        if (worldObj != null) worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType());
    }

    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public final void writeToNbtOrientable(NBTTagCompound data) {
        if (this.canBeRotated()) {
            data.setByte("f_fwd", (byte)this.getForward().ordinal());
            data.setByte("f_up", (byte)this.getUp().ordinal());
        }
    }

    @TileEvent(TileEventType.WORLD_NBT_READ)
    public final void readFromNbtOrientable(NBTTagCompound data) {
        if (this.canBeRotated()) {
            ForgeDirection f = ForgeDirection.getOrientation(data.getByte("f_fwd"));
            ForgeDirection u = ForgeDirection.getOrientation(data.getByte("f_up"));
            this.setOrientation(f, u);
        }
    }

    @TileEvent(TileEventType.NETWORK_WRITE)
    public final void writeToStreamOrientable(ByteBuf data) {
        if (this.canBeRotated()) {
            data.writeByte((byte) getForward().ordinal());
            data.writeByte((byte) getUp().ordinal());
        }
    }

    @TileEvent(TileEventType.NETWORK_READ)
    public final boolean readFromStreamOrientable(ByteBuf data) {
        boolean output = false;
        if (this.canBeRotated()) {
            ForgeDirection oldForward = this.getForward(), oldUp = this.getUp();
            byte orientationForward = data.readByte(), orientationUp = data.readByte();
            ForgeDirection newForward = ForgeDirection.getOrientation(orientationForward & 7);
            ForgeDirection newUp = ForgeDirection.getOrientation(orientationUp & 7);
            this.setOrientation(newForward, newUp);
            output = newForward != oldForward || newUp != oldUp;
        }
        return output;
    }
}
