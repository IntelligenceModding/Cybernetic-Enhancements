package de.artemis.cyberneticenhancements.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public final class WikiLaunchButton extends AbstractButton {
    private final Runnable onPress;

    public WikiLaunchButton(int x, int y, int width, int height, Component message, Runnable onPress) {
        super(x, y, width, height, message);
        this.onPress = onPress;
    }

    @Override
    public void onPress() {
        onPress.run();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        var font = Minecraft.getInstance().font;
        int fill = isHoveredOrFocused() ? StationScreenStyle.SLOT_ACTIVE : StationScreenStyle.PANEL_ALT;
        WikiFrameRenderer.drawPanelWithAccentTop(guiGraphics, getX(), getY(), width, height, fill, isHoveredOrFocused() ? StationScreenStyle.ACCENT : StationScreenStyle.FRAME_HIGHLIGHT_SOFT);
        guiGraphics.drawCenteredString(font, getMessage(), getX() + width / 2, getY() + (height - 8) / 2, active ? StationScreenStyle.TEXT_PRIMARY : StationScreenStyle.TEXT_SECONDARY);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        defaultButtonNarrationText(narrationElementOutput);
    }
}
