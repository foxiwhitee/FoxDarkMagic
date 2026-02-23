package foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.client;

import foxiwhitee.FoxDarkMagic.integrations.appeng.client.gui.GuiFastEssentiaBus;
import foxiwhitee.FoxLib.network.BasePacket;
import foxiwhitee.FoxLib.network.IInfoPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;

public class S2CBusCraftingModePacket extends BasePacket {
    private final boolean isCraftingOnly;

    public S2CBusCraftingModePacket(ByteBuf data) {
        super(data);
        this.isCraftingOnly = data.readBoolean();
    }

    public S2CBusCraftingModePacket(boolean isCraftingOnly) {
        super();
        this.isCraftingOnly = isCraftingOnly;

        ByteBuf data = Unpooled.buffer();
        data.writeInt(getId());
        data.writeBoolean(isCraftingOnly);
        setPacketData(data);
    }

    @Override
    public void handleClientSide(IInfoPacket network, BasePacket packet, EntityPlayer player) {
        Gui gui = Minecraft.getMinecraft().currentScreen;

        if (gui instanceof GuiFastEssentiaBus bus) {
            bus.onReceiveCraftingOnly(this.isCraftingOnly);
        }
    }
}
