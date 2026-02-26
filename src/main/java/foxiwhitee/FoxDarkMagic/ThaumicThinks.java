package foxiwhitee.FoxDarkMagic;

import foxiwhitee.FoxDarkMagic.config.ContentConfig;
import foxiwhitee.FoxDarkMagic.recipes.ThaumRecipesUtils;
import foxiwhitee.FoxDarkMagic.researches.json.annotations.ResearchLocation;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.aspects.Aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThaumicThinks {
    public static Aspect moolon;
    private static final Map<String, List<Object>> addRecipePages = new HashMap<>();

    @ResearchLocation(modId = DarkCore.MODID)
    public static final String[] researches = {"researches"};

    public static void init() {
        if (ContentConfig.enableMoolonAspect) {
            moolon = new Aspect("moolon", 13426909, new Aspect[]{Aspect.DARKNESS, Aspect.TAINT}, new ResourceLocation(DarkCore.MODID, "textures/aspects/moolon.png"), 1);
        }
    }

    public static void postInit() {
        for (Map.Entry<String, List<Object>> entry : addRecipePages.entrySet()) {
            for (Object object : entry.getValue()) {
                ThaumRecipesUtils.addRecipeToResearch(entry.getKey(), object);
            }
        }
    }

    public static void addRecipeToResearch(String key, Object recipe) {
        if (addRecipePages.containsKey(key)) {
            addRecipePages.get(key).add(recipe);
        } else {
            List<Object> list = new ArrayList<>();
            list.add(recipe);
            addRecipePages.put(key, list);
        }
    }
}
