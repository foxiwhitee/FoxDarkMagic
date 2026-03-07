package foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders;

import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import foxiwhitee.FoxDarkMagic.helpers.RecipesHelper;
import foxiwhitee.FoxDarkMagic.integrations.appeng.AE2Integration;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumicenergistics.common.storage.AEEssentiaStack;

import java.util.List;

public class TileInfusionEncoder extends TileUniversalPatternEncoder {
    private final AspectList aspectList = new AspectList();

    public TileInfusionEncoder() {
        super(17);
    }

    @Override
    protected Item getEncodedPattern() {
        return AE2Integration.encodedInfusionPattern;
    }

    @Override
    protected void afterEncoding(NBTTagCompound data) {
        NBTTagList tagList = data.getTagList("in", 10);
        for (Aspect aspect : aspectList.getAspects()) {
            AEEssentiaStack stack = new AEEssentiaStack(aspect, aspectList.getAmount(aspect));
            tagList.appendTag(stack.toNBTGeneric());
        }
        data.setTag("in", tagList);
    }

    @Override
    public void updateRecipe() {
        aspectList.aspects.clear();

        InfusionRecipe newRecipe = null;
        for (Object obj : ThaumcraftApi.getCraftingRecipes()) {
            if (obj instanceof InfusionRecipe recipe) {
                if (RecipesHelper.matchesInfusion(getCraftingInventory(), recipe)) {
                    newRecipe = recipe;
                    break;
                }
            }
        }

        if (newRecipe == null || !(newRecipe.getRecipeOutput() instanceof ItemStack)) {
            getOutputInventory().setInventorySlotContents(0, null);
        } else {
            aspectList.add(newRecipe.getAspects());
            getOutputInventory().setInventorySlotContents(0, (ItemStack) newRecipe.getRecipeOutput());
        }
        markForUpdate();
    }

    @Override
    public void overlayRecipe(NBTTagCompound nbtTagCompound, EntityPlayer entityPlayer) {
        List<List<ItemStack>> all = readIngredients(nbtTagCompound);
        for (int i = 0; i < getCraftingInventory().getSizeInventory(); i++) {
            List<ItemStack> items = i < all.size() ? all.get(i) : null ;
            if (items != null && !items.isEmpty() && !(items.get(0).getItem() instanceof ItemAspect)) {
                getCraftingInventory().setInventorySlotContents(i, items.get(0));
                continue;
            }
            getCraftingInventory().setInventorySlotContents(i, null);
        }
    }

    @Override
    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeToNbt_(NBTTagCompound data) {
        super.writeToNbt_(data);
        aspectList.writeToNBT(data, "aspects");
    }

    @Override
    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readFromNbt_(NBTTagCompound data) {
        super.readFromNbt_(data);
        aspectList.readFromNBT(data, "aspects");
        updateRecipe();
    }
}
