package de.artemis.cyberneticenhancements.client.screen;

import de.artemis.cyberneticenhancements.common.cyberware.CyberwareRecyclePlan;
import de.artemis.cyberneticenhancements.common.menu.RecyclerStationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public final class RecyclerStationScreen extends AbstractContainerScreen<RecyclerStationMenu> {
    private static final int IMAGE_WIDTH = 320;
    private static final int IMAGE_HEIGHT = 204;
    private static final int PANEL_BG = 0xFF10161D;
    private static final int PANEL_EDGE = 0xFF2B3C4D;
    private static final int PANEL_ALT = 0xFF151E27;
    private static final int TEXT_PRIMARY = 0xFFE5F2FF;
    private static final int TEXT_SECONDARY = 0xFF9FB5C7;
    private static final int ACCENT = 0xFF1EE2B5;
    private static final int WARNING = 0xFFFF6B6B;

    public RecyclerStationScreen(RecyclerStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = IMAGE_WIDTH;
        this.imageHeight = IMAGE_HEIGHT;
        this.inventoryLabelX = 48;
        this.inventoryLabelY = 106;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x0 = leftPos;
        int y0 = topPos;
        guiGraphics.fill(x0, y0, x0 + imageWidth, y0 + imageHeight, PANEL_EDGE);
        guiGraphics.fill(x0 + 1, y0 + 1, x0 + imageWidth - 1, y0 + imageHeight - 1, PANEL_BG);
        guiGraphics.fill(x0 + 14, y0 + 14, x0 + 306, y0 + 94, PANEL_ALT);
        guiGraphics.fill(x0 + 14, y0 + 100, x0 + 306, y0 + 192, 0xFF0D1319);
        drawSlotBack(guiGraphics, 80, 54);
        drawSlotBack(guiGraphics, 184, 54);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.recycler_station.recycler_bay"), 14, 18, TEXT_PRIMARY, false);
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.recycler_station.scrap_input"), 64, 36, TEXT_SECONDARY, false);
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.recycler_station.reclaimed_output"), 164, 36, TEXT_SECONDARY, false);
        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT_SECONDARY, false);

        CyberwareRecyclePlan plan = menu.getRecyclePlan();
        if (!plan.isAvailable()) {
            guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.recycler_station.no_recycle_output"), 14, 86, WARNING, false);
            return;
        }

        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.recycler_station.recovery"), 14, 86, ACCENT, false);
        guiGraphics.drawString(font, Component.literal(plan.output().getCount() + "x"), 14, 98, ACCENT, false);
        guiGraphics.drawString(font, plan.output().getHoverName(), 34, 98, TEXT_PRIMARY, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void drawSlotBack(GuiGraphics guiGraphics, int x, int y) {
        int drawX = leftPos + x - 2;
        int drawY = topPos + y - 2;
        guiGraphics.fill(drawX, drawY, drawX + 20, drawY + 20, 0xFF202A34);
        guiGraphics.renderOutline(drawX, drawY, 20, 20, PANEL_EDGE);
    }
}
