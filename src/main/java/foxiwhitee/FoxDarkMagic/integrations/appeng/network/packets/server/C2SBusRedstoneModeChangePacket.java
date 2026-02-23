package foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.server;

import foxiwhitee.FoxDarkMagic.integrations.appeng.parts.PartFastEssentiaBus;
import foxiwhitee.FoxDarkMagic.integrations.appeng.utils.PacketUtils;
import foxiwhitee.FoxLib.network.BasePacket;
import foxiwhitee.FoxLib.network.IInfoPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;

public class C2SBusRedstoneModeChangePacket extends BasePacket {
    private final PartFastEssentiaBus part;

    public C2SBusRedstoneModeChangePacket(ByteBuf data) {
        super(data);
        this.part = (PartFastEssentiaBus) PacketUtils.readPart(data);
    }

    public C2SBusRedstoneModeChangePacket(PartFastEssentiaBus part) {
        super();
        this.part = part;
        ByteBuf data = Unpooled.buffer();
        data.writeInt(getId());
        PacketUtils.writePart(part, data);
        setPacketData(data);
    }

    @Override
    public void handleServerSide(IInfoPacket network, BasePacket packet, EntityPlayer player) {
        this.part.onClientRequestChangeRedstoneMode();
    }
}
