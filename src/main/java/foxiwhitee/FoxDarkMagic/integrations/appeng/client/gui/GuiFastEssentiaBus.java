package foxiwhitee.FoxDarkMagic.integrations.appeng.client.gui;

import appeng.api.config.RedstoneMode;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import foxiwhitee.FoxDarkMagic.integrations.appeng.container.ContainerFastEssentiaBus;
import foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.server.C2SBusCraftingModeChangePacket;
import foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.server.C2SBusRedstoneModeChangePacket;
import foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.server.C2SBusRequestFilterListPacket;
import foxiwhitee.FoxDarkMagic.integrations.appeng.network.packets.server.C2SBusVoidModeChangePacket;
import foxiwhitee.FoxDarkMagic.integrations.appeng.parts.PartFastEssentiaBus;
import foxiwhitee.FoxDarkMagic.integrations.appeng.parts.PartFastEssentiaExportBus;
import foxiwhitee.FoxDarkMagic.integrations.appeng.parts.PartFastEssentiaImportBus;
import foxiwhitee.FoxLib.network.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumicenergistics.api.gui.IAspectSlotGui;
import thaumicenergistics.client.gui.abstraction.ThEBaseGui;
import thaumicenergistics.client.gui.buttons.GuiButtonAllowVoid;
import thaumicenergistics.client.gui.buttons.GuiButtonCraftingMode;
import thaumicenergistics.client.gui.buttons.GuiButtonRedstoneModes;
import thaumicenergistics.client.gui.widget.ThEWidget;
import thaumicenergistics.client.gui.widget.WidgetAspectSlot;
import thaumicenergistics.client.textures.GuiTextureManager;
import thaumicenergistics.common.integration.tc.EssentiaItemContainerHelper;
import thaumicenergistics.common.registries.ThEStrings;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

// Taken From Thaumic Energistics
@SideOnly(Side.CLIENT)
public class GuiFastEssentiaBus extends ThEBaseGui implements WidgetAspectSlot.IConfigurable, IAspectSlotGui {
    private static final int FILTER_GRID_SIZE = 3;
    private static final byte[] WIDGET_CONFIG_BYTES = new byte[] { 2, 1, 2, 1, 0, 1, 2, 1, 2 };
    private static final int WIDGET_X_POSITION = 61;
    private static final int WIDGET_Y_POSITION = 21;
    private static final int GUI_HEIGHT = 184;
    private static final int GUI_WIDTH_NO_TOOL = 211;
    private static final int GUI_WIDTH_WITH_TOOL = 246;
    private static final int GUI_MAIN_WIDTH = 176;
    private static final int GUI_UPGRADES_WIDTH = 35;
    private static final int GUI_UPGRADES_HEIGHT = 86;
    private static final int TITLE_POS_X = 6, TITLE_POS_Y = 5;
    private static final int REDSTONE_CONTROL_BUTTON_POS_Y = 2, REDSTONE_CONTROL_BUTTON_POS_X = -18, REDSTONE_CONTROL_BUTTON_SIZE = 16, REDSTONE_CONTROL_BUTTON_ID = 0;
    private static final int ALLOW_VOID_BUTTON_POS_Y = 2, ALLOW_VOID_BUTTON_POS_X = -19, ALLOW_VOID_BUTTON_ID = 1;
    private static final int CRAFTING_MODE_BUTTON_POS_Y = 40, CRAFTING_MODE_BUTTON_POS_X = -18, CRAFTING_MODE_BUTTON_ID = 2;
    private final PartFastEssentiaBus part;
    private final EntityPlayer player;
    private byte filterSize;
    private final List<WidgetAspectSlot> aspectSlotList = new ArrayList<>();
    private List<Aspect> filteredAspects = new ArrayList<>();
    private final boolean hasNetworkTool;
    private boolean isAdvancedNetworkTool = false;
    private RedstoneMode redstoneMode = RedstoneMode.HIGH_SIGNAL;
    private final String guiTitle;
    private GuiButtonRedstoneModes redstoneControlButton = null;
    private GuiButtonAllowVoid voidModeButton = null;
    private GuiButtonCraftingMode craftingModeButton = null;

