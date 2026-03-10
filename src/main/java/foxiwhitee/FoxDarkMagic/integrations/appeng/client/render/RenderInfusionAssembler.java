package foxiwhitee.FoxDarkMagic.integrations.appeng.client.render;

import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.TileInfusionAssembler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.renderers.models.ModelCube;

public class RenderInfusionAssembler extends RenderAssembler<TileInfusionAssembler> {
    private final ModelCube modelCube = new ModelCube(0);
    private final ModelCube modelCubeOver = new ModelCube(32);
    private static final ResourceLocation texInfuser = new ResourceLocation("thaumcraft", "textures/models/infuser.png");

    public RenderInfusionAssembler() {
        super(TileInfusionAssembler.class, "infusionAssembler", "infusionAssembler");
    }

    @Override
    protected void renderRunicMatrix(float partialTicks) {
        GL11.glPushMatrix();
        GL11.glTranslated(0, (double) 0 + 0.5, 0);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        float ticks = (float)Minecraft.getMinecraft().renderViewEntity.ticksExisted + partialTicks;

        float scale = 0.225F;
        float offset = 0.125F;

        Minecraft.getMinecraft().renderEngine.bindTexture(texInfuser);

        GL11.glRotatef(ticks % 360.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(35.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(45.0F, 0.0F, 0.0F, 1.0F);

        for (int a = 0; a < 2; ++a) {
            for (int b = 0; b < 2; ++b) {
                for (int c = 0; c < 2; ++c) {
                    int aa = (a == 0) ? -1 : 1;
                    int bb = (b == 0) ? -1 : 1;
                    int cc = (c == 0) ? -1 : 1;

                    GL11.glPushMatrix();

                    GL11.glTranslatef(aa * offset, bb * offset, cc * offset);

                    if (a > 0) GL11.glRotatef(90.0F, (float)a, 0.0F, 0.0F);
                    if (b > 0) GL11.glRotatef(90.0F, 0.0F, (float)b, 0.0F);
                    if (c > 0) GL11.glRotatef(90.0F, 0.0F, 0.0F, (float)c);

                    GL11.glScaled(scale, scale, scale);
                    this.modelCube.render();

                    renderGlowLayer(a, b, c, ticks);

                    GL11.glPopMatrix();
                }
            }
        }

        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }

    private void renderGlowLayer(int a, int b, int c, float ticks) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);

        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);

        GL11.glScaled(1.001, 1.001, 1.001);

        float alpha = (MathHelper.sin((ticks + (float)(a * 2 + b * 3 + c * 4)) / 4.0F) * 0.1F + 0.2F);
        GL11.glColor4f(0.8F, 0.1F, 1.0F, alpha);

        this.modelCubeOver.render();

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

}
