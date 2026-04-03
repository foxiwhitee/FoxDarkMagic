package foxiwhitee.FoxDarkMagic.blocks;

import cpw.mods.fml.client.registry.RenderingRegistry;
import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.tile.TileSingularAlchemicalFurnace;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class BlockSingularAlchemicalFurnace extends ThaumBlock {
    private final static int renderId = RenderingRegistry.getNextAvailableRenderId();

    public BlockSingularAlchemicalFurnace(String name) {
        super(name, TileSingularAlchemicalFurnace.class);
        setLightLevel(1);
        super.renderId = renderId;
        setBlockTextureName("thaumcraft:arcane_stone");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
        list.add(LocalizationUtils.localize("tooltip.furnace.description.1"));
        list.add(LocalizationUtils.localizeF("tooltip.furnace.description.2", DarkConfig.singularArcaneFurnaceStorage));
        list.add(LocalizationUtils.localize("tooltip.furnace.description.3"));
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
}
