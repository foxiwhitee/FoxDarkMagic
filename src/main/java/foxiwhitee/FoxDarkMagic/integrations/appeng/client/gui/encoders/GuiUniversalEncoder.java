package foxiwhitee.FoxDarkMagic.integrations.appeng.client.gui.encoders;

import foxiwhitee.FoxDarkMagic.client.gui.DMGui;
import foxiwhitee.FoxDarkMagic.integrations.appeng.container.encoders.ContainerUniversalEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.server.C2SEncodePacket;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders.TileUniversalPatternEncoder;
import foxiwhitee.FoxLib.client.gui.buttons.NoTextureButton;
import foxiwhitee.FoxLib.network.NetworkManager;
import net.minecraft.client.gui.GuiButton;

public abstract class GuiUniversalEncoder extends DMGui {
    private final TileUniversalPatternEncoder tile;
    private final int bx, by;

    public GuiUniversalEncoder(ContainerUniversalEncoder container, int xSize, int ySize, int bx, int by) {
        super(container, xSize, ySize);
        this.tile = (TileUniversalPatternEncoder) container.getTileEntity();
        this.bx = bx;
        this.by = by;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new NoTextureButton(0, this.guiLeft + bx, this.guiTop + by, 16, 16, "tooltip.encode"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (button instanceof NoTextureButton && button.id == 0) {
            NetworkManager.instance.sendToServer(new C2SEncodePacket(tile.xCoord, tile.yCoord, tile.zCoord));
        }
    }
}
