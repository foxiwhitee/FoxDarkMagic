package foxiwhitee.FoxDarkMagic.network.packets;

import cpw.mods.fml.common.network.ByteBufUtils;
import foxiwhitee.FoxDarkMagic.tile.TileSingularAlchemicalFurnace;
import foxiwhitee.FoxLib.network.BasePacket;
import foxiwhitee.FoxLib.network.IInfoPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;

public class C2SSelectAspectPacket extends BasePacket {
    private final int xCoord, yCoord, zCoord;
    private final String tag;

    public C2SSelectAspectPacket(ByteBuf data) {
        super(data);
        xCoord = data.readInt();
        yCoord = data.readInt();
        zCoord = data.readInt();
        tag = ByteBufUtils.readUTF8String(data);
    }

    public C2SSelectAspectPacket(int xCoord, int yCoord, int zCoord, String tag) {
        super();
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.zCoord = zCoord;
        this.tag = tag;
        ByteBuf data = Unpooled.buffer();
        data.writeInt(getId());
        data.writeInt(xCoord);
        data.writeInt(yCoord);
        data.writeInt(zCoord);
        ByteBufUtils.writeUTF8String(data, tag);
        setPacketData(data);
    }

    @Override
    public void handleServerSide(IInfoPacket network, BasePacket packet, EntityPlayer player) {
        if (player != null && player.worldObj != null) {
            TileEntity te = player.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord);
            if (te instanceof TileSingularAlchemicalFurnace tile) {
                Aspect aspect = tag.isEmpty() ? null : Aspect.getAspect(tag);
                tile.setSelected(aspect);
            }
        }
    }
}
