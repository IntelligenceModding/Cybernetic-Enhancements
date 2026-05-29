package de.artemis.cyberneticenhancements.client.screen;

import net.minecraft.client.gui.GuiGraphics;

final class StationSlotRenderer {
    static final int SLOT_SIZE = 16;
    static final int SLOT_FRAME_SIZE = 18;
    static final int UPGRADE_SLOT_FRAME_SIZE = 20;

    private static final int SLOT_OUTER = 0xFF141C24;
    private static final int SLOT_HIGHLIGHT = 0xFF384754;
    private static final int SLOT_SHADOW = 0xFF090E13;

    private StationSlotRenderer() {
    }

    static void drawStandardSlot(GuiGraphics guiGraphics, int leftPos, int topPos, int x, int y, int fill) {
        int drawX = leftPos + x - 1;
        int drawY = topPos + y - 1;
        guiGraphics.fill(drawX, drawY, drawX + SLOT_FRAME_SIZE, drawY + SLOT_FRAME_SIZE, SLOT_OUTER);
        guiGraphics.fill(drawX + 1, drawY + 1, drawX + SLOT_FRAME_SIZE - 1, drawY + SLOT_FRAME_SIZE - 1, fill);
        guiGraphics.fill(drawX + 1, drawY + 1, drawX + SLOT_FRAME_SIZE - 1, drawY + 2, SLOT_HIGHLIGHT);
        guiGraphics.fill(drawX + 1, drawY + 1, drawX + 2, drawY + SLOT_FRAME_SIZE - 1, SLOT_HIGHLIGHT);
        guiGraphics.fill(drawX + 1, drawY + SLOT_FRAME_SIZE - 2, drawX + SLOT_FRAME_SIZE - 1, drawY + SLOT_FRAME_SIZE - 1, SLOT_SHADOW);
        guiGraphics.fill(drawX + SLOT_FRAME_SIZE - 2, drawY + 1, drawX + SLOT_FRAME_SIZE - 1, drawY + SLOT_FRAME_SIZE - 1, SLOT_SHADOW);
    }

    static void drawUpgradeableSlot(GuiGraphics guiGraphics, int leftPos, int topPos, int x, int y, int fill, int outline) {
        drawStandardSlot(guiGraphics, leftPos, topPos, x, y, fill);
        guiGraphics.renderOutline(leftPos + x - 2, topPos + y - 2, UPGRADE_SLOT_FRAME_SIZE, UPGRADE_SLOT_FRAME_SIZE, outline);
    }
}
