package foxiwhitee.FoxDarkMagic.integrations.nei;

import codechicken.nei.PositionedStack;
import foxiwhitee.FoxDarkMagic.DarkCore;
import foxiwhitee.FoxDarkMagic.config.ContentConfig;
import foxiwhitee.FoxDarkMagic.integrations.appeng.client.gui.encoders.GuiArcaneEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.client.gui.encoders.GuiCrucibleEncoder;
import foxiwhitee.FoxDarkMagic.integrations.appeng.client.gui.encoders.GuiInfusionEncoder;
import foxiwhitee.FoxLib.nei.NeiConfiguration;
import foxiwhitee.FoxLib.nei.NeiConfigurationMethod;
import foxiwhitee.FoxLib.nei.NeiProcessor;

import java.util.*;

@NeiConfiguration(modID = DarkCore.MODID, modVersion = DarkCore.VERSION, modName = DarkCore.MODNAME, needMods = "aspectrecipeindex")
@SuppressWarnings("unused")
public class DarkConfiguration extends NeiProcessor {
    private static final int[][] positions = new int[][]{{47, 33}, {75, 33}, {103, 33}, {47, 60}, {75, 60}, {103, 60}, {47, 87}, {75, 87}, {103, 87}};

    @NeiConfigurationMethod(needMods = "thaumicenergistics")
    public void config() {
        configuration()
            .doNextIf(ContentConfig.enableBasicArcaneMolecularAssembler || ContentConfig.enableAdvancedArcaneMolecularAssembler || ContentConfig.enableHybridArcaneMolecularAssembler || ContentConfig.enableUltimateArcaneMolecularAssembler)
            .addOverlay(GuiArcaneEncoder.class, "thaumcraft.arcane.shaped")
            .addOverlay(GuiArcaneEncoder.class, "thaumcraft.arcane.shapeless")
            .addOverlay(GuiArcaneEncoder.class, "thaumcraft.wands")
            .addOverlaySorting(GuiArcaneEncoder.class, DarkConfiguration::sort)
            .doNextIf(ContentConfig.enableCrucibleMolecularAssembler)
            .addOverlay(GuiCrucibleEncoder.class, "thaumcraft.alchemy")
            .doNextIf(ContentConfig.enableInfusionMolecularAssembler)
            .addOverlay(GuiInfusionEncoder.class, "thaumcraft.infusion");
    }

    private static List<PositionedStack> sort(List<PositionedStack> items) {
        List<PositionedStack> result = new LinkedList<>();
        Map<Integer, PositionedStack> stacks = new HashMap<>();
        for (PositionedStack pStack : items) {
            if (pStack == null) {
                continue;
            }
            stacks.put(findPos(pStack), pStack);
        }

        for (int i = 0; i < 9; i++) {
            result.add(stacks.getOrDefault(i, null));
        }
        return result;
    }

    private static int findPos(PositionedStack pStack) {
        for(int i = 0; i < positions.length; ++i) {
            if (pStack.relx == positions[i][0] && pStack.rely == positions[i][1]) {
                return i;
            }
        }

        return -1;
    }

}
