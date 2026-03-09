package foxiwhitee.FoxDarkMagic.integrations.appeng.client.gui.assemblers;

import foxiwhitee.FoxDarkMagic.client.gui.DMGui;
import foxiwhitee.FoxDarkMagic.integrations.appeng.container.assemblers.ContainerArcaneAssembler;
import foxiwhitee.FoxDarkMagic.integrations.appeng.tile.assemblers.arcane.TileArcaneAssembler;
import foxiwhitee.FoxLib.utils.ProductivityUtil;
import foxiwhitee.FoxLib.utils.helpers.EnergyUtility;
import foxiwhitee.FoxLib.utils.helpers.LocalizationUtils;
import foxiwhitee.FoxLib.utils.helpers.UtilGui;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumicenergistics.client.gui.ThEGuiHelper;

import java.util.ArrayList;
import java.util.List;

import static thaumicenergistics.common.tiles.TileArcaneAssembler.PRIMALS;

@SuppressWarnings("unused")
public class GuiArcaneAssembler extends DMGui {
    private final TileArcaneAssembler tile;

    public GuiArcaneAssembler(ContainerArcaneAssembler container) {
        super(container, 252, 259);
        this.tile = (TileArcaneAssembler) container.getTileEntity();
    }

    @Override
    public void drawFG(int offsetX, int offsetY, int mouseX, int mouseY) {
        super.drawFG(offsetX, offsetY, mouseX, mouseY);
        AspectList aspects = tile.getStoredVis();
        List<Runnable> runnables = new ArrayList<>();
        for (int i = 0; i < PRIMALS.length; i++) {
            Aspect aspect = PRIMALS[i];
            double amount = aspects.getAmount(aspect) / 100d;
            if (amount >= 0) {
                int maxStored = tile.getMaxStoredVis() / 100;
                double y = ProductivityUtil.gauge(16, amount, maxStored);
                UtilGui.drawTexture(52 + 18 * i, 151 - y, i * 4, 272, 4, y, 4, y, 512.0D, 512.0D);
                String text = ThEGuiHelper.INSTANCE.getAspectChatColor(aspect) + aspect.getName() + "\n" +
                    EnergyUtility.formatNumber(amount) + " / " + EnergyUtility.formatNumber(maxStored) + "\n" +
                    LocalizationUtils.localize("tooltip.assembler.discount", (int)(tile.getVisDiscountForAspect(aspect) * 100));
                int finalI = i;
                runnables.add(() -> drawIfInMouse(mouseX, mouseY, 51 + 18 * finalI, 134, 6, 18,  text));
            }
        }
        runnables.forEach(Runnable::run);
    }

    @Override
    protected String getBackground() {
        return "gui/" + tile.getGuiName() + ".png";
    }
}
