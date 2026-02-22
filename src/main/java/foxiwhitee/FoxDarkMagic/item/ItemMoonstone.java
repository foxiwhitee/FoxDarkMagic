package foxiwhitee.FoxDarkMagic.item;

import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.entity.ItemEntityMoonStone;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.List;

public class ItemMoonstone extends DefaultItem {
    public ItemMoonstone(String name) {
        super(name);
    }

    @Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List<String> list, boolean p_77624_4_) {
        if (DarkConfig.enableTooltips) {
            list.add(LocalizationUtils.localize("tooltip.moonstone"));
        }
    }

    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    @Override
    public Entity createEntity(World world, Entity location, ItemStack itemstack) {
        ItemEntityMoonStone customEntity = new ItemEntityMoonStone(world, location.posX, location.posY, location.posZ, itemstack);

        customEntity.motionX = location.motionX;
        customEntity.motionY = location.motionY;
        customEntity.motionZ = location.motionZ;

        if (location instanceof EntityItem) {
            customEntity.delayBeforeCanPickup = ((EntityItem)location).delayBeforeCanPickup;
        }
        return customEntity;
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isHeld) {
        if (!world.isRemote && entity instanceof EntityPlayer player) {
            if (player.ticksExisted % 20 == 0) {
                player.attackEntityFrom(DamageSource.magic, world.isDaytime() ? 1 : 3);
            }
        }
    }
}
