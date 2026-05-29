package de.artemis.cyberneticenhancements.client.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public final class UpgradeProgressClientTooltip implements ClientTooltipComponent {
    private static final int BAR_WIDTH = 84;
    private static final int BAR_HEIGHT = 8;
    private static final int TOOLTIP_HEIGHT = BAR_HEIGHT;
    private static final int BAR_FILL = 0xFF1EE2B5;
    private static final int BAR_FILL_GLOW = 0x661EE2B5;

    private final float progress;

    public UpgradeProgressClientTooltip(UpgradeProgressTooltip tooltip) {
        this.progress = tooltip.progress();
    }

    @Override
    public int getHeight() {
        return TOOLTIP_HEIGHT;
    }

    @Override
    public int getWidth(Font font) {
        return BAR_WIDTH;
    }

    public static int getBarWidth() {
        return BAR_WIDTH;
    }

    public static int getBarHeight() {
        return BAR_HEIGHT;
    }

    public static void renderBar(GuiGraphics guiGraphics, int x, int y, float progress) {
        int x2 = x + BAR_WIDTH;
        int y2 = y + BAR_HEIGHT;
        int fillWidth = Math.max(0, Math.min(BAR_WIDTH - 2, Math.round((BAR_WIDTH - 2) * progress)));

        ModTooltipStyle.drawTooltipFrame(guiGraphics, x, y, x2, y2);

        if (fillWidth > 0) {
            guiGraphics.fill(x + 1, y + 1, x + 1 + fillWidth, y2 - 1, BAR_FILL);
            guiGraphics.fill(x + 1, y + 1, x + 1 + fillWidth, y + 2, BAR_FILL_GLOW);
        }
    }

    @Override
    public void renderImage(Font font, int x, int y, GuiGraphics guiGraphics) {
        renderBar(guiGraphics, x, y, progress);
    }
}
