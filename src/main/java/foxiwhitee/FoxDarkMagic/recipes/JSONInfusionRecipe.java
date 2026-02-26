package foxiwhitee.FoxDarkMagic.recipes;

import foxiwhitee.FoxDarkMagic.ThaumicThinks;
import foxiwhitee.FoxLib.recipes.json.IJsonRecipe;
import foxiwhitee.FoxLib.recipes.json.annotations.*;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;

import java.util.List;

@JsonRecipe("infusion")
public class JSONInfusionRecipe implements IJsonRecipe {

    @RecipeOutput
    @RecipeValue("output")
    private ItemStack output;

    @StringValue("research")
    private String research;

    @NumberValue("instability")
    private int instability;

    @StringValue("aspects")
    private List<String> aspects;

    @RecipeValue("inputs")
    private List<ItemStack> inputs;

    @RecipeValue("catalyst")
    private ItemStack catalyst;

    public JSONInfusionRecipe() {}

    @Override
    public void register() {
        if (output == null || research == null || research.isEmpty() || aspects == null || aspects.isEmpty() || inputs == null || inputs.isEmpty() || catalyst == null) {
            return;
        }
        AspectList list = ThaumRecipesUtils.getAspectsFrom(aspects);
        var recipe = ThaumcraftApi.addInfusionCraftingRecipe(research, output, instability, list, catalyst, inputs.toArray(new ItemStack[0]));
        ThaumicThinks.addRecipeToResearch(research, recipe);
    }
}
