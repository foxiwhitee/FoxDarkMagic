package foxiwhitee.FoxDarkMagic.blocks;

import foxiwhitee.FoxDarkMagic.client.gui.GuiSingularAlchemicalFurnace;
import foxiwhitee.FoxDarkMagic.container.ContainerSingularAlchemicalFurnace;
import foxiwhitee.FoxDarkMagic.tile.TileSingularAlchemicalFurnace;
import foxiwhitee.FoxLib.utils.handler.SimpleGuiHandler;
import net.minecraft.tileentity.TileEntity;

@SimpleGuiHandler(tile = TileSingularAlchemicalFurnace.class, container = ContainerSingularAlchemicalFurnace.class, gui = GuiSingularAlchemicalFurnace.class)
public class BlockSingularAlchemicalFurnace extends ThaumBlock {
    public BlockSingularAlchemicalFurnace(String name) {
        super(name, TileSingularAlchemicalFurnace.class);
    }
}
