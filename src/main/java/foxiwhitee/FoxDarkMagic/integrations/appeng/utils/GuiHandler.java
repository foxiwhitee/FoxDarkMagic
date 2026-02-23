package foxiwhitee.FoxDarkMagic.integrations.appeng.utils;

import appeng.api.parts.IPart;
import appeng.api.parts.IPartHost;
import cpw.mods.fml.common.network.IGuiHandler;
import foxiwhitee.FoxDarkMagic.DarkCore;
import foxiwhitee.FoxDarkMagic.integrations.appeng.parts.BasePart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

// Taken From Thaumic Energistics
public class GuiHandler implements IGuiHandler {
    public static void launchGui(final BasePart part, final EntityPlayer player, final World world, final int x, final int y, final int z) {
        if (part.isPartUseableByPlayer(player)) {
            player.openGui(DarkCore.instance, part.getSide().ordinal(), world, x, y, z);
        }
    }

    private static Object getPartGuiElement(final ForgeDirection tileSide, final EntityPlayer player, final World world, final int x, final int y, final int z, final boolean isServerSide) {
        BasePart part = (BasePart) getPart(tileSide, world, x, y, z);

        if (part == null) {
            return null;
        }

        if (isServerSide) {
            return part.getServerGuiElement(player);
        }

        return part.getClientGuiElement(player);
    }

    private static IPart getPart(final ForgeDirection tileSide, final World world, final int x, final int y, final int z) {
        IPartHost partHost = (IPartHost) (world.getTileEntity(x, y, z));

        if (partHost == null) {
            return null;
        }

        return (partHost.getPart(tileSide));
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        ForgeDirection side = ForgeDirection.getOrientation(ID);

        if ((world != null) && (side != ForgeDirection.UNKNOWN)) {
            return getPartGuiElement(side, player, world, x, y, z, true);
        }

        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        ForgeDirection side = ForgeDirection.getOrientation(ID);

        if ((world != null) && (side != ForgeDirection.UNKNOWN)) {
            return getPartGuiElement(side, player, world, x, y, z, false);
        }

        return null;
    }
}
