package foxiwhitee.FoxDarkMagic.integrations.appeng.client.gui.encoders;


import foxiwhitee.FoxDarkMagic.integrations.appeng.container.encoders.ContainerArcaneEncoder;

@SuppressWarnings("unused")
public class GuiArcaneEncoder extends GuiUniversalEncoder {
    public GuiArcaneEncoder(ContainerArcaneEncoder container) {
        super(container, 232, 224, 163, 78);
    }

    @Override
    protected String getBackground() {
        return "gui/guiArcaneEncoder.png";
    }
}
