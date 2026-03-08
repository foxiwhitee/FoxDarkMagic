package foxiwhitee.FoxDarkMagic.integrations.appeng.client.gui.assemblers;

import foxiwhitee.FoxDarkMagic.client.gui.DMGui;
import foxiwhitee.FoxDarkMagic.integrations.appeng.container.assemblers.ContainerEssetiaAssembler;

@SuppressWarnings("unused")
public class GuiCrucibleAssembler extends DMGui {
    public GuiCrucibleAssembler(ContainerEssetiaAssembler container) {
        super(container, 230, 246);
    }

    @Override
    protected String getBackground() {
        return "gui/guiCrucibleAssembler.png";
    }
}
