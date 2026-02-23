package foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.client;

import cpw.mods.fml.common.network.ByteBufUtils;
import foxiwhitee.FoxLib.network.BasePacket;
import foxiwhitee.FoxLib.network.IInfoPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.api.gui.IAspectSlotGui;

import java.util.ArrayList;
import java.util.List;

public class S2CAspectSlotPacket extends BasePacket {
    private final List<Aspect> filteredAspects;

    public S2CAspectSlotPacket(ByteBuf data) {
        super(data);
        int size = data.readInt();
        filteredAspects = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            filteredAspects.add(Aspect.getAspect(ByteBufUtils.readUTF8String(data)));
        }
    }

    public S2CAspectSlotPacket(final List<Aspect> filterAspects) {
        super();
        this.filteredAspects = filterAspects;
        ByteBuf data = Unpooled.buffer();
        data.writeInt(getId());
        data.writeInt(filterAspects.size());
        for (Aspect aspect : filterAspects) {
            ByteBufUtils.writeUTF8String(data, aspect.getTag());
        }
        setPacketData(data);
    }

    @Override
    public void handleClientSide(IInfoPacket network, BasePacket packet, EntityPlayer player) {
        Gui gui = Minecraft.getMinecraft().currentScreen;

        if (!(gui instanceof IAspectSlotGui)) {
            return;
        }

        ((IAspectSlotGui) gui).updateAspects(this.filteredAspects);
    }
}
