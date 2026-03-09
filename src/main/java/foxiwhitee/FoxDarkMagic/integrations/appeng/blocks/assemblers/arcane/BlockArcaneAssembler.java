package foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.assemblers.arcane;

import appeng.api.implementations.items.IMemoryCard;
import foxiwhitee.FoxDarkMagic.integrations.appeng.blocks.assemblers.BlockAssembler;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.arcane.TileArcaneAssembler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;

public abstract class BlockArcaneAssembler extends BlockAssembler {
    public BlockArcaneAssembler(String name, Class<? extends TileEntity> tileEntityClass) {
        super(name, tileEntityClass);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        ItemStack playerHolding = player.inventory.getCurrentItem();

        TileEntity tileAssembler = world.getTileEntity(x, y, z);

        if (tileAssembler instanceof TileArcaneAssembler tile) {
            if ((playerHolding != null) && (playerHolding.getItem() instanceof IMemoryCard)) {
                tile.onMemoryCardActivate(player, (IMemoryCard) playerHolding.getItem(), playerHolding);
                return true;
            }
        }
        return super.onBlockActivated(world, x, y, z, player, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, entity, stack);
        TileEntity te = world.getTileEntity(x, y, z);
        if (te instanceof TileArcaneAssembler tile && stack.getTagCompound() != null) {
            tile.readVisLevelsFromNBT(stack.getTagCompound());
        }
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        if (willHarvest) return true;
        return super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        super.harvestBlock(world, player, x, y, z, meta);
        world.setBlockToAir(x, y, z);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<>();
        TileEntity te = world.getTileEntity(x, y, z);

        if (te instanceof TileArcaneAssembler tile) {
            NBTTagCompound nbt = new NBTTagCompound();
            tile.writeVisLevelsToNBT(nbt);
            Item item = getItemDropped(metadata, world.rand, fortune);
            ItemStack stack = new ItemStack(item, 1, damageDropped(metadata));
            stack.setTagCompound(nbt);

            ret.add(stack);
        }

        return ret;
    }
}
