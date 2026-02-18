package foxiwhitee.FoxDarkMagic.client.gui;

import foxiwhitee.FoxDarkMagic.container.ContainerSingularAlchemicalFurnace;
import foxiwhitee.FoxDarkMagic.tile.TileSingularAlchemicalFurnace;
import foxiwhitee.FoxLib.utils.ProductivityUtil;
import foxiwhitee.FoxLib.utils.helpers.UtilGui;
import net.minecraft.inventory.Container;

public class GuiSingularAlchemicalFurnace extends DMGui {
    private final TileSingularAlchemicalFurnace tile;

    public GuiSingularAlchemicalFurnace(ContainerSingularAlchemicalFurnace container) {
        super(container, 288, 273);
        this.tile = (TileSingularAlchemicalFurnace)container.getTileEntity();
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(offsetX, offsetY, mouseX, mouseY);
        if (tile.getProgress() > 0) {
            double l = ProductivityUtil.gauge(216, tile.getProgress(), tile.getTicksNeed());
            UtilGui.drawTexture(offsetX + 36, offsetY + 135, 0, 288, (int) l, 6, (int) l, 6);
        }
    }

    @Override
    protected String getBackground() {
        return "gui/guiSingularAlchemicalFurnace.png";
    }
}
