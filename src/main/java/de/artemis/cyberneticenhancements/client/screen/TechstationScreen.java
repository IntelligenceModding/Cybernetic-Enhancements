package de.artemis.cyberneticenhancements.client.screen;

import de.artemis.cyberneticenhancements.common.cyberware.CyberwareServicePlan;
import de.artemis.cyberneticenhancements.common.menu.TechstationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public final class TechstationScreen extends AbstractContainerScreen<TechstationMenu> {
    private static final int IMAGE_WIDTH = 320;
    private static final int IMAGE_HEIGHT = 220;
    private static final int PANEL_BG = 0xFF10161D;
    private static final int PANEL_EDGE = 0xFF2B3C4D;
    private static final int PANEL_ALT = 0xFF151E27;
    private static final int TEXT_PRIMARY = 0xFFE5F2FF;
    private static final int TEXT_SECONDARY = 0xFF9FB5C7;
    private static final int ACCENT = 0xFF1EE2B5;
    private static final int WARNING = 0xFFFF6B6B;

    public TechstationScreen(TechstationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = IMAGE_WIDTH;
        this.imageHeight = IMAGE_HEIGHT;
        this.inventoryLabelX = 48;
        this.inventoryLabelY = 122;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x0 = leftPos;
        int y0 = topPos;
        guiGraphics.fill(x0, y0, x0 + imageWidth, y0 + imageHeight, PANEL_EDGE);
        guiGraphics.fill(x0 + 1, y0 + 1, x0 + imageWidth - 1, y0 + imageHeight - 1, PANEL_BG);

        guiGraphics.fill(x0 + 14, y0 + 14, x0 + 306, y0 + 110, PANEL_ALT);
        guiGraphics.fill(x0 + 14, y0 + 116, x0 + 306, y0 + 208, 0xFF0D1319);
        drawSlotBack(guiGraphics, 44, 60);
        drawSlotBack(guiGraphics, 116, 42);
        drawSlotBack(guiGraphics, 116, 78);
        drawSlotBack(guiGraphics, 206, 60);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.techstation.service_bay"), 14, 18, TEXT_PRIMARY, false);
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.techstation.operation"), 154, 18, TEXT_PRIMARY, false);
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.techstation.input"), 34, 42, TEXT_SECONDARY, false);
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.techstation.materials"), 98, 18, TEXT_SECONDARY, false);
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.techstation.output"), 198, 42, TEXT_SECONDARY, false);
        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT_SECONDARY, false);

        CyberwareServicePlan resolvedPlan = menu.getResolvedPlan();
        CyberwareServicePlan repairPlan = menu.getRepairPlan();
        CyberwareServicePlan upgradePlan = menu.getUpgradePlan();

        renderPlanSummary(guiGraphics, Component.translatable("screen.cyberneticenhancements.techstation.repair"), repairPlan, 154, 38);
        renderPlanSummary(guiGraphics, Component.translatable("screen.cyberneticenhancements.techstation.upgrade"), upgradePlan, 154, 74);

        Component activeLabel = resolvedPlan.type() == CyberwareServicePlan.Type.NONE
                ? Component.translatable("screen.cyberneticenhancements.techstation.no_valid_operation")
                : Component.translatable(resolvedPlan.type() == CyberwareServicePlan.Type.REPAIR
                ? "screen.cyberneticenhancements.techstation.ready_repair"
                : "screen.cyberneticenhancements.techstation.ready_upgrade");
        guiGraphics.drawString(font, activeLabel, 14, 100, resolvedPlan.isAvailable() ? ACCENT : WARNING, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderPlanSummary(GuiGraphics guiGraphics, Component title, CyberwareServicePlan plan, int x, int y) {
        guiGraphics.drawString(font, title, x, y, TEXT_SECONDARY, false);
        if (!plan.isAvailable()) {
            guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.techstation.unavailable"), x, y + 12, WARNING, false);
            return;
        }

        guiGraphics.drawString(font, Component.literal(plan.primaryCount() + "x"), x, y + 12, ACCENT, false);
        guiGraphics.drawString(font, plan.primaryMaterial().getHoverName(), x + 18, y + 12, TEXT_PRIMARY, false);
        int outputY = y + 26;
        if (plan.requiresSecondaryMaterial()) {
            guiGraphics.drawString(font, Component.literal(plan.secondaryCount() + "x"), x, y + 24, ACCENT, false);
            guiGraphics.drawString(font, plan.secondaryMaterial().getHoverName(), x + 18, y + 24, TEXT_PRIMARY, false);
            outputY = y + 38;
        }

        ItemStack output = plan.output();
        if (!output.isEmpty()) {
            guiGraphics.drawString(font, output.getHoverName(), x, outputY, TEXT_SECONDARY, false);
        }
    }

    private void drawSlotBack(GuiGraphics guiGraphics, int x, int y) {
        int drawX = leftPos + x - 2;
        int drawY = topPos + y - 2;
        guiGraphics.fill(drawX, drawY, drawX + 20, drawY + 20, 0xFF202A34);
        guiGraphics.renderOutline(drawX, drawY, 20, 20, PANEL_EDGE);
    }
}
