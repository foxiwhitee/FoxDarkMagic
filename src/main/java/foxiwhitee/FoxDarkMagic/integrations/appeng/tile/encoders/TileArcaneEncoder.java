package foxiwhitee.FoxDarkMagic.integrations.appeng.tile.encoders;

import appeng.tile.TileEvent;
import appeng.tile.events.TileEventType;
import foxiwhitee.FoxDarkMagic.integrations.appeng.AE2Integration;
import foxiwhitee.FoxDarkMagic.helpers.RecipesHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;
import thaumcraft.common.lib.crafting.ArcaneSceptreRecipe;
import thaumcraft.common.lib.crafting.ArcaneWandRecipe;

import java.util.List;

public class TileArcaneEncoder extends TileUniversalPatternEncoder {
    private final AspectList aspectList = new AspectList();

    public TileArcaneEncoder() {
        super(9);
    }

    @Override
    protected Item getEncodedPattern() {
        return AE2Integration.encodedArcanePattern;
    }

    @Override
    protected void afterEncoding(NBTTagCompound data) {
        NBTTagCompound tag = new NBTTagCompound();
        aspectList.writeToNBT(tag);
        data.setTag("vis", tag);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateRecipe() {
        aspectList.aspects.clear();
        for (int i = 0; i < ThaumcraftApi.getCraftingRecipes().size(); i++) {
            Object obj = ThaumcraftApi.getCraftingRecipes().get(i);
            if (obj instanceof ShapelessArcaneRecipe shapeless) {
                if (RecipesHelper.matchesArcaneShapeless(getCraftingInventory(), shapeless.getInput())) {
                    getOutputInventory().setInventorySlotContents(0, shapeless.getRecipeOutput().copy());
                    aspectList.add(shapeless.getAspects());
                    return;
                }
            } else if (obj instanceof ShapedArcaneRecipe shaped) {
                if (RecipesHelper.matchesArcaneShaped(getCraftingInventory(), shaped)) {
                    getOutputInventory().setInventorySlotContents(0, shaped.getRecipeOutput().copy());
                    aspectList.add(shaped.getAspects());
                    return;
                }
            } else if (obj instanceof ArcaneSceptreRecipe) {
                var entry = RecipesHelper.matchesSceptre(getCraftingInventory());
                if (entry != null) {
                    getOutputInventory().setInventorySlotContents(0, entry.getKey().copy());
                    aspectList.add(entry.getValue());
                    return;
                }
            } else if (obj instanceof ArcaneWandRecipe) {
                var entry = RecipesHelper.matchesWand(getCraftingInventory());
                if (entry != null) {
                    getOutputInventory().setInventorySlotContents(0, entry.getKey().copy());
                    aspectList.add(entry.getValue());
                    return;
                }
            }
        }
        getOutputInventory().setInventorySlotContents(0, null);
        markForUpdate();
    }

    @Override
    public void overlayRecipe(NBTTagCompound nbtTagCompound, EntityPlayer entityPlayer) {
        List<List<ItemStack>> all = readIngredients(nbtTagCompound);
        for (int i = 0; i < all.size(); i++) {
            List<ItemStack> list = all.get(i);
            if (list != null && !list.isEmpty()) {
                getCraftingInventory().setInventorySlotContents(i, list.get(0));
            } else {
                getCraftingInventory().setInventorySlotContents(i, null);
            }
        }
    }

    @Override
    @TileEvent(TileEventType.WORLD_NBT_WRITE)
    public void writeToNbt_(NBTTagCompound data) {
        super.writeToNbt_(data);
        aspectList.writeToNBT(data, "vis");
    }

    @Override
    @TileEvent(TileEventType.WORLD_NBT_READ)
    public void readFromNbt_(NBTTagCompound data) {
        super.readFromNbt_(data);
        aspectList.readFromNBT(data, "vis");
    }
}
