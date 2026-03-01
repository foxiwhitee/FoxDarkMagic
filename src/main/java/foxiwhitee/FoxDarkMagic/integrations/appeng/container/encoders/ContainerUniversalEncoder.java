package foxiwhitee.FoxDarkMagic.integrations.appeng.container.encoders;

import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders.TileUniversalPatternEncoder;
import foxiwhitee.FoxLib.container.FoxBaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public abstract class ContainerUniversalEncoder extends FoxBaseContainer {

    public ContainerUniversalEncoder(EntityPlayer player, TileUniversalPatternEncoder tileEntity) {
        super(player, tileEntity);
    }

    @Override
    public void detectAndSendChanges() {
        crafters.stream().filter(c -> c instanceof EntityPlayerMP).forEach(c -> ((EntityPlayerMP) c).isChangingQuantityOnly = false);
        super.detectAndSendChanges();
    }
}
