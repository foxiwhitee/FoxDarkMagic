package foxiwhitee.FoxDarkMagic.recipes;

import foxiwhitee.FoxDarkMagic.ThaumicThinks;
import foxiwhitee.FoxLib.recipes.json.IJsonRecipe;
import foxiwhitee.FoxLib.recipes.json.annotations.*;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;

import java.util.List;

@JsonRecipe(value = "crucible", hasOreDict = true)
public class JSONCrucibleRecipe implements IJsonRecipe {

    @RecipeOutput
    @RecipeValue("output")
    private ItemStack output;

    @StringValue("research")
    private String research;

    @OreValue("input")
    private Object input;

    @StringValue("aspects")
    private List<String> aspects;

    public JSONCrucibleRecipe() {}

    @Override
    public void register() {
        if (output == null || research == null || research.isEmpty() || input == null || aspects == null || aspects.isEmpty()) {
            return;
        }
        AspectList list = ThaumRecipesUtils.getAspectsFrom(aspects);
        var recipe = ThaumcraftApi.addCrucibleRecipe(research, output, input, list);
        ThaumicThinks.addRecipeToResearch(research, recipe);
    }
}
