package foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.client;

import appeng.api.config.RedstoneMode;
import foxiwhitee.FoxDarkMagic.integrations.appeng.client.gui.GuiFastEssentiaBus;
import foxiwhitee.FoxLib.network.BasePacket;
import foxiwhitee.FoxLib.network.IInfoPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;

public class S2CBusRedstoneModePacket extends BasePacket {
    private final RedstoneMode redstoneMode;

    public S2CBusRedstoneModePacket(ByteBuf data) {
        super(data);
        this.redstoneMode = RedstoneMode.values()[data.readByte()];
    }

    public S2CBusRedstoneModePacket(RedstoneMode redstoneMode) {
        super();
        this.redstoneMode = redstoneMode;

        ByteBuf data = Unpooled.buffer();
        data.writeInt(getId());
        data.writeByte(redstoneMode.ordinal());
        setPacketData(data);
    }

    @Override
    public void handleClientSide(IInfoPacket network, BasePacket packet, EntityPlayer player) {
        Gui gui = Minecraft.getMinecraft().currentScreen;

        if (gui instanceof GuiFastEssentiaBus bus) {
            bus.onReceiveRedstoneMode(this.redstoneMode);
        }
    }
}
