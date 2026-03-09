package foxiwhitee.FoxDarkMagic.integrations.appeng.item.block;

import foxiwhitee.FoxDarkMagic.config.DarkConfig;
import foxiwhitee.FoxDarkMagic.integrations.appeng.AE2Integration;
import foxiwhitee.FoxLib.items.ModItemBlock;
import foxiwhitee.FoxLib.utils.helpers.EnergyUtility;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

import java.util.List;

public class ItemBlockArcaneAssembler extends ModItemBlock {
    private final long speed;
    private final int storage, discount;

    public ItemBlockArcaneAssembler(Block b) {
        super(b);
        if (isBlock(AE2Integration.basicArcaneAssembler)) {
            this.speed = DarkConfig.assemblerArcaneBasicSpeed;
            this.storage = DarkConfig.assemblerArcaneBasicStorage;
            this.discount = DarkConfig.assemblerArcaneBasicDiscount;
        } else if (isBlock(AE2Integration.advancedArcaneAssembler)) {
            this.speed = DarkConfig.assemblerArcaneAdvancedSpeed;
            this.storage = DarkConfig.assemblerArcaneAdvancedStorage;
            this.discount = DarkConfig.assemblerArcaneAdvancedDiscount;
        } else if (isBlock(AE2Integration.hybridArcaneAssembler)) {
            this.speed = DarkConfig.assemblerArcaneHybridSpeed;
            this.storage = DarkConfig.assemblerArcaneHybridStorage;
            this.discount = DarkConfig.assemblerArcaneHybridDiscount;
        } else if (isBlock(AE2Integration.ultimateArcaneAssembler)) {
            this.speed = DarkConfig.assemblerArcaneUltimateSpeed;
            this.storage = DarkConfig.assemblerArcaneUltimateStorage;
            this.discount = DarkConfig.assemblerArcaneUltimateDiscount;
        } else {
            this.speed = 0;
            this.storage = 0;
            this.discount = 0;
        }
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean b) {
        if (DarkConfig.enableTooltips) {
            list.add(LocalizationUtils.localize("tooltip.assembler.description", EnergyUtility.formatNumber(speed)));
            list.add(LocalizationUtils.localize("tooltip.assembler.visStorage", EnergyUtility.formatNumber(storage)));
            list.add(LocalizationUtils.localize("tooltip.assembler.discount", discount));

            NBTTagCompound data = stack.getTagCompound();
            if (data != null && data.hasKey("storedVis")) {
                if (!GuiScreen.isShiftKeyDown()) {
                    list.add(LocalizationUtils.localize("tooltip.assembler.shift"));
                } else {
                    AspectList aspects = new AspectList();
                    aspects.readFromNBT(data, "storedVis");
                    list.add(EnumChatFormatting.DARK_PURPLE + LocalizationUtils.localize("tooltip.assembler.vis"));
                    for (Aspect aspect : aspects.getAspects()) {
                        list.add("   " + EnumChatFormatting.WHITE + EnergyUtility.formatNumber(aspects.getAmount(aspect) / 100d) + EnumChatFormatting.RESET + EnumChatFormatting.LIGHT_PURPLE + " " + aspect.getName());
                    }
                }
            }
        }
    }
}
