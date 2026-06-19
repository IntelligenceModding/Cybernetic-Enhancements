package de.artemis.cyberneticenhancements.client.screen;

import net.minecraft.client.gui.GuiGraphics;

final class WikiFrameRenderer {
    private WikiFrameRenderer() {
    }

    static void drawPanel(GuiGraphics guiGraphics, int x, int y, int width, int height, int fill) {
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, fill);
        guiGraphics.fill(x, y, x + width, y + 1, StationScreenStyle.FRAME_HIGHLIGHT);
        guiGraphics.fill(x, y, x + 1, y + height, StationScreenStyle.FRAME_HIGHLIGHT);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + 2, StationScreenStyle.FRAME_HIGHLIGHT_SOFT);
        guiGraphics.fill(x + 1, y + 1, x + 2, y + height - 1, StationScreenStyle.FRAME_HIGHLIGHT_SOFT);
        guiGraphics.fill(x + 1, y + height - 2, x + width - 1, y + height - 1, StationScreenStyle.FRAME_SHADOW_SOFT);
        guiGraphics.fill(x + width - 2, y + 1, x + width - 1, y + height - 1, StationScreenStyle.FRAME_SHADOW_SOFT);
        guiGraphics.fill(x, y + height - 1, x + width, y + height, StationScreenStyle.FRAME_SHADOW);
        guiGraphics.fill(x + width - 1, y, x + width, y + height, StationScreenStyle.FRAME_SHADOW);
    }

    static void drawPanelWithAccentTop(GuiGraphics guiGraphics, int x, int y, int width, int height, int fill, int accent) {
        drawPanel(guiGraphics, x, y, width, height, fill);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + 2, accent);
    }
}
