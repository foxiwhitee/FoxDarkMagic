package foxiwhitee.FoxDarkMagic.helpers;

import foxiwhitee.FoxLib.utils.helpers.EnergyUtility;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;

import java.awt.*;

public class AspectRenderHelper {
    private static final ResourceLocation UNKNOWN_TEXTURE = new ResourceLocation("thaumcraft", "textures/aspects/_unknown.png");
    private static final int selectorBorderColor = 0xFF00FFFF;
    private static final int[] COLOR_SHIFT_AMOUNT = new int[] { 0, 8, 16, 24 };
    private static Aspect selectedAspectCache;
    private static int[] backgroundPulseGradient;

    public static void renderAspect(double x, double y, Aspect aspect, float amount, double zLevel, boolean isSelected, boolean discovered) {
        if (aspect != null) {
            Minecraft mc = Minecraft.getMinecraft();
            FontRenderer font = mc.fontRenderer;
            Color color = new Color(aspect.getColor());

            if (isSelected) {
                if (aspect != selectedAspectCache) {
                    setSelectedAspectCache(aspect);
                }
                drawHollowRectWithCorners((int) (x - 1), (int) (y - 1), getBackgroundColor());
            }

            GL11.glPushMatrix();
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glAlphaFunc(516, 0.003921569F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(770, 771);

            GL11.glPushMatrix();
            if (discovered) {
                mc.renderEngine.bindTexture(aspect.getImage());

                setGLColor(color);
                drawTexturedQuad(x, y, zLevel, color);
            } else {
                mc.renderEngine.bindTexture(UNKNOWN_TEXTURE);
                setGLColor(color);

                UtilsFX.drawTexturedQuadFull((int) x, (int) y, zLevel);
            }
            GL11.glPopMatrix();

            if (amount > 0.0F) {
                String amountStr = EnergyUtility.formatNumber(amount);
                drawAmountText(font, amountStr, (int) x, (int) y);
            }

            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glAlphaFunc(516, 0.1F);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
    }

    private static void setSelectedAspectCache(Aspect aspect) {
        selectedAspectCache = aspect;
        int aspectColor = aspect.getColor();
        backgroundPulseGradient = createColorGradient(0x70000000 | aspectColor, 0x20000000 | aspectColor);
    }

    // Taken from Thaumic Energistics
    private static int[] createColorGradient(final int fromColor, final int toColor) {
        int[] fromColorBytes = new int[4];
        int[] toColorBytes = new int[4];
        float[] stepAmount = new float[4];
        float[] currentColor = new float[4];
        int[] gradient = new int[16];

        for (int i = 0; i < 4; i++) {
            fromColorBytes[i] = (fromColor >> COLOR_SHIFT_AMOUNT[i]) & 0xFF;
            toColorBytes[i] = ((toColor >> COLOR_SHIFT_AMOUNT[i]) & 0xFF);
            stepAmount[i] = (toColorBytes[i] - fromColorBytes[i]) / (float) 16;
            currentColor[i] = fromColorBytes[i];
        }

        gradient[0] = fromColor;

        for (int iteration = 1; iteration < 16; iteration++) {
            int result = 0;

            for (int i = 0; i < 4; i++) {
                currentColor[i] += stepAmount[i];
                result += ((Math.round(currentColor[i]) & 0xFF) << COLOR_SHIFT_AMOUNT[i]);
            }

            gradient[iteration] = result;
        }
        gradient[16 - 1] = toColor;

        return gradient;
    }

    // Taken from Thaumic Energistics
    private static void drawHollowRectWithCorners(final int posX, final int posY, final int color) {
        int rightXEnd = posX + 18;
        int rightXBegin = rightXEnd - 1;
        int leftXEnd = posX + 1;
        int topYEnd = posY + 1;
        int bottomYEnd = posY + 18;
        int bottomYBegin = bottomYEnd - 1;
        Gui.drawRect(posX, posY, rightXEnd, bottomYEnd, color);
        Gui.drawRect(posX, posY, leftXEnd + 1, topYEnd + 1, selectorBorderColor);
        Gui.drawRect(rightXEnd, posY, rightXBegin - 1, topYEnd + 1, selectorBorderColor);
        Gui.drawRect(rightXEnd, bottomYEnd, rightXBegin - 1, bottomYBegin - 1, selectorBorderColor);
        Gui.drawRect(posX, bottomYEnd, leftXEnd + 1, bottomYBegin - 1, selectorBorderColor);
        Gui.drawRect(posX, posY, rightXEnd, topYEnd, selectorBorderColor);
        Gui.drawRect(posX, bottomYBegin, rightXEnd, bottomYEnd, selectorBorderColor);
        Gui.drawRect(posX, posY, leftXEnd, bottomYEnd, selectorBorderColor);
        Gui.drawRect(rightXBegin, posY, rightXEnd, bottomYEnd, selectorBorderColor);
    }

    private static int getBackgroundColor() {
        int time = (int) (System.currentTimeMillis() / 45L);
        int index = Math.abs(Math.abs(time % (15 * 2)) - 15);
        return backgroundPulseGradient[index];
    }

    private static void drawAmountText(FontRenderer font, String text, int x, int y) {
        GL11.glPushMatrix();
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int width = font.getStringWidth(text);
        int posX = 32 - width + x * 2;
        int posY = 32 - font.FONT_HEIGHT + y * 2;

        font.drawString(text, posX, posY, 16777215);
        GL11.glPopMatrix();
    }

    private static void setGLColor(Color color) {
        float r = (float) color.getRed() / 255.0F;
        float g = (float)color.getGreen() / 255.0F;
        float b = (float)color.getBlue() / 255.0F;
        GL11.glColor4f(r, g, b, 1);
    }

    private static void drawTexturedQuad(double x, double y, double z, Color color) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();

        setTessellatorColor(tessellator, color);

        tessellator.addVertexWithUV(x, y + 16.0, z, 0.0, 1.0);
        tessellator.addVertexWithUV(x + 16.0, y + 16.0, z, 1.0, 1.0);
        tessellator.addVertexWithUV(x + 16.0, y, z, 1.0, 0.0);
        tessellator.addVertexWithUV(x, y, z, 0.0, 0.0);
        tessellator.draw();
    }

    private static void setTessellatorColor(Tessellator t, Color color) {
        t.setColorRGBA_F((float) color.getRed() / 255.0F, (float) color.getGreen() / 255.0F, (float) color.getBlue() / 255.0F, (float) 1.0);
    }
}
