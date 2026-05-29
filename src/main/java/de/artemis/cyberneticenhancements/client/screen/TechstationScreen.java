package de.artemis.cyberneticenhancements.client.screen;

import de.artemis.cyberneticenhancements.common.cyberware.CyberwareServicePlan;
import de.artemis.cyberneticenhancements.common.menu.TechstationLayout;
import de.artemis.cyberneticenhancements.common.menu.TechstationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public final class TechstationScreen extends AbstractContainerScreen<TechstationMenu> {
    private static final int PANEL_BG = 0xFF10161D;
    private static final int PANEL_EDGE = 0xFF2B3C4D;
    private static final int PANEL_ALT = 0xFF151E27;
    private static final int PANEL_DEEP = 0xFF0D1319;
    private static final int SLOT_BG = 0xFF202A34;
    private static final int TEXT_PRIMARY = 0xFFE5F2FF;
    private static final int TEXT_SECONDARY = 0xFF9FB5C7;
    private static final int ACCENT = 0xFF1EE2B5;
    private static final int WARNING = 0xFFFF6B6B;
    private static final int PANEL_TEXT_PADDING_X = 8;
    private static final int PANEL_TEXT_PADDING_Y = 8;
    private static final int SLOT_LABEL_Y = 38;
    private static final int SUMMARY_Y = 118;
    private static final int SUMMARY_LINE_HEIGHT = 14;
    private static final int INVENTORY_LABEL_Y = 177;

    public TechstationScreen(TechstationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = TechstationLayout.IMAGE_WIDTH;
        this.imageHeight = TechstationLayout.IMAGE_HEIGHT;
        this.inventoryLabelX = TechstationLayout.PLAYER_INVENTORY_X;
        this.inventoryLabelY = TechstationLayout.PLAYER_INVENTORY_Y - 12;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x0 = leftPos;
        int y0 = topPos;
        guiGraphics.fill(x0, y0, x0 + imageWidth, y0 + imageHeight, PANEL_EDGE);
        guiGraphics.fill(x0 + 1, y0 + 1, x0 + imageWidth - 1, y0 + imageHeight - 1, PANEL_BG);

        fillPanel(guiGraphics, TechstationLayout.REPAIR_PANEL_X1, TechstationLayout.REPAIR_PANEL_Y1, TechstationLayout.REPAIR_PANEL_X2, TechstationLayout.REPAIR_PANEL_Y2, PANEL_ALT);
        fillPanel(guiGraphics, TechstationLayout.UPGRADE_PANEL_X1, TechstationLayout.UPGRADE_PANEL_Y1, TechstationLayout.UPGRADE_PANEL_X2, TechstationLayout.UPGRADE_PANEL_Y2, PANEL_ALT);
        fillPanel(guiGraphics, TechstationLayout.INVENTORY_PANEL_X1, TechstationLayout.INVENTORY_PANEL_Y1, TechstationLayout.INVENTORY_PANEL_X2, TechstationLayout.INVENTORY_PANEL_Y2, PANEL_DEEP);

        drawSlotBack(guiGraphics, TechstationLayout.REPAIR_INPUT_X, TechstationLayout.REPAIR_INPUT_Y);
        drawSlotBack(guiGraphics, TechstationLayout.REPAIR_MATERIAL_X, TechstationLayout.REPAIR_MATERIAL_Y);
        drawSlotBack(guiGraphics, TechstationLayout.REPAIR_RESULT_X, TechstationLayout.REPAIR_RESULT_Y);
        drawSlotBack(guiGraphics, TechstationLayout.UPGRADE_INPUT_X, TechstationLayout.UPGRADE_INPUT_Y);
        drawSlotBack(guiGraphics, TechstationLayout.UPGRADE_PRIMARY_X, TechstationLayout.UPGRADE_PRIMARY_Y);
        drawSlotBack(guiGraphics, TechstationLayout.UPGRADE_SECONDARY_X, TechstationLayout.UPGRADE_SECONDARY_Y);
        drawSlotBack(guiGraphics, TechstationLayout.UPGRADE_RESULT_X, TechstationLayout.UPGRADE_RESULT_Y);
        drawPlayerSlotBacks(guiGraphics);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.techstation.servicing_bay"), TechstationLayout.REPAIR_PANEL_X1 + PANEL_TEXT_PADDING_X, TechstationLayout.REPAIR_PANEL_Y1 + PANEL_TEXT_PADDING_Y, TEXT_PRIMARY, false);
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.techstation.upgrade_bay"), TechstationLayout.UPGRADE_PANEL_X1 + PANEL_TEXT_PADDING_X, TechstationLayout.UPGRADE_PANEL_Y1 + PANEL_TEXT_PADDING_Y, TEXT_PRIMARY, false);
        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT_SECONDARY, false);

        drawCenteredLabel(guiGraphics, Component.translatable("screen.cyberneticenhancements.techstation.input"), TechstationLayout.REPAIR_INPUT_X + 8, SLOT_LABEL_Y, TEXT_SECONDARY);
        drawCenteredLabel(guiGraphics, Component.translatable("screen.cyberneticenhancements.techstation.materials"), TechstationLayout.REPAIR_MATERIAL_X + 8, SLOT_LABEL_Y, TEXT_SECONDARY);
        drawCenteredLabel(guiGraphics, Component.translatable("screen.cyberneticenhancements.techstation.output"), TechstationLayout.REPAIR_RESULT_X + 8, SLOT_LABEL_Y, TEXT_SECONDARY);

        drawCenteredLabel(guiGraphics, Component.translatable("screen.cyberneticenhancements.techstation.input"), TechstationLayout.UPGRADE_INPUT_X + 8, SLOT_LABEL_Y, TEXT_SECONDARY);
        drawCenteredLabel(guiGraphics, Component.translatable("screen.cyberneticenhancements.techstation.upgrade_parts"), TechstationLayout.UPGRADE_PRIMARY_X + 8, SLOT_LABEL_Y, TEXT_SECONDARY);
        drawCenteredLabel(guiGraphics, Component.translatable("screen.cyberneticenhancements.techstation.output"), TechstationLayout.UPGRADE_RESULT_X + 8, SLOT_LABEL_Y, TEXT_SECONDARY);

        renderRepairSummary(guiGraphics);
        renderUpgradeSummary(guiGraphics);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void renderRepairSummary(GuiGraphics guiGraphics) {
        int x = TechstationLayout.REPAIR_PANEL_X1 + PANEL_TEXT_PADDING_X;
        int maxWidth = TechstationLayout.REPAIR_PANEL_X2 - TechstationLayout.REPAIR_PANEL_X1 - PANEL_TEXT_PADDING_X * 2;
        ItemStack input = menu.getRepairInputStack();
        CyberwareServicePlan plan = menu.getRepairPlan();
        CyberwareServicePlan resolvedPlan = menu.getRepairResolvedPlan();

        if (input.isEmpty()) {
            drawTrimmedText(guiGraphics, Component.translatable("screen.cyberneticenhancements.techstation.insert_repair_target"), x, SUMMARY_Y, maxWidth, TEXT_SECONDARY);
            return;
        }

        drawTrimmedText(guiGraphics, input.getHoverName(), x, SUMMARY_Y, maxWidth, TEXT_PRIMARY);
        if (!plan.isAvailable()) {
            drawTrimmedText(guiGraphics, Component.translatable("screen.cyberneticenhancements.techstation.repair_unavailable"), x, SUMMARY_Y + SUMMARY_LINE_HEIGHT, maxWidth, WARNING);
            return;
        }

        drawPlanMaterial(guiGraphics, plan.primaryCount(), plan.primaryMaterial(), x, SUMMARY_Y + SUMMARY_LINE_HEIGHT, maxWidth, hasMatchingCount(menu.getRepairMaterialStack(), plan.primaryMaterial(), plan.primaryCount()));
        drawTrimmedText(guiGraphics, plan.output().getHoverName(), x, SUMMARY_Y + SUMMARY_LINE_HEIGHT * 2, maxWidth, resolvedPlan.isAvailable() ? ACCENT : TEXT_SECONDARY);
    }

    private void renderUpgradeSummary(GuiGraphics guiGraphics) {
        int x = TechstationLayout.UPGRADE_PANEL_X1 + PANEL_TEXT_PADDING_X;
        int maxWidth = TechstationLayout.UPGRADE_PANEL_X2 - TechstationLayout.UPGRADE_PANEL_X1 - PANEL_TEXT_PADDING_X * 2;
        ItemStack input = menu.getUpgradeInputStack();
        CyberwareServicePlan plan = menu.getUpgradePlan();

        if (input.isEmpty()) {
            drawTrimmedText(guiGraphics, Component.translatable("screen.cyberneticenhancements.techstation.insert_upgrade_target"), x, SUMMARY_Y, maxWidth, TEXT_SECONDARY);
            return;
        }

        drawTrimmedText(guiGraphics, input.getHoverName(), x, SUMMARY_Y, maxWidth, TEXT_PRIMARY);
        if (!plan.isAvailable()) {
            drawTrimmedText(guiGraphics, Component.translatable("screen.cyberneticenhancements.techstation.upgrade_unavailable"), x, SUMMARY_Y + SUMMARY_LINE_HEIGHT, maxWidth, WARNING);
            return;
        }

        drawPlanMaterial(guiGraphics, plan.primaryCount(), plan.primaryMaterial(), x, SUMMARY_Y + SUMMARY_LINE_HEIGHT, maxWidth, hasMatchingCount(menu.getUpgradePrimaryMaterialStack(), plan.primaryMaterial(), plan.primaryCount()) || hasMatchingCount(menu.getUpgradeSecondaryMaterialStack(), plan.primaryMaterial(), plan.primaryCount()));
        if (plan.requiresSecondaryMaterial()) {
            drawPlanMaterial(guiGraphics, plan.secondaryCount(), plan.secondaryMaterial(), x, SUMMARY_Y + SUMMARY_LINE_HEIGHT * 2, maxWidth, hasMatchingCount(menu.getUpgradePrimaryMaterialStack(), plan.secondaryMaterial(), plan.secondaryCount()) || hasMatchingCount(menu.getUpgradeSecondaryMaterialStack(), plan.secondaryMaterial(), plan.secondaryCount()));
        } else {
            drawTrimmedText(guiGraphics, plan.output().getHoverName(), x, SUMMARY_Y + SUMMARY_LINE_HEIGHT * 2, maxWidth, menu.getUpgradeResolvedPlan().isAvailable() ? ACCENT : TEXT_SECONDARY);
        }
    }

    private boolean hasMatchingCount(ItemStack candidate, ItemStack expected, int requiredCount) {
        return !candidate.isEmpty()
                && !expected.isEmpty()
                && ItemStack.isSameItemSameComponents(candidate.copyWithCount(1), expected.copyWithCount(1))
                && candidate.getCount() >= requiredCount;
    }

    private void drawPlanMaterial(GuiGraphics guiGraphics, int count, ItemStack stack, int x, int y, int maxWidth, boolean ready) {
        Component amount = Component.literal(count + "x ");
        guiGraphics.drawString(font, amount, x, y, ready ? ACCENT : WARNING, false);
        int materialX = x + font.width(amount);
        drawTrimmedText(guiGraphics, stack.getHoverName(), materialX, y, Math.max(0, maxWidth - font.width(amount)), ready ? TEXT_PRIMARY : TEXT_SECONDARY);
    }

    private void fillPanel(GuiGraphics guiGraphics, int x1, int y1, int x2, int y2, int fillColor) {
        guiGraphics.fill(leftPos + x1, topPos + y1, leftPos + x2, topPos + y2, PANEL_EDGE);
        guiGraphics.fill(leftPos + x1 + 1, topPos + y1 + 1, leftPos + x2 - 1, topPos + y2 - 1, fillColor);
    }

    private void drawCenteredLabel(GuiGraphics guiGraphics, Component label, int centerX, int y, int color) {
        guiGraphics.drawString(font, label, centerX - font.width(label) / 2, y, color, false);
    }

    private void drawTrimmedText(GuiGraphics guiGraphics, Component text, int x, int y, int maxWidth, int color) {
        String raw = text.getString();
        String trimmed = raw;
        if (font.width(raw) > maxWidth) {
            trimmed = font.plainSubstrByWidth(raw, Math.max(0, maxWidth - font.width("..."))) + "...";
        }
        guiGraphics.drawString(font, trimmed, x, y, color, false);
    }

    private void drawPlayerSlotBacks(GuiGraphics guiGraphics) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                drawSlotBack(guiGraphics, TechstationLayout.PLAYER_INVENTORY_X + column * 18, TechstationLayout.PLAYER_INVENTORY_Y + row * 18);
            }
        }
        for (int slot = 0; slot < 9; slot++) {
            drawSlotBack(guiGraphics, TechstationLayout.PLAYER_INVENTORY_X + slot * 18, TechstationLayout.PLAYER_HOTBAR_Y);
        }
    }

    private void drawSlotBack(GuiGraphics guiGraphics, int x, int y) {
        StationSlotRenderer.drawStandardSlot(guiGraphics, leftPos, topPos, x, y, SLOT_BG);
    }
}
