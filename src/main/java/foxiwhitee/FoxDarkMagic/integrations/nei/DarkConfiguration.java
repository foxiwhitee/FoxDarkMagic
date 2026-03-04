package foxiwhitee.FoxDarkMagic.integrations.nei;

import codechicken.nei.PositionedStack;
import com.djgiannuzz.thaumcraftneiplugin.items.ItemAspect;
import foxiwhitee.FoxDarkMagic.DarkCore;
import foxiwhitee.FoxDarkMagic.config.ContentConfig;
import foxiwhitee.FoxDarkMagic.integrations.appeng.client.gui.encoders.GuiArcaneEncoder;
import foxiwhitee.FoxDarkMagic.integrations.nei.helpers.AspectInItemsHelper;
import foxiwhitee.FoxDarkMagic.integrations.nei.helpers.AspectRecipeHelper;
import foxiwhitee.FoxLib.nei.NeiConfiguration;
import foxiwhitee.FoxLib.nei.NeiConfigurationMethod;
import foxiwhitee.FoxLib.nei.NeiProcessor;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;

import java.util.*;
import java.util.stream.Collectors;

import static foxiwhitee.FoxDarkMagic.proxy.ClientProxy.allAspects;

@NeiConfiguration(modID = DarkCore.MODID, modVersion = DarkCore.VERSION, modName = DarkCore.MODNAME, needMods = "thaumcraftneiplugin")
@SuppressWarnings("unused")
public class DarkConfiguration extends NeiProcessor {
    private static final int[][] positions = new int[][]{{49, 33}, {76, 33}, {104, 33}, {50, 60}, {77, 60}, {104, 60}, {50, 87}, {77, 87}, {104, 87}};

    @NeiConfigurationMethod
    public void aspectRecipe() {
        var handler = this.<Aspect>newRecipeHandler("aspectRecipes")
            .recipes(allAspects)
            .perPage(7)
            .processInputs(AspectRecipeHelper::processInputs)
            .processOutputs(AspectRecipeHelper::processOutputs)
            .drawBG().addMethod(AspectRecipeHelper::drawBG)
            .processLoading(AspectRecipeHelper::processLoading)
            .processUsage(AspectRecipeHelper::processUsage)
            .register();

        configuration()
            .addHandlerInfo(handler.getHandlerId(), "Thaumcraft:ItemResearchNotes", 192, 43, 7);
    }

    @NeiConfigurationMethod
    public void aspectInItems() {
        var handler = this.<AspectInItemsHelper.ItemsWithAspect>newRecipeHandler("aspectInItems")
            .recipes(allAspects.stream().map(aspect -> new AspectInItemsHelper.ItemsWithAspect(aspect, new ArrayList<>())).collect(Collectors.toList()))
            .perPage(2)
            .processInputs(AspectInItemsHelper::processInputs)
            .processOutputs(AspectInItemsHelper::processOutputs)
            .drawBG().addMethod(AspectInItemsHelper::drawBG)
            .processLoading(AspectInItemsHelper::processLoading)
            .processUsage((l, c, i) -> {})
            .register();

        configuration()
            .addHandlerInfo(handler.getHandlerId(), "Thaumcraft:ItemResearchNotes", 192, 147, 2);
    }

    @NeiConfigurationMethod(needMods = "thaumicenergistics")
    public void config() {
        configuration()
            .doNextIf(ContentConfig.enableBasicArcaneMolecularAssembler || ContentConfig.enableAdvancedArcaneMolecularAssembler || ContentConfig.enableHybridArcaneMolecularAssembler || ContentConfig.enableUltimateArcaneMolecularAssembler)
            .addOverlay(GuiArcaneEncoder.class, "arcaneshapedrecipes")
            .addOverlay(GuiArcaneEncoder.class, "arcaneshapelessrecipes")
            .addOverlaySorting(GuiArcaneEncoder.class, DarkConfiguration::sort);
    }

    private static List<PositionedStack> sort(List<PositionedStack> items) {
        List<PositionedStack> result = new LinkedList<>();
        Map<Integer, PositionedStack> stacks = new HashMap<>();
        for (PositionedStack pStack : items) {
            if (pStack == null || isAspect(pStack.item)) {
                continue;
            }
            stacks.put(findPos(pStack), pStack);
        }

        for (int i = 0; i < 9; i++) {
            result.add(stacks.getOrDefault(i, null));
        }
        return result;
    }

    private static boolean isAspect(ItemStack item) {
        return item != null && item.getItem() instanceof ItemAspect;
    }

    private static int findPos(PositionedStack pStack) {
        for(int i = 0; i < positions.length; ++i) {
            System.out.println(pStack.toString() + " x: " + pStack.relx + " y: " + pStack.rely);
            System.out.println("Need " + i + " x: " + positions[i][0] + " y: " + positions[i][1]);
            if (pStack.relx == positions[i][0] && pStack.rely == positions[i][1]) {
                return i;
            }
        }

        return -1;
    }

}
