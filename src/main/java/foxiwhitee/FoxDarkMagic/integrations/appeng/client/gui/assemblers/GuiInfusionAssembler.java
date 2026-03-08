package foxiwhitee.FoxDarkMagic.integrations.appeng.client.gui.assemblers;

import foxiwhitee.FoxDarkMagic.client.gui.DMGui;
import foxiwhitee.FoxDarkMagic.integrations.appeng.container.assemblers.ContainerEssetiaAssembler;

@SuppressWarnings("unused")
public class GuiInfusionAssembler extends DMGui {
    public GuiInfusionAssembler(ContainerEssetiaAssembler container) {
        super(container, 230, 246);
    }

    @Override
    protected String getBackground() {
        return "gui/guiInfusionAssembler.png";
    }
}
