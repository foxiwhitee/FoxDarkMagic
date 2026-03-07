package foxiwhitee.FoxDarkMagic.integrations.appeng.client.gui.encoders;

import foxiwhitee.FoxDarkMagic.integrations.appeng.container.encoders.ContainerCrucibleEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.server.C2SChangePagePacket;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders.TileCrucibleEncoder;
import foxiwhitee.FoxLib.client.gui.buttons.NoTextureButton;
import foxiwhitee.FoxLib.network.NetworkManager;
import net.minecraft.client.gui.GuiButton;

@SuppressWarnings("unused")
public class GuiCrucibleEncoder extends GuiUniversalEncoder {
    private final TileCrucibleEncoder tile;

    public GuiCrucibleEncoder(ContainerCrucibleEncoder container) {
        super(container, 232, 224, 163, 78);
        this.tile = (TileCrucibleEncoder) container.getTileEntity();
    }

    @Override
    protected String getBackground() {
        return "gui/guiCrucibleEncoder.png";
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new NoTextureButton(1, this.guiLeft + 53, this.guiTop + 78, 16, 16, "tooltip.button.previous"));
        this.buttonList.add(new NoTextureButton(2, this.guiLeft + 89, this.guiTop + 78, 16, 16, "tooltip.button.next"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        super.actionPerformed(button);
        if (button instanceof NoTextureButton && button.id > 0) {
            C2SChangePagePacket.Step step = C2SChangePagePacket.Step.values()[button.id - 1];
            NetworkManager.instance.sendToServer(new C2SChangePagePacket(tile.xCoord, tile.yCoord, tile.zCoord, step));
        }
    }
}
