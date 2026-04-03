package foxiwhitee.FoxDarkMagic.client.gui;

import foxiwhitee.FoxDarkMagic.DarkCore;
import foxiwhitee.FoxDarkMagic.client.gui.elements.ScrollBar;
import foxiwhitee.FoxDarkMagic.container.ContainerSingularAlchemicalFurnace;
import foxiwhitee.FoxDarkMagic.helpers.AspectRenderHelper;
import foxiwhitee.FoxDarkMagic.network.packets.C2SSelectAspectPacket;
import foxiwhitee.FoxDarkMagic.tile.TileSingularAlchemicalFurnace;
import foxiwhitee.FoxLib.network.NetworkManager;
import foxiwhitee.FoxLib.utils.helpers.UtilGui;
import net.minecraft.entity.player.EntityPlayer;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.Thaumcraft;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class GuiSingularAlchemicalFurnace extends DMGui {
    private final TileSingularAlchemicalFurnace tile;
    private ScrollBar scrollBar;

    public GuiSingularAlchemicalFurnace(ContainerSingularAlchemicalFurnace container) {
        super(container, 288, 273);
        this.tile = (TileSingularAlchemicalFurnace)container.getTileEntity();
    }

    @Override
    public void initGui() {
        super.initGui();
        this.scrollBar = new ScrollBar(243, 59, 4, 5, 70, 0, 294);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.scrollBar.handleMouseLogic(this.guiLeft, this.guiTop, mouseX, mouseY);
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(offsetX, offsetY, mouseX, mouseY);

        this.scrollBar.setTotalItems(tile.getAspects().size());
        this.scrollBar.draw(0, 0);

        int currentRow = this.scrollBar.getScrollRow();
        AspectList aspectList = this.tile.getAspects();
        List<Runnable> runnables = new ArrayList<>();

        if (aspectList.size() > 0) {
            for(int row = 0; row < 4; ++row) {
                for(int col = 0; col < 5; ++col) {
                    int index = col + (row + currentRow) * 5;

                    if (aspectList.getAspects().length <= index) {
                        break;
                    }

                    Aspect aspect = aspectList.getAspects()[index];
                    int amount = aspectList.getAmount(aspect);
                    int posX = offsetX + 149 + col * 18;
                    int posY = offsetY + 59 + row * 18;

                    boolean discovered = hasPlayerDiscoveredAspect(getContainer().getPlayer(), aspect);
                    AspectRenderHelper.renderAspect(posX, posY, aspect, amount, this.zLevel, tile.getSelected() == aspect, discovered);
                    String description = "";
                    if (discovered) {
                        description += aspect.getName() + "\n";
                        description += aspect.getLocalizedDescription();
                    } else {
                        description = "Unknown";
                    }
                    String finalDescription = description;
                    runnables.add(() -> drawIfInMouse(mouseX, mouseY, posX, posY, 18, 18, finalDescription));
                }
            }
        }

        runnables.forEach(Runnable::run);

        this.bindTexture(DarkCore.MODID, this.getBackground());

        if (tile.getProgress() > 0) {
            double l = UtilGui.gauge(216, tile.getProgress(), tile.getTicksNeed());
            UtilGui.drawTexture(offsetX + 36, offsetY + 135, 0, 288, (int) l, 6, (int) l, 6);
        }
    }

    private boolean hasPlayerDiscoveredAspect(final EntityPlayer player, Aspect aspect) {
        if ((player != null) && (aspect != null)) {
            return Thaumcraft.proxy.getPlayerKnowledge().hasDiscoveredAspect(player.getCommandSenderName(), aspect);
        }

        return false;
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        this.scrollBar.handleMouseWheel();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (mouseButton == 0) {
            int totalWidth = 5 * 18;
            int totalHeight = 4 * 18;
            int startX = 148 + this.guiLeft;
            int startY = 58 + this.guiTop;

            if (mouseX >= startX && mouseX < startX + totalWidth &&
                mouseY >= startY && mouseY < startY + totalHeight) {

                int relX = mouseX - startX;
                int relY = mouseY - startY;

                int col = relX / 18;
                int row = relY / 18;

                int clickedId = ((row * 5) + col) + this.scrollBar.getScrollRow() * 5;

                AspectList aspectList = this.tile.getAspects();
                if (clickedId < aspectList.getAspects().length) {
                    Aspect aspect = aspectList.getAspects()[clickedId];
                    String tag = "";
                    if (aspect != this.tile.getSelected()) {
                        tag = aspect.getTag();
                    }
                    NetworkManager.instance.sendToServer(new C2SSelectAspectPacket(this.tile.xCoord, this.tile.yCoord, this.tile.zCoord, tag));
                }
            }
        }
    }

    @Override
    protected String getBackground() {
        return "gui/guiSingularAlchemicalFurnace.png";
    }

    @Override
    public ContainerSingularAlchemicalFurnace getContainer() {
        return (ContainerSingularAlchemicalFurnace) super.getContainer();
    }
}
