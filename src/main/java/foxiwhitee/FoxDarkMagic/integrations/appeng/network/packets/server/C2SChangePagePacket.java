package foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.server;

import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders.TileCrucibleEncoder;
import foxiwhitee.FoxLib.network.BasePacket;
import foxiwhitee.FoxLib.network.IInfoPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

@SuppressWarnings("unused")
public class C2SChangePagePacket extends BasePacket {
    private final int xCoord, yCoord, zCoord;
    private final Step step;

    public C2SChangePagePacket(ByteBuf data) {
        super(data);
        xCoord = data.readInt();
        yCoord = data.readInt();
        zCoord = data.readInt();
        step = Step.values()[data.readInt()];
    }

    public C2SChangePagePacket(int xCoord, int yCoord, int zCoord, Step step) {
        super();
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        this.step = step;
        ByteBuf data = Unpooled.buffer();
        data.writeInt(getId());
        data.writeInt(xCoord);
        data.writeInt(yCoord);
        data.writeInt(zCoord);
        data.writeInt(step.ordinal());
        setPacketData(data);
    }

    @Override
    public void handleServerSide(IInfoPacket network, BasePacket packet, EntityPlayer player) {
        if (player != null && player.worldObj != null) {
            TileEntity te = player.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord);
            if (te instanceof TileCrucibleEncoder tile) {
                tile.nextPage(step == Step.NEXT ? 1 : -1);
            }
        }
    }

    public enum Step {PREVIOUS, NEXT}
}
