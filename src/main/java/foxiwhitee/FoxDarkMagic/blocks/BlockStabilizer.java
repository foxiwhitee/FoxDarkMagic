package foxiwhitee.FoxDarkMagic.blocks;

import cpw.mods.fml.common.Optional;
import dev.rndmorris.salisarcana.api.IVariableInfusionStabilizer;
import foxiwhitee.FoxDarkMagic.tile.TileStabilizer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import thaumcraft.api.crafting.IInfusionStabiliser;

@Optional.Interface(modid = "salisarcana", iface = "dev.rndmorris.salisarcana.api.IVariableInfusionStabilizer")
public class BlockStabilizer extends ThaumBlock implements IInfusionStabiliser, IVariableInfusionStabilizer {
    public BlockStabilizer(String name) {
        super(name, TileStabilizer.class);
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
