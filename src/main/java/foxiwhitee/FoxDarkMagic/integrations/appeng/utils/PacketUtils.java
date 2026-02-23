package foxiwhitee.FoxDarkMagic.integrations.appeng.utils;

import appeng.api.parts.IPartHost;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import foxiwhitee.FoxDarkMagic.integrations.appeng.parts.BasePart;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.ForgeDirection;

// Taker From Thaumic Energistics
public class PacketUtils {
    public static void writePart(final BasePart part, final ByteBuf stream) {
        stream.writeInt(part.getSide().ordinal());

        writeTileEntity(part.getHost().getTile(), stream);
    }

    private static void writeTileEntity(final TileEntity entity, final ByteBuf stream) {
        writeWorld(entity.getWorldObj(), stream);
        stream.writeInt(entity.xCoord);
        stream.writeInt(entity.yCoord);
        stream.writeInt(entity.zCoord);
    }

    private static void writeWorld(final World world, final ByteBuf stream) {
        stream.writeInt(world.provider.dimensionId);
    }

    public static BasePart readPart(final ByteBuf stream) {
        ForgeDirection side = ForgeDirection.getOrientation(stream.readInt());

        IPartHost host = (IPartHost) readTileEntity(stream);

        return (BasePart) host.getPart(side);
    }

    private static TileEntity readTileEntity(final ByteBuf stream) {
        World world = readWorld(stream);

        return world.getTileEntity(stream.readInt(), stream.readInt(), stream.readInt());
    }

    private static World readWorld(final ByteBuf stream) {
        World world = DimensionManager.getWorld(stream.readInt());

        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            if (world == null) {
                world = getClientWorld();
            }
        }

        return world;
    }

    @SideOnly(Side.CLIENT)
    private static World getClientWorld() {
        return Minecraft.getMinecraft().theWorld;
    }
}
