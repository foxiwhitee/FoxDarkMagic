package foxiwhitee.FoxDarkMagic.recipes;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

import java.util.List;

public class ThaumRecipesUtils {
    public static AspectList getAspectsFrom(List<String> keys) {
        AspectList list = new AspectList();
        for (String key : keys) {
            String[] split = key.split(":");
            Aspect aspect = Aspect.getAspect(split[0]);
            if (aspect != null) {
                list.add(aspect, Integer.parseInt(split[1]));
            }
        }
        return list;
    }

    public static void addRecipeToResearch(String researchKey, Object recipe) {
        ResearchItem research = ResearchCategories.getResearch(researchKey);

        if (research != null) {
            ResearchPage newPage = null;
            if (recipe instanceof IArcaneRecipe arcaneRecipe) {
                newPage = new ResearchPage(arcaneRecipe);
            } else if (recipe instanceof CrucibleRecipe crucibleRecipe) {
                newPage = new ResearchPage(crucibleRecipe);
            } else if (recipe instanceof InfusionRecipe infusionRecipe) {
                newPage = new ResearchPage(infusionRecipe);
            }

            ResearchPage[] oldPages = research.getPages();
            ResearchPage[] allPages;
            if (oldPages != null) {
                allPages = new ResearchPage[oldPages.length + 1];

                System.arraycopy(oldPages, 0, allPages, 0, oldPages.length);
                allPages[allPages.length - 1] = newPage;
            } else {
                allPages = new ResearchPage[1];
                allPages[0] = newPage;
            }
            research.setPages(allPages);
        }
    }
}
