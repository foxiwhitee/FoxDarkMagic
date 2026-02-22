package foxiwhitee.FoxDarkMagic.entity;

import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class ItemEntityMoonStone extends EntityItem {
    public ItemEntityMoonStone(World world, double x, double y, double z, ItemStack stack) {
        super(world, x, y, z, stack);
        this.isImmuneToFire = true;
        this.delayBeforeCanPickup = 20;
    }

    public ItemEntityMoonStone(World par1World) {
        super(par1World);
        this.isImmuneToFire = true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.isFireDamage() || source == DamageSource.magic) {
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void onUpdate() {
        System.out.println("Update");
        super.onUpdate();

        boolean inLava = this.worldObj.getBlock(
            MathHelper.floor_double(this.posX),
            MathHelper.floor_double(this.posY),
            MathHelper.floor_double(this.posZ)
        ).getMaterial() == Material.lava;

        if (inLava) {
            this.motionY += 0.05D;
            this.isImmuneToFire = true;
        }

        if (this.isBurning()) {
            this.extinguish();
        }
    }
}
