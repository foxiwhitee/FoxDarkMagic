package foxiwhitee.FoxDarkMagic.item;

import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketAspectPool;
import thaumcraft.common.lib.research.ResearchManager;

import java.util.List;

public class ItemSaturatedKnowledgeFragment extends DefaultItem {
    public ItemSaturatedKnowledgeFragment(String name) {
        super(name);
    }

    @Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List<String> list, boolean p_77624_4_) {
        if (DarkConfig.enableTooltips) {
            list.add(LocalizationUtils.localize("tooltip.saturatedKnowledgeFragment.description.1"));
            list.add(LocalizationUtils.localize("tooltip.saturatedKnowledgeFragment.description.2", DarkConfig.saturatedKnowledgeFragmentAspects));
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

        for (Aspect aspect : Aspect.aspects.values()) {
            Thaumcraft.proxy.playerKnowledge.addAspectPool(player.getCommandSenderName(), aspect, DarkConfig.saturatedKnowledgeFragmentAspects);
            PacketHandler.INSTANCE.sendTo(new PacketAspectPool(aspect.getTag(), DarkConfig.saturatedKnowledgeFragmentAspects, Thaumcraft.proxy.playerKnowledge.getAspectPoolFor(player.getGameProfile().getName(), aspect)), (EntityPlayerMP)player);
        }
        ResearchManager.scheduleSave(player);
        return stack;
    }
}
