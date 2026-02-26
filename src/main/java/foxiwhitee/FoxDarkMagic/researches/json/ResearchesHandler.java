package foxiwhitee.FoxDarkMagic.researches.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import foxiwhitee.FoxDarkMagic.recipes.ThaumRecipesUtils;
import foxiwhitee.FoxDarkMagic.researches.json.annotations.ResearchLocation;
import foxiwhitee.FoxLib.recipes.json.RecipeUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.*;

import static foxiwhitee.FoxLib.recipes.json.RecipesHandler.loadJsonResources;

public class ResearchesHandler {
    private static final Map<String, List<String>> RESEARCHES_NAMES = new HashMap<>();
    private static final Map<String, Class<?>> RESEARCHES_ClASS = new HashMap<>();

    public static void loadResearchesLocations(FMLPreInitializationEvent event) {
        ASMDataTable asm = event.getAsmData();
        Set<ASMDataTable.ASMData> data = asm.getAll(ResearchLocation.class.getName());

        for (ASMDataTable.ASMData entry : data) {
            try {
                Class<?> clazz = Class.forName(entry.getClassName());
                Field field = clazz.getDeclaredField(entry.getObjectName());
                ResearchLocation ann = field.getAnnotation(ResearchLocation.class);
                field.setAccessible(true);
                String[] array = (String[]) field.get(null);
                if (RESEARCHES_NAMES.containsKey(ann.modId())) {
                    List<String> temp = RESEARCHES_NAMES.get(ann.modId());
                    temp.addAll(Arrays.asList(array));
                    RESEARCHES_NAMES.put(ann.modId(), temp);
                } else {
                    RESEARCHES_NAMES.put(ann.modId(), Arrays.asList(array));
                }
                RESEARCHES_ClASS.put(ann.modId(), clazz);
            } catch (Exception e) {
                System.err.println("Failed: " + e);
            }
        }
    }

    public static void init() {
        List<JsonObject> researches = new ArrayList<>();
        RESEARCHES_NAMES.forEach((s, strings) -> strings.forEach(s1 -> {
            try {
                researches.addAll(loadJsonResources(RESEARCHES_ClASS.get(s), "assets/" + s + "/" + s1));
            } catch (IOException | URISyntaxException ignored) {
            }
        }));
        researches.forEach(jsonObject -> {
            if (jsonObject.has("aspects")) {
                parseAspects(jsonObject);
            }
            if (jsonObject.has("categories")) {
                parseCategories(jsonObject);
            }
            if (jsonObject.has("pages")) {
                parsePages(jsonObject);
            }
        });
    }

    private static void parseAspects(JsonObject object) {
        JsonObject obj = object.getAsJsonObject("aspects");
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            if (entry.getValue().isJsonArray()) {
                JsonArray array = entry.getValue().getAsJsonArray();
                ItemStack stack = RecipeUtils.getItemStack(entry.getKey(), true);
                if (stack != null) {
                    AspectList list = new AspectList();
                    for (JsonElement element : array) {
                        if (element.isJsonPrimitive()) {
                            String[] elements = element.getAsString().split(":");
                            Aspect aspect = Aspect.getAspect(elements[0]);
                            if (aspect != null) {
                                list.add(aspect, Integer.parseInt(elements[1]));
                            }
                        }
                    }
                    if (list.size() > 0) {
                        ThaumcraftApi.registerObjectTag(stack, list);
                    }
                }
            }
        }
    }

    private static void parseCategories(JsonObject object) {
        JsonObject obj = object.getAsJsonObject("categories");
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            if (entry.getValue().isJsonObject()) {
                JsonObject obj2 = entry.getValue().getAsJsonObject();
                String[] iconStr = obj2.get("icon").getAsString().split(":");
                ResourceLocation icon = new ResourceLocation(iconStr[0], iconStr[1]);
                String[] backgroundStr = obj2.get("background").getAsString().split(":");
                ResourceLocation background = new ResourceLocation(backgroundStr[0], backgroundStr[1]);
                ResearchCategories.registerCategory(entry.getKey(), icon, background);
            }
        }
    }

    private static void parsePages(JsonObject object) {
        JsonObject obj = object.getAsJsonObject("pages");
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            if (entry.getValue().isJsonObject()) {
                JsonObject obj2 = entry.getValue().getAsJsonObject();

                ItemStack icon = RecipeUtils.getItemStack(obj2.get("icon").getAsString(), true);
                if (icon == null) {
                    continue;
                }
                String category = obj2.get("category").getAsString();
                int x = obj2.get("x").getAsInt();
                int y = obj2.get("y").getAsInt();
                int complex = obj2.get("complex").getAsInt();

                List<String> aspectsKeys = new ArrayList<>();
                for (JsonElement aspect : obj2.get("aspects").getAsJsonArray()) {
                    aspectsKeys.add(aspect.getAsString());
                }

                AspectList list = ThaumRecipesUtils.getAspectsFrom(aspectsKeys);

                ResearchItem researchItem = new ResearchItem(entry.getKey(), category, list, x, y, complex, icon);

                if (obj2.has("text")) {
                    List<ResearchPage> pages = new ArrayList<>();
                    for (JsonElement page : obj2.get("text").getAsJsonArray()) {
                        pages.add(new ResearchPage(page.getAsString()));
                    }

                    researchItem.setPages(pages.toArray(new ResearchPage[0]));
                }
                if (obj2.has("parents")) {
                    List<String> parents = new ArrayList<>();
                    for (JsonElement page : obj2.get("parents").getAsJsonArray()) {
                        parents.add(page.getAsString());
                    }

                    researchItem.setParents(parents.toArray(new String[0]));
                }

                researchItem.registerResearchItem();
            }
        }
    }
}
