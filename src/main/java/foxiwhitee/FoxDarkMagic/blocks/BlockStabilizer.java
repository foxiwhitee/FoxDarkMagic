package foxiwhitee.FoxDarkMagic.blocks;

import cpw.mods.fml.common.Optional;
import dev.rndmorris.salisarcana.api.IVariableInfusionStabilizer;
import foxiwhitee.FoxDarkMagic.tile.TileStabilizer;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.crafting.IInfusionStabiliser;

import java.util.List;

@Optional.Interface(modid = "salisarcana", iface = "dev.rndmorris.salisarcana.api.IVariableInfusionStabilizer")
public class BlockStabilizer extends ThaumBlock implements IInfusionStabiliser, IVariableInfusionStabilizer {
    public BlockStabilizer(String name) {
        super(name, TileStabilizer.class);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
        list.add(LocalizationUtils.localize("tooltip.stabilizer.description.1"));
        list.add(LocalizationUtils.localize("tooltip.stabilizer.description.2"));
    }

    @Override
    public boolean canStabaliseInfusion(World world, int i, int i1, int i2) {
        return true;
    }

    @Override
    @Optional.Method(modid = "salisarcana")
    public int getStabilizerStrength(World world, int x, int y, int z) {
        return 200;
    }
}
