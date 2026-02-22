package foxiwhitee.FoxDarkMagic.client.gui.elements;

import foxiwhitee.FoxLib.utils.helpers.UtilGui;
import org.lwjgl.input.Mouse;

public class ScrollBar {
    private final static int width = 12, height = 15;
    private final int x, y, rows, colons, scrollBarHeight, xStart, yStart;
    private float currentScroll;
    private int totalItems;
    private boolean isScrolling;
    private boolean wasMouseDown;
    private boolean isClicking;

    public ScrollBar(int x, int y, int rows, int colons, int scrollBarHeight, int xStart, int yStart) {
        this.x = x;
        this.y = y;
        this.rows = rows;
        this.colons = colons;
        this.scrollBarHeight = scrollBarHeight;
        this.xStart = xStart;
        this.yStart = yStart;
    }

    public void draw(int guiLeft, int guiTop) {
        int renderX = guiLeft + this.x;
        int renderY = guiTop + this.y;

        if (this.needsScrollbar()) {
            int scrollOffset = (int)((float)(this.scrollBarHeight - height) * this.currentScroll);
            UtilGui.drawTexture(renderX, renderY + scrollOffset, xStart, yStart, width, height, width, height);
        }
    }

    public void handleMouseLogic(int guiLeft, int guiTop, int mouseX, int mouseY) {
        boolean isMouseDown = Mouse.isButtonDown(0);
        int absoluteX = guiLeft + this.x;
        int absoluteY = guiTop + this.y;

        boolean isHovered;
        if (!this.wasMouseDown && mouseX >= absoluteX && mouseY >= absoluteY && mouseX < absoluteX + width && mouseY < absoluteY + scrollBarHeight) {
            isHovered = true;
        } else {
            isHovered = this.isClicking;
        }

        if (isMouseDown && isHovered) {
            this.isScrolling = this.needsScrollbar();
            this.isClicking = true;
        } else {
            this.isClicking = false;
        }

        if (!isMouseDown) {
            this.isScrolling = false;
        }

        this.wasMouseDown = isMouseDown;

        if (this.isScrolling) {
            this.currentScroll = ((float)(mouseY - absoluteY) - 7.5F) / ((float)scrollBarHeight - height);

            if (this.currentScroll < 0.0F) {
                this.currentScroll = 0.0F;
            }
            if (this.currentScroll > 1.0F) {
                this.currentScroll = 1.0F;
            }
        }
    }

    public void handleMouseWheel() {
        int dWheel = Mouse.getEventDWheel();
        if (dWheel != 0 && this.needsScrollbar()) {
            int maxScrollRows = this.totalItems / this.colons - this.rows + 1;

            if (dWheel > 0) {
                dWheel = 1;
            }
            if (dWheel < 0) {
                dWheel = -1;
            }

            this.currentScroll = (float)((double)this.currentScroll - (double)dWheel / (double)maxScrollRows);

            if (this.currentScroll < 0.0F) {
                this.currentScroll = 0.0F;
            }
            if (this.currentScroll > 1.0F) {
                this.currentScroll = 1.0F;
            }
        }
    }

    public boolean needsScrollbar() {
        return this.totalItems + 1 > this.colons * this.rows;
    }

    public int getScrollRow() {
        int maxScrollRows = this.totalItems / this.colons - this.rows + 1;
        int currentRow = (int)((double)(this.currentScroll * (float)maxScrollRows) + 0.5);

        if (currentRow < 0) {
            currentRow = 0;
        }
        return currentRow;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }
}
