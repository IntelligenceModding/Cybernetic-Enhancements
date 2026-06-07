package de.artemis.cyberneticenhancements.client.screen;

import de.artemis.cyberneticenhancements.client.tooltip.ModTooltipStyle;
import de.artemis.cyberneticenhancements.client.tooltip.UpgradeProgressClientTooltip;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.List;

final class StationUpgradeTooltipRenderer {
    private StationUpgradeTooltipRenderer() {
    }

    static void renderTooltip(GuiGraphics guiGraphics, Font font, int screenWidth, int screenHeight, List<Component> lines, float progress, int mouseX, int mouseY) {
        int textWidth = 0;
        for (Component line : lines) {
            textWidth = Math.max(textWidth, font.width(line));
        }

        int barWidth = UpgradeProgressClientTooltip.getBarWidth();
        int barHeight = UpgradeProgressClientTooltip.getBarHeight();
        int contentWidth = Math.max(textWidth, barWidth);
        int lineAdvance = font.lineHeight + ModTooltipStyle.LINE_SPACING;
        int textHeight = lines.size() * lineAdvance - ModTooltipStyle.LINE_SPACING;
        int tooltipWidth = contentWidth + ModTooltipStyle.CONTENT_PADDING * 2;
        int tooltipHeight = ModTooltipStyle.CONTENT_PADDING + textHeight + ModTooltipStyle.BAR_GAP + barHeight + ModTooltipStyle.BAR_BOTTOM_PADDING;

        int tooltipX = mouseX + ModTooltipStyle.TOOLTIP_CURSOR_OFFSET_X;
        if (tooltipX + tooltipWidth > screenWidth - ModTooltipStyle.SCREEN_EDGE_MARGIN) {
            tooltipX = mouseX - ModTooltipStyle.TOOLTIP_CURSOR_OFFSET_X - tooltipWidth;
        }
        tooltipX = Math.max(ModTooltipStyle.SCREEN_EDGE_MARGIN, tooltipX);

        int tooltipY = mouseY - ModTooltipStyle.TOOLTIP_CURSOR_OFFSET_Y;
        if (tooltipY + tooltipHeight > screenHeight - ModTooltipStyle.SCREEN_EDGE_MARGIN) {
            tooltipY = screenHeight - ModTooltipStyle.SCREEN_EDGE_MARGIN - tooltipHeight;
        }
        tooltipY = Math.max(ModTooltipStyle.SCREEN_EDGE_MARGIN, tooltipY);

        int tooltipX2 = tooltipX + tooltipWidth;
        int tooltipY2 = tooltipY + tooltipHeight;
        guiGraphics.flush();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 400.0F);
        RenderSystem.disableDepthTest();

        ModTooltipStyle.drawTooltipFrame(guiGraphics, tooltipX, tooltipY, tooltipX2, tooltipY2);

        int textX = tooltipX + ModTooltipStyle.CONTENT_PADDING;
        int textY = tooltipY + ModTooltipStyle.CONTENT_PADDING;
        for (Component line : lines) {
            guiGraphics.drawString(font, line.getVisualOrderText(), textX, textY, ModTooltipStyle.TEXT_FALLBACK, false);
            textY += lineAdvance;
        }

        int barX = tooltipX + ModTooltipStyle.CONTENT_PADDING;
        int barY = tooltipY + ModTooltipStyle.CONTENT_PADDING + textHeight + ModTooltipStyle.BAR_GAP;
        UpgradeProgressClientTooltip.renderBar(guiGraphics, barX, barY, progress);

        guiGraphics.flush();
        RenderSystem.enableDepthTest();
        guiGraphics.pose().popPose();
    }
}
