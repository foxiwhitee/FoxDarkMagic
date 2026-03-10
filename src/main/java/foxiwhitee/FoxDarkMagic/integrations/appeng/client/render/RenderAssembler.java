package foxiwhitee.FoxDarkMagic.integrations.appeng.client.render;

import foxiwhitee.FoxDarkMagic.DarkCore;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.TileAssembler;
import foxiwhitee.FoxLib.client.render.TileEntitySpecialRendererObjWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public abstract class RenderAssembler<T extends TileAssembler> extends TileEntitySpecialRendererObjWrapper<T> implements IItemRenderer {
    private final IModelCustom model;

    public RenderAssembler(Class<T> tileClass, String obj, String texture) {
        super(tileClass, new ResourceLocation(DarkCore.MODID, "models/" + obj + ".obj"), new ResourceLocation(DarkCore.MODID, "textures/blocks/assemblers/" + texture + ".png"));
        this.model = AdvancedModelLoader.loadModel(new ResourceLocation(DarkCore.MODID, "models/" + obj + ".obj"));
        createList("all");
    }

    @Override
    public void renderAt(T tileEntity, double x, double y, double z, double f) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);

        renderRunicMatrix((float)f);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
        bindTexture();
        renderPart("all");
        GL11.glDisable(GL11.GL_ALPHA_TEST);

        GL11.glDisable(GL11.GL_BLEND);

        GL11.glPopMatrix();
    }

    protected void renderRunicMatrix(float partialTicks) {}

    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glTranslated(0.0F, -0.5F, 0.0F);
        GL11.glScaled(1.0F, 1.0F, 1.0F);
        switch (type) {
            case ENTITY:
                GL11.glScaled(1.35, 1.35, 1.35);
                GL11.glTranslated(0, 0, 0);
                break;
            case EQUIPPED, EQUIPPED_FIRST_PERSON:
                GL11.glScaled(1, 1, 1);
                GL11.glTranslated(0.5, 0.5, 0.5);
                break;
        }

        renderRunicMatrix(0);
        Minecraft.getMinecraft().renderEngine.bindTexture(this.getTexture());
        this.model.renderAll();
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
}
