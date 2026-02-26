package foxiwhitee.FoxDarkMagic.recipes;

import foxiwhitee.FoxDarkMagic.ThaumicThinks;
import foxiwhitee.FoxLib.recipes.json.IJsonRecipe;
import foxiwhitee.FoxLib.recipes.json.annotations.*;
import foxiwhitee.FoxLib.utils.helpers.StackOreDict;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;

import java.util.List;
import java.util.stream.Collectors;

@JsonRecipe(value = "arcaneCraftingShapeless", hasOreDict = true)
public class JSONArcaneCraftingShapeless implements IJsonRecipe {

    @RecipeOutput
    @RecipeValue("output")
    private ItemStack output;

    @StringValue("research")
    private String research;

    @StringValue("vis")
    private List<String> vis;

    @OreValue("inputs")
    private List<Object> inputs;

    public JSONArcaneCraftingShapeless() {

    }

    @Override
    public void register() {
        if (output == null || research == null || research.isEmpty() || inputs == null || inputs.isEmpty() || inputs.size() > 9 || vis == null || vis.isEmpty()) {
            return;
        }
        AspectList list = ThaumRecipesUtils.getAspectsFrom(vis);
        var craft = ThaumcraftApi.addShapelessArcaneCraftingRecipe(research, output, list, inputs.stream().map(o -> o instanceof StackOreDict ore ? ore.getOre() : o).toArray());
        ThaumicThinks.addRecipeToResearch(research, craft);
    }
}
