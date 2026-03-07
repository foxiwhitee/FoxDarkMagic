package foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders;

import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import appeng.tile.inventory.InvOperation;
import foxiwhitee.FoxDarkMagic.helpers.RecipesHelper;
import foxiwhitee.FoxDarkMagic.integrations.appeng.AE2Integration;
import foxiwhitee.FoxLib.utils.helpers.ItemStackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumicenergistics.common.storage.AEEssentiaStack;

import java.util.ArrayList;
import java.util.List;

public class TileCrucibleEncoder extends TileUniversalPatternEncoder {
    private final AspectList aspectList = new AspectList();
    private int page;
    private final List<CrucibleRecipe> crucibleRecipes = new ArrayList<>();

    public TileCrucibleEncoder() {
        super(1);
    }

    @Override
    protected Item getEncodedPattern() {
        return AE2Integration.encodedCruciblePattern;
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
        var recipes = RecipesHelper.getCrucibleRecipesByInput(getCraftingInventory().getStackInSlot(0));
        crucibleRecipes.clear();
        crucibleRecipes.addAll(recipes);
        if (!crucibleRecipes.isEmpty()) {
            aspectList.add(crucibleRecipes.get(page).aspects);
        }
        getOutputInventory().setInventorySlotContents(0, crucibleRecipes.isEmpty() ? null : crucibleRecipes.get(page).getRecipeOutput());
        markForUpdate();
    }

    @Override
    public void onChangeInventory(IInventory inv, int slot, InvOperation mc, ItemStack removed, ItemStack added) {
        if (inv == getCraftingInventory()) {
            if (!ItemStackUtil.stackEquals(removed, added)) {
                this.page = 0;
            }
            updateRecipe();
        }
    }

    @Override
    public void overlayRecipe(NBTTagCompound nbtTagCompound, EntityPlayer entityPlayer) {
        List<List<ItemStack>> all = readIngredients(nbtTagCompound);
        ItemStack result = ItemStack.loadItemStackFromNBT(nbtTagCompound.getCompoundTag("result"));
        getCraftingInventory().setInventorySlotContents(0, all.get(0).get(0));
        if (result != null) {
            var recipes = RecipesHelper.getCrucibleRecipesByInput(getCraftingInventory().getStackInSlot(0));
            if (!recipes.isEmpty()) {
                for (int i = 0; i < recipes.size(); i++) {
                    CrucibleRecipe recipe = recipes.get(i);
                    if (ItemStackUtil.stackEquals(recipe.getRecipeOutput(), result)) {
                        this.page = i;
                        break;
                    }
                }
            }
        }
        updateRecipe();
    }

    @Override
    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeToNbt_(NBTTagCompound data) {
        super.writeToNbt_(data);
        aspectList.writeToNBT(data, "vis");
        data.setInteger("page", page);
    }

    @Override
    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readFromNbt_(NBTTagCompound data) {
        super.readFromNbt_(data);
        aspectList.readFromNBT(data, "vis");
        page = data.getInteger("page");
        updateRecipe();
    }

    public void nextPage(int step) {
        if (!crucibleRecipes.isEmpty()) {
            page += step;
            if (crucibleRecipes.size() <= page) {
                page -= crucibleRecipes.size();
            } else if (page < 0) {
                page += crucibleRecipes.size();
            }
            aspectList.aspects.clear();
            aspectList.add(crucibleRecipes.get(page).aspects);
            getOutputInventory().setInventorySlotContents(0, crucibleRecipes.get(page).getRecipeOutput());
            markForUpdate();
        } else {
            page = 0;
        }
    }
}
