package foxiwhitee.FoxDarkMagic.item;

import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategoryList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncResearch;
import thaumcraft.common.lib.research.ResearchManager;

import java.util.Collection;
import java.util.List;

public class ItemKnowledgeBook extends DefaultItem {
    public ItemKnowledgeBook(String name) {
        super(name, 1);
    }

    @Override
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List<String> list, boolean p_77624_4_) {
        if (DarkConfig.enableTooltips) {
            list.add(LocalizationUtils.localize("tooltip.knowledgeBook.description"));
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

        Collection<ResearchCategoryList> rc = ResearchCategories.researchCategories.values();

        for (ResearchCategoryList cat : rc) {
            Collection<ResearchItem> rl = cat.research.values();

            for (ResearchItem ri : rl) {
                if (!ResearchManager.isResearchComplete(player.getCommandSenderName(), ri.key)) {
                    ResearchManager.completeResearchUnsaved(player.getGameProfile().getName(), ri.key);
                    ResearchManager.scheduleSave(player);
                }
            }
        }

        player.addChatMessage(new ChatComponentTranslation("tooltip.knowledgeBook.message.1"));
        player.addChatMessage(new ChatComponentTranslation("tooltip.knowledgeBook.message.2"));
        PacketHandler.INSTANCE.sendTo(new PacketSyncResearch(player), (EntityPlayerMP)player);
        return stack;
    }
}
