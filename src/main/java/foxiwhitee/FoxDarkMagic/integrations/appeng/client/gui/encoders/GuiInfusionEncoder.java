package foxiwhitee.FoxDarkMagic.integrations.appeng.client.gui.encoders;

import foxiwhitee.FoxDarkMagic.integrations.appeng.container.encoders.ContainerInfusionEncoder;
import foxiwhitee.FoxLib.container.slots.SlotFake;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiInfusionEncoder extends GuiUniversalEncoder {
    private final static RenderItem itemRenderer = new RenderItem();
    private final ContainerInfusionEncoder container;

    public GuiInfusionEncoder(ContainerInfusionEncoder container) {
        super(container, 262, 254, 190, 93);
        this.container = container;
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(offsetX, offsetY, mouseX, mouseY);
        List<ItemStack> stacks = new ArrayList<>();
        for (int i = 1; i < container.getCircle().length; i++) {
            SlotFake slot = container.getCircle()[i];
            if (slot.getStack() != null) {
                stacks.add(slot.getStack());
            }
        }

        this.renderItems(container.getCentralSlot().xDisplayPosition, container.getCentralSlot().yDisplayPosition, stacks);
    }

    @Override
    protected String getBackground() {
        return "gui/guiInfusionEncoder.png";
    }

    private void renderItems(int x, int y, List<ItemStack> stacks) {
        int xc = x / 2;
        int yc = y / 2;
        int items = stacks.size();
        if (items > 0) {
            float angle = -90;
            int radius = 39;
            float anglePer = 360 / (float)(items + 1);
            RenderHelper.enableGUIStandardItemLighting();

            for (int i = 0; i < items; ++i) {
                double xPos = (double)xc + Math.cos((double)(angle + anglePer) * Math.PI / 180) * (double)radius - 8;
                double yPos = (double)yc + Math.sin((double)(angle + anglePer) * Math.PI / 180) * (double)radius - 8;
                xPos += 46;
                yPos += 55;
                GL11.glPushMatrix();
                GL11.glTranslated(xPos, yPos, itemRenderer.zLevel - 80);
                GL11.glScaled(1, 1, 1);
                itemRenderer.zLevel = 300;
                GL11.glEnable(2929);
                itemRenderer.renderItemAndEffectIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), stacks.get(i), 0, 0);
                itemRenderer.zLevel = 0;
                itemRenderer.renderItemIntoGUI(this.fontRendererObj, this.mc.getTextureManager(), stacks.get(i), 0, 0);
                angle += anglePer;
                GL11.glPopMatrix();
            }
        }
    }

}
