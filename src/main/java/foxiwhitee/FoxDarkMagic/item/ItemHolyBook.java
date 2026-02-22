package foxiwhitee.FoxDarkMagic.item;

import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;

import java.util.Collection;
import java.util.List;

public class ItemHolyBook extends DefaultItem {
    public ItemHolyBook(String name) {
        super(name, 1);
    }

    @Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List<String> list, boolean p_77624_4_) {
        if (DarkConfig.enableTooltips) {
            list.add(LocalizationUtils.localize("tooltip.holyBook.description"));
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack p_77613_1_) {
        return EnumRarity.epic;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote) {
            return stack;
        }
        if (!player.capabilities.isCreativeMode) {
            stack.stackSize--;
        }

        Thaumcraft.proxy.playerKnowledge.setWarpPerm(player.getCommandSenderName(), 0);
        Thaumcraft.proxy.playerKnowledge.setWarpTemp(player.getCommandSenderName(), 0);
        Thaumcraft.proxy.playerKnowledge.setWarpSticky(player.getCommandSenderName(), 0);
        PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player, (byte)2), (EntityPlayerMP) player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player, (byte)0), (EntityPlayerMP) player);
        PacketHandler.INSTANCE.sendTo(new PacketSyncWarp(player, (byte)1), (EntityPlayerMP) player);

        player.addChatMessage(new ChatComponentTranslation("tooltip.holyBook.message.1"));
        player.addChatMessage(new ChatComponentTranslation("tooltip.holyBook.message.2"));
        return stack;
    }
}
