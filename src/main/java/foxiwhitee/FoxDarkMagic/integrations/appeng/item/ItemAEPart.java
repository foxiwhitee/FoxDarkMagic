package foxiwhitee.FoxDarkMagic.integrations.appeng.item;

import appeng.api.AEApi;
import appeng.api.config.Upgrades;
import appeng.api.implementations.items.IItemGroup;
import appeng.api.parts.IPart;
import appeng.api.parts.IPartItem;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import foxiwhitee.FoxDarkMagic.DarkCore;
import foxiwhitee.FoxDarkMagic.integrations.appeng.parts.ModParts;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumicenergistics.common.utils.ThELog;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemAEPart extends Item implements IPartItem, IItemGroup {
    public ItemAEPart() {
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setCreativeTab(DarkCore.TAB);
        AEApi.instance().partHelper().setItemBusRenderer(this);
        Map<Upgrades, Integer> possibleUpgradesList;
        for (ModParts part : ModParts.VALUES) {
            possibleUpgradesList = part.getUpgrades();

            for (Upgrades upgrade : possibleUpgradesList.keySet()) {
                upgrade.registerItem(new ItemStack(this, 1, part.ordinal()), possibleUpgradesList.get(upgrade));
            }
        }
    }

    @Override
    public IPart createPartFromItemStack(final ItemStack itemStack) {
        IPart newPart = null;
        ModParts part = ModParts.getPartFromDamageValue(itemStack);
        try {
            newPart = part.createPartInstance(itemStack);
        } catch (Throwable e) {
            ThELog.error(e, "Unable to create cable-part from item: %s", itemStack.getDisplayName());
        }
        return newPart;
    }

    @Override
    public EnumRarity getRarity(final ItemStack itemStack) {
        return EnumRarity.rare;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getSpriteNumber() {
        return 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void getSubItems(final Item item, final CreativeTabs tab,
            @SuppressWarnings("rawtypes") final List itemList) {
        int count = ModParts.VALUES.length;

        for (int i = 0; i < count; i++) {
            itemList.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    public String getUnlocalizedGroupName(final Set<ItemStack> arg0, final ItemStack itemStack) {
        return ModParts.getPartFromDamageValue(itemStack).getGroupName();
    }

    @Override
    public String getUnlocalizedName() {
        return DarkCore.MODID + ".item.aeparts";
    }

    @Override
    public String getUnlocalizedName(final ItemStack itemStack) {
        return ModParts.getPartFromDamageValue(itemStack).getUnlocalizedName();
    }

    @Override
    public boolean onItemUse(final ItemStack itemStack, final EntityPlayer player, final World world, final int x,
            final int y, final int z, final int side, final float hitX, final float hitY, final float hitZ) {
        return AEApi.instance().partHelper().placeBus(itemStack, x, y, z, side, player, world);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(final IIconRegister par1IconRegister) {}
}
