package foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.assemblers;

import cpw.mods.fml.client.registry.RenderingRegistry;
import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.TileInfusionAssembler;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.List;

public class BlockInfusionAssembler extends BlockAssembler {
    private static final int renderId = RenderingRegistry.getNextAvailableRenderId();

    public BlockInfusionAssembler(String name) {
        super(name, TileInfusionAssembler.class);
        super.renderId = renderId;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
        list.add(LocalizationUtils.localize("tooltip.assembler.description", DarkConfig.assemblerInfusionSpeed));
    }
}
