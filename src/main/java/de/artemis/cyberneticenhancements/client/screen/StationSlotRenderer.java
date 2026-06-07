package de.artemis.cyberneticenhancements.client.screen;

import de.artemis.cyberneticenhancements.common.cyberware.CyberwareTier;
import de.artemis.cyberneticenhancements.common.menu.AbstractBaseMenu;
import net.minecraft.client.gui.GuiGraphics;

final class StationSlotRenderer {
    static final int SLOT_SIZE = 16;
    static final int SLOT_FRAME_SIZE = AbstractBaseMenu.SLOT_SPACING;
    static final int UPGRADE_SLOT_FRAME_SIZE = SLOT_FRAME_SIZE + 2;

    private static final int SLOT_FRAME_INSET = (SLOT_FRAME_SIZE - SLOT_SIZE) / 2;
    private static final int UPGRADE_FRAME_PADDING = (UPGRADE_SLOT_FRAME_SIZE - SLOT_FRAME_SIZE) / 2;
    private static final int SLOT_FRAME_HIGHLIGHT = 0xFF3B5164;
    private static final int SLOT_FRAME_HIGHLIGHT_SOFT = 0xFF2B3C4D;
    private static final int SLOT_FRAME_SHADOW_SOFT = 0xFF141C24;
    private static final int SLOT_FRAME_SHADOW = 0xFF090E13;

    private StationSlotRenderer() {
    }

    static void drawStandardSlot(GuiGraphics guiGraphics, int leftPos, int topPos, int x, int y, int fill) {
        int drawX = leftPos + x - SLOT_FRAME_INSET;
        int drawY = topPos + y - SLOT_FRAME_INSET;
        int innerX = drawX + SLOT_FRAME_INSET;
        int innerY = drawY + SLOT_FRAME_INSET;
        int innerX2 = innerX + SLOT_SIZE;
        int innerY2 = innerY + SLOT_SIZE;

        guiGraphics.fill(innerX, innerY, innerX2, innerY2, fill);

        guiGraphics.fill(drawX, drawY, drawX + SLOT_FRAME_SIZE - 1, drawY + 1, SLOT_FRAME_HIGHLIGHT);
        guiGraphics.fill(drawX, drawY, drawX + 1, drawY + SLOT_FRAME_SIZE - 1, SLOT_FRAME_HIGHLIGHT);
        guiGraphics.fill(drawX + 1, drawY + 1, drawX + SLOT_FRAME_SIZE - 1, drawY + 2, SLOT_FRAME_HIGHLIGHT_SOFT);
        guiGraphics.fill(drawX + 1, drawY + 1, drawX + 2, drawY + SLOT_FRAME_SIZE - 1, SLOT_FRAME_HIGHLIGHT_SOFT);

        guiGraphics.fill(drawX + 1, drawY + SLOT_FRAME_SIZE - 2, drawX + SLOT_FRAME_SIZE, drawY + SLOT_FRAME_SIZE - 1, SLOT_FRAME_SHADOW_SOFT);
        guiGraphics.fill(drawX + SLOT_FRAME_SIZE - 2, drawY + 1, drawX + SLOT_FRAME_SIZE - 1, drawY + SLOT_FRAME_SIZE, SLOT_FRAME_SHADOW_SOFT);
        guiGraphics.fill(drawX + 1, drawY + SLOT_FRAME_SIZE - 1, drawX + SLOT_FRAME_SIZE, drawY + SLOT_FRAME_SIZE, SLOT_FRAME_SHADOW);
        guiGraphics.fill(drawX + SLOT_FRAME_SIZE - 1, drawY + 1, drawX + SLOT_FRAME_SIZE, drawY + SLOT_FRAME_SIZE - 1, SLOT_FRAME_SHADOW);
    }

    static void drawUpgradeableSlot(GuiGraphics guiGraphics, int leftPos, int topPos, int x, int y, int fill, int outline) {
        drawStandardSlot(guiGraphics, leftPos, topPos, x, y, fill);
        int frameX = leftPos + x - SLOT_FRAME_INSET - UPGRADE_FRAME_PADDING;
        int frameY = topPos + y - SLOT_FRAME_INSET - UPGRADE_FRAME_PADDING;
        drawPixelFrame(guiGraphics, frameX, frameY, UPGRADE_SLOT_FRAME_SIZE, UPGRADE_SLOT_FRAME_SIZE, outline);
    }

    static int getTierFrameColor(CyberwareTier tier) {
        return switch (tier) {
            case TIER_1 -> 0xFF4A5662;
            case TIER_2 -> 0xFF3FAE6A;
            case TIER_3 -> 0xFF35A8E0;
            case TIER_4 -> 0xFFAA63E8;
            case TIER_5 -> 0xFFE2A93A;
        };
    }

    private static void drawPixelFrame(GuiGraphics guiGraphics, int x, int y, int width, int height, int color) {
        guiGraphics.fill(x, y, x + width, y + 1, color);
        guiGraphics.fill(x, y + height - 1, x + width, y + height, color);
        guiGraphics.fill(x, y + 1, x + 1, y + height - 1, color);
        guiGraphics.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }
}
