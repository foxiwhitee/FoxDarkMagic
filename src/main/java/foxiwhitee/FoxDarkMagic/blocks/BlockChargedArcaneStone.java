package foxiwhitee.FoxDarkMagic.blocks;

import foxiwhitee.FoxDarkMagic.DarkCore;
import foxiwhitee.FoxDarkMagic.ModItems;
import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxLib.block.FoxBaseBlock;
import net.minecraft.block.Block;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.common.config.ConfigBlocks;

import java.util.Random;

public class BlockChargedArcaneStone extends FoxBaseBlock {
    public BlockChargedArcaneStone(String name) {
        super(DarkCore.MODID, name);
        setCreativeTab(DarkCore.TAB);
    }

    @Override
    public Item getItemDropped(int metadata, Random random, int fortune) {
        return Item.getItemFromBlock(ConfigBlocks.blockCosmeticSolid);
    }

    @Override
    protected boolean canSilkHarvest() {
        return false;
    }

    @Override
    public int damageDropped(int metadata) {
        return 7;
    }

    @Override
    public int tickRate(World world) {
        return 100;
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (world.getBlock(x, y + 1, z) == Blocks.fire) {
            world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
        }
    }

    @Override
    public void updateTick(World world, int x, int y, int z, Random random) {
        if (!world.isRemote) {
            if (!world.isDaytime()) {
                if (world.getBlock(x, y + 1, z) == Blocks.fire) {
                    world.addWeatherEffect(new EntityLightningBolt(world, x, y, z));
                    world.setBlockToAir(x, y + 1, z);
                    this.dropBlockAsItem(world, x, y, z, new ItemStack(ModItems.moonstone, random.nextInt(4) + 1));
                    if (DarkConfig.chargedArcaneStoneRemoveAfterRitual) {
                        world.setBlockToAir(x, y, z);
                    }
                }
            }
        }
    }
}