    public GuiFastEssentiaBus(final PartFastEssentiaBus partBus, final EntityPlayer player) {
        super(new ContainerFastEssentiaBus(partBus, player));

        this.part = partBus;
        this.player = player;

        this.hasNetworkTool = ((ContainerFastEssentiaBus) this.inventorySlots).hasNetworkTool();
        if (this.hasNetworkTool)
            this.isAdvancedNetworkTool = ((ContainerFastEssentiaBus) this.inventorySlots).isAdvancedNetworkTool();

        if (this.hasNetworkTool) {
            if (this.isAdvancedNetworkTool) {
                this.xSize = GUI_WIDTH_WITH_TOOL + 36;
            } else {
                this.xSize = GUI_WIDTH_WITH_TOOL;
            }
        } else {
            this.xSize = GUI_WIDTH_NO_TOOL;
        }

        this.ySize = this.isAdvancedNetworkTool ? GUI_HEIGHT + 36 : GUI_HEIGHT;

        if (partBus instanceof PartFastEssentiaImportBus) {
            this.guiTitle = ThEStrings.Gui_TitleEssentiaImportBus.getLocalized();
        } else if (partBus instanceof PartFastEssentiaExportBus) {
            this.guiTitle = ThEStrings.Gui_TitleEssentiaExportBus.getLocalized();
        } else {
            this.guiTitle = "";
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(final float alpha, final int mouseX, final int mouseY) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().renderEngine.bindTexture(GuiTextureManager.ESSENTIA_IO_BUS.getTexture());

        if (this.hasNetworkTool) {
            if (this.isAdvancedNetworkTool) {
                this.drawTexturedModalRect(
                    this.guiLeft,
                    this.guiTop,
                    0,
                    0,
                    GUI_MAIN_WIDTH,
                    GUI_HEIGHT);

                this.drawTexturedModalRect(
                    this.guiLeft + GUI_MAIN_WIDTH,
                    this.guiTop,
                    GUI_MAIN_WIDTH,
                    0,
                    GUI_UPGRADES_WIDTH,
                    GUI_UPGRADES_HEIGHT);

                Minecraft.getMinecraft().renderEngine.bindTexture(GuiTextureManager.ADVANCED_TOOLBOX.getTexture());
                this.drawTexturedModalRect(this.guiLeft + 179, this.guiTop + 94, 0, 0, 104, 104);
                Minecraft.getMinecraft().renderEngine.bindTexture(GuiTextureManager.ESSENTIA_IO_BUS.getTexture());

            } else {
                this.drawTexturedModalRect(
                    this.guiLeft,
                    this.guiTop,
                    0,
                    0,
                    GUI_WIDTH_WITH_TOOL,
                    GUI_HEIGHT);
            }
        } else {
            this.drawTexturedModalRect(
                this.guiLeft,
                this.guiTop,
                0,
                0,
                GUI_MAIN_WIDTH,
                GUI_HEIGHT);

            this.drawTexturedModalRect(
                this.guiLeft + GUI_MAIN_WIDTH,
                this.guiTop,
                GUI_MAIN_WIDTH,
                0,
                GUI_UPGRADES_WIDTH,
                GUI_UPGRADES_HEIGHT);
        }

        super.drawAEToolAndUpgradeSlots(alpha, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(final int mouseX, final int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        this.fontRendererObj.drawString(this.guiTitle, TITLE_POS_X, TITLE_POS_Y, 0x000000);

        boolean hoverUnderlayRendered = false;

        WidgetAspectSlot slotUnderMouse = null;

        for (int i = 0; i < 9; i++) {
            WidgetAspectSlot slotWidget = this.aspectSlotList.get(i);

            if ((!hoverUnderlayRendered) && (slotWidget.canRender())
                && (slotWidget.isMouseOverWidget(mouseX, mouseY))) {
                slotWidget.drawMouseHoverUnderlay();

                slotUnderMouse = slotWidget;

                hoverUnderlayRendered = true;
            }

            slotWidget.drawWidget();
        }

        if (slotUnderMouse != null) {
            slotUnderMouse.getTooltip(this.tooltip);
        }
    }

    @Override
    protected void mouseClicked(final int mouseX, final int mouseY, final int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        for (WidgetAspectSlot aspectSlot : this.aspectSlotList) {
            if (aspectSlot.isMouseOverWidget(mouseX, mouseY)) {
                Aspect itemAspect = EssentiaItemContainerHelper.INSTANCE.getFilterAspectFromItem(
                    this.draggedStack == null ? this.player.inventory.getItemStack() : this.draggedStack.copy());

                if (itemAspect != null) {
                    if (this.filteredAspects.contains(itemAspect)) {
                        return;
                    }
                }

                aspectSlot.mouseClicked(itemAspect);

                break;
            }
        }
        this.draggedStack = null;
    }

    @Override
    protected void onButtonClicked(final GuiButton button, final int mouseButton) {
        if (button.id == REDSTONE_CONTROL_BUTTON_ID) {
            NetworkManager.instance.sendToServer(new C2SBusRedstoneModeChangePacket(this.part));
        }
        else if (button.id == ALLOW_VOID_BUTTON_ID) {
            if (this.part instanceof PartFastEssentiaExportBus) {
                NetworkManager.instance.sendToServer(new C2SBusVoidModeChangePacket(this.part));
            }
        } else if (button.id == CRAFTING_MODE_BUTTON_ID) {
            if (this.part instanceof PartFastEssentiaExportBus) {
                NetworkManager.instance.sendToServer(new C2SBusCraftingModeChangePacket(this.part));
            }
        }
    }

    @Override
    public byte getConfigState() {
        return this.filterSize;
    }

    @Override
    public void initGui() {
        super.initGui();

        for (int row = 0; row < FILTER_GRID_SIZE; row++) {
            for (int column = 0; column < FILTER_GRID_SIZE; column++) {
                int index = (row * FILTER_GRID_SIZE) + column;
                int xPos = WIDGET_X_POSITION + (column * ThEWidget.WIDGET_SIZE);
                int yPos = WIDGET_Y_POSITION + (row * ThEWidget.WIDGET_SIZE);

                this.aspectSlotList.add(
                    new WidgetAspectSlot(
                        this,
                        this.player,
                        this.part,
                        index,
                        xPos,
                        yPos,
                        this,
                        WIDGET_CONFIG_BYTES[index]));
            }
        }

        this.redstoneControlButton = new GuiButtonRedstoneModes(
            REDSTONE_CONTROL_BUTTON_ID,
            this.guiLeft + REDSTONE_CONTROL_BUTTON_POS_X,
            this.guiTop + REDSTONE_CONTROL_BUTTON_POS_Y,
            REDSTONE_CONTROL_BUTTON_SIZE,
            REDSTONE_CONTROL_BUTTON_SIZE,
            this.redstoneMode,
            false);

        if (this.part instanceof PartFastEssentiaExportBus) {
            this.voidModeButton = new GuiButtonAllowVoid(
                ALLOW_VOID_BUTTON_ID,
                this.guiLeft + ALLOW_VOID_BUTTON_POS_X,
                this.guiTop + ALLOW_VOID_BUTTON_POS_Y);
            this.buttonList.add(this.voidModeButton);

            craftingModeButton = new GuiButtonCraftingMode(
                CRAFTING_MODE_BUTTON_ID,
                this.guiLeft + CRAFTING_MODE_BUTTON_POS_X,
                this.guiTop + CRAFTING_MODE_BUTTON_POS_Y,
                16,
                16);
        }

        NetworkManager.instance.sendToServer(new C2SBusRequestFilterListPacket(this.part));
    }

    public void onReceiveFilterSize(final byte filterSize) {
        this.part.onReceiveFilterSize(filterSize);

        this.filterSize = filterSize;

        for (WidgetAspectSlot slot : this.aspectSlotList) {
            if (!slot.canRender()) {
                slot.setAspect(null);
            }
        }
    }

    public void onReceiveRedstoneControlled(final boolean newRedstoneControled) {
        if (newRedstoneControled && !this.buttonList.contains(this.redstoneControlButton)) {
            this.buttonList.add(this.redstoneControlButton);

            if (this.voidModeButton != null) {
                this.voidModeButton.yPosition += 18;
            }
        } else if (!newRedstoneControled && this.buttonList.contains(this.redstoneControlButton)) {
            this.buttonList.remove(this.redstoneControlButton);

            if (this.voidModeButton != null) {
                this.voidModeButton.yPosition -= 18;
            }
        }
    }

    public void onReceiveRedstoneMode(final RedstoneMode redstoneMode) {
        this.redstoneControlButton.setRedstoneMode(redstoneMode);
        this.redstoneMode = redstoneMode;
    }

    public void onReceiveHasCrafting(final boolean hasCrafting) {
        if (hasCrafting && !buttonList.contains(craftingModeButton)) {
            buttonList.add(craftingModeButton);
        } else if (!hasCrafting) {
            buttonList.remove(craftingModeButton);
        }
    }

    public void onReceiveCraftingOnly(final boolean craftingOnly) {
        if (craftingModeButton != null) {
            craftingModeButton.setAlwaysCraft(craftingOnly);
        }
    }

    public void onServerSendVoidMode(final boolean isVoidAllowed) {
        if (this.voidModeButton != null) {
            this.voidModeButton.setIsVoidAllowed(isVoidAllowed);
        }
    }

    @Override
    public void updateAspects(@Nonnull final List<Aspect> aspectList) {
        this.part.onReceiveFilterList(aspectList);

        int count = Math.min(this.aspectSlotList.size(), aspectList.size());

        for (int i = 0; i < count; i++) {
            this.aspectSlotList.get(i).setAspect(aspectList.get(i), 1, false);
        }

        this.filteredAspects = aspectList;
    }
}
