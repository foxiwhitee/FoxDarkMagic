package foxiwhitee.FoxDarkMagic.client.gui;

import foxiwhitee.FoxDarkMagic.DarkCore;
import foxiwhitee.FoxLib.client.gui.FoxBaseGui;
import net.minecraft.inventory.Container;

public abstract class DMGui extends FoxBaseGui {
    public DMGui(Container container, int xSize, int ySize) {
        super(container, xSize, ySize);
        setModID(DarkCore.MODID);
    }
}
