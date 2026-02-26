package foxiwhitee.FoxDarkMagic.recipes;

import foxiwhitee.FoxDarkMagic.ThaumicThinks;
import foxiwhitee.FoxLib.recipes.json.IJsonRecipe;
import foxiwhitee.FoxLib.recipes.json.annotations.*;
import foxiwhitee.FoxLib.utils.helpers.StackOreDict;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;

import java.util.ArrayList;
import java.util.List;

@JsonRecipe(value = "arcaneCraftingShaped", hasOreDict = true)
public class JSONArcaneCrafting implements IJsonRecipe {

    @RecipeOutput
    @RecipeValue("output")
    private ItemStack output;

    @StringValue("research")
    private String research;

    @StringValue("vis")
    private List<String> vis;

    @OreValue("inputs")
    private List<Object> inputs;

    public JSONArcaneCrafting() {

    }

    @Override
    public void register() {
        if (output == null || research == null || research.isEmpty() || inputs == null || inputs.size() != 9 || vis == null || vis.isEmpty()) {
            return;
        }
        AspectList list = ThaumRecipesUtils.getAspectsFrom(vis);

        char[][] grid = {
            {' ', ' ', ' '},
            {' ', ' ', ' '},
            {' ', ' ', ' '}
        };

        List<Object> recipeDefinition = new ArrayList<>();
        char[] alphabet = "ABCDEFGHI".toCharArray();

        for (int i = 0; i < 9; i++) {
            Object input = inputs.get(i);
            if (input instanceof StackOreDict ore) {
                input = ore.getOre();
            }
            if (input != null) {
                int row = i / 3;
                int col = i % 3;
                char symbol = alphabet[i];

                grid[row][col] = symbol;
                recipeDefinition.add(symbol);
                recipeDefinition.add(input);
            }
        }

        Object[] finalArgs = new Object[3 + recipeDefinition.size()];
        finalArgs[0] = "" + grid[0][0] + grid[0][1] + grid[0][2];
        finalArgs[1] = "" + grid[1][0] + grid[1][1] + grid[1][2];
        finalArgs[2] = "" + grid[2][0] + grid[2][1] + grid[2][2];

        for (int i = 0; i < recipeDefinition.size(); i++) {
            finalArgs[3 + i] = recipeDefinition.get(i);
        }

        var craft = ThaumcraftApi.addArcaneCraftingRecipe(research, output, list, finalArgs);
        ThaumicThinks.addRecipeToResearch(research, craft);
    }
}
