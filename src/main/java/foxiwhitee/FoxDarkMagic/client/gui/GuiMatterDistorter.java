package foxiwhitee.FoxDarkMagic.client.gui;

import foxiwhitee.FoxDarkMagic.container.ContainerMatterDistorter;

public class GuiMatterDistorter extends DMGui{
    public GuiMatterDistorter(ContainerMatterDistorter container) {
        super(container, 266, 245);
    }

    @Override
    protected String getBackground() {
        return "gui/guiMatterDistorter.png";
    }
}
