package foxiwhitee.FoxDarkMagic.integrations.appeng.client.render;

import foxiwhitee.FoxDarkMagic.DarkCore;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.TileCrucibleAssembler;
import foxiwhitee.FoxLib.client.render.TileEntitySpecialRendererObjWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public class RenderCrucibleAssembler extends TileEntitySpecialRendererObjWrapper<TileCrucibleAssembler> implements IItemRenderer {
    private final IModelCustom model;

    public RenderCrucibleAssembler() {
        super(TileCrucibleAssembler.class, new ResourceLocation(DarkCore.MODID, "models/crucibleAssembler.obj"), new ResourceLocation(DarkCore.MODID, "textures/blocks/assemblers/crucibleAssembler.png"));
        this.model = AdvancedModelLoader.loadModel(new ResourceLocation(DarkCore.MODID, "models/crucibleAssembler.obj"));
        createList("all");
    }

    public void renderAt(TileCrucibleAssembler tileEntity, double x, double y, double z, double f) {
        GL11.glPushMatrix();
        GL11.glTranslated(x + 0.5, y, z + 0.5);

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

        Minecraft.getMinecraft().renderEngine.bindTexture(this.getTexture());
        this.model.renderAll();
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }
}
