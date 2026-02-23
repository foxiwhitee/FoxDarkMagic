package foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.client;

import foxiwhitee.FoxDarkMagic.integrations.appeng.client.gui.GuiFastEssentiaBus;
import foxiwhitee.FoxLib.network.BasePacket;
import foxiwhitee.FoxLib.network.IInfoPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;

public class S2CBusFilterSizePacket extends BasePacket {
    private final byte filterSize;

    public S2CBusFilterSizePacket(ByteBuf data) {
        super(data);
        this.filterSize = data.readByte();
    }

    public S2CBusFilterSizePacket(byte filterSize) {
        super();
        this.filterSize = filterSize;

        ByteBuf data = Unpooled.buffer();
        data.writeInt(getId());
        data.writeByte(filterSize);
        setPacketData(data);
    }

    @Override
    public void handleClientSide(IInfoPacket network, BasePacket packet, EntityPlayer player) {
        Gui gui = Minecraft.getMinecraft().currentScreen;

        if (gui instanceof GuiFastEssentiaBus bus) {
            bus.onReceiveFilterSize(this.filterSize);
        }
    }
}
