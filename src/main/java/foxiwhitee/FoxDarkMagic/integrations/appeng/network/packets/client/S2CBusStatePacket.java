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

public class S2CBusStatePacket extends BasePacket {
    private final RedstoneMode redstoneMode;
    private final byte filterSize;
    private final boolean redstoneControlled;
    private final boolean hasCraftingCard;
    private final boolean isCraftingOnly;

    public S2CBusStatePacket(ByteBuf data) {
        super(data);
        this.redstoneMode = RedstoneMode.values()[data.readByte()];
        this.filterSize = data.readByte();
        this.redstoneControlled = data.readBoolean();
        this.hasCraftingCard = data.readBoolean();
        this.isCraftingOnly = data.readBoolean();
    }

    public S2CBusStatePacket(RedstoneMode redstoneMode, byte filterSize, boolean redstoneControlled, boolean hasCraftingCard, boolean isCraftingOnly) {
        super();
        this.redstoneMode = redstoneMode;
        this.filterSize = filterSize;
        this.redstoneControlled = redstoneControlled;
        this.hasCraftingCard = hasCraftingCard;
        this.isCraftingOnly = isCraftingOnly;

        ByteBuf data = Unpooled.buffer();
        data.writeInt(getId());
        data.writeByte(redstoneMode.ordinal());
        data.writeByte(filterSize);
        data.writeBoolean(redstoneControlled);
        data.writeBoolean(hasCraftingCard);
        data.writeBoolean(isCraftingOnly);
        setPacketData(data);
    }

    @Override
    public void handleClientSide(IInfoPacket network, BasePacket packet, EntityPlayer player) {
        Gui gui = Minecraft.getMinecraft().currentScreen;

        if (gui instanceof GuiFastEssentiaBus bus) {
            bus.onReceiveRedstoneMode(this.redstoneMode);
            bus.onReceiveRedstoneControlled(this.redstoneControlled);
            bus.onReceiveFilterSize(this.filterSize);
            bus.onReceiveHasCrafting(this.hasCraftingCard);
            bus.onReceiveCraftingOnly(this.isCraftingOnly);
        }
    }
}
