package de.artemis.cyberneticenhancements.client.screen;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareTier;
import de.artemis.cyberneticenhancements.common.menu.AbstractBaseMenu;
import de.artemis.cyberneticenhancements.common.menu.RecyclerStationLayout;
import de.artemis.cyberneticenhancements.common.menu.RecyclerStationMenu;
import de.artemis.cyberneticenhancements.common.network.UpgradeStationInputSlotPayload;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.List;

public final class RecyclerStationScreen extends AbstractContainerScreen<RecyclerStationMenu> {
    private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(
            CyberneticEnhancements.MOD_ID,
            "textures/gui/container/recycler_station.png");
    private static final int SLOT_BG = StationScreenStyle.SLOT_BG;
    private static final int SLOT_ACTIVE = StationScreenStyle.SLOT_ACTIVE;
    private static final int TEXT_PRIMARY = StationScreenStyle.TEXT_PRIMARY;
    private static final int TEXT_SECONDARY = StationScreenStyle.TEXT_SECONDARY;
    private static final int PANEL_TEXT_PADDING_X = StationScreenStyle.PANEL_TEXT_PADDING_X;
    private static final int PANEL_TEXT_PADDING_Y = StationScreenStyle.PANEL_TEXT_PADDING_Y;
    private static final int SLOT_LABEL_Y = StationScreenStyle.SLOT_LABEL_Y;
    private static final int SUMMARY_Y = 124;
    private static final int SUMMARY_LINE_HEIGHT = 18;
    private static final int SUMMARY_MAX_LINES = 3;
    private static final long SLOT_UPGRADE_HOLD_MS = 5000L;

    private CyberwareTier heldUpgradeStartTier;
    private long heldUpgradeStartMs;
    private boolean upgradeTriggeredThisHold;

    public RecyclerStationScreen(RecyclerStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = RecyclerStationLayout.IMAGE_WIDTH;
        this.imageHeight = RecyclerStationLayout.IMAGE_HEIGHT;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x0 = leftPos;
        int y0 = topPos;

        guiGraphics.blit(BACKGROUND_TEXTURE, x0, y0, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        drawUpgradeableSlotBack(guiGraphics, RecyclerStationLayout.INPUT_X, RecyclerStationLayout.INPUT_Y, menu.getSupportedTier());
        drawResultSlotBacks(guiGraphics, menu.getDisplayedResultStacks().size());
        drawPlayerSlotBacks(guiGraphics);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.recycler_station.scrap_input"), RecyclerStationLayout.INTAKE_PANEL_X1 + PANEL_TEXT_PADDING_X, RecyclerStationLayout.INTAKE_PANEL_Y1 + PANEL_TEXT_PADDING_Y, TEXT_PRIMARY, false);
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.recycler_station.reclaimed_output"), RecyclerStationLayout.RECOVERY_PANEL_X1 + PANEL_TEXT_PADDING_X, RecyclerStationLayout.RECOVERY_PANEL_Y1 + PANEL_TEXT_PADDING_Y, TEXT_PRIMARY, false);
        drawCenteredLabel(guiGraphics, Component.translatable("screen.cyberneticenhancements.recycler_station.recycle_target"), RecyclerStationLayout.INPUT_X + StationSlotRenderer.SLOT_SIZE / 2, SLOT_LABEL_Y, TEXT_SECONDARY);
        drawCenteredLabel(guiGraphics, Component.translatable("screen.cyberneticenhancements.recycler_station.recovered_parts"), RecyclerStationLayout.RECOVERY_PANEL_X1 + (RecyclerStationLayout.RECOVERY_PANEL_X2 - RecyclerStationLayout.RECOVERY_PANEL_X1) / 2, SLOT_LABEL_Y, TEXT_SECONDARY);
        renderInputSummary(guiGraphics);
        renderRecoverySummary(guiGraphics);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        updateHeldSlotUpgrade(mouseX, mouseY);
        renderUpgradeSlotTooltip(guiGraphics, mouseX, mouseY);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && menu.getCarried().isEmpty() && isHoveringInputSlot((int) mouseX, (int) mouseY) && menu.getInputStack().isEmpty()
                && menu.canUpgradeSupportedTier() && menu.hasRequiredUpgradeComponent()) {
            heldUpgradeStartTier = menu.getSupportedTier();
            heldUpgradeStartMs = System.currentTimeMillis();
            upgradeTriggeredThisHold = false;
            return true;
        }

        clearHeldSlotUpgrade();
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean consumed = button == 0 && heldUpgradeStartTier != null;
        if (button == 0) {
            clearHeldSlotUpgrade();
        }
        return consumed || super.mouseReleased(mouseX, mouseY, button);
    }

    private void drawPlayerSlotBacks(GuiGraphics guiGraphics) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                drawSlotBack(guiGraphics, RecyclerStationLayout.PLAYER_INVENTORY_X + column * AbstractBaseMenu.SLOT_SPACING, RecyclerStationLayout.PLAYER_INVENTORY_Y + row * AbstractBaseMenu.SLOT_SPACING);
            }
        }
        for (int slot = 0; slot < 9; slot++) {
            drawSlotBack(guiGraphics, RecyclerStationLayout.PLAYER_INVENTORY_X + slot * AbstractBaseMenu.SLOT_SPACING, RecyclerStationLayout.PLAYER_HOTBAR_Y);
        }
    }

    private void drawResultSlotBacks(GuiGraphics guiGraphics, int activeSlots) {
        for (int slotIndex = 0; slotIndex < RecyclerStationLayout.RESULT_SLOT_COUNT; slotIndex++) {
            StationSlotRenderer.drawStandardSlot(
                    guiGraphics,
                    leftPos,
                    topPos,
                    RecyclerStationLayout.RESULT_SLOT_X[slotIndex],
                    RecyclerStationLayout.RESULT_SLOT_Y[slotIndex],
                    slotIndex < activeSlots ? SLOT_ACTIVE : SLOT_BG
            );
        }
    }

    private void drawSlotBack(GuiGraphics guiGraphics, int x, int y) {
        StationSlotRenderer.drawStandardSlot(guiGraphics, leftPos, topPos, x, y, SLOT_BG);
    }

    private void renderInputSummary(GuiGraphics guiGraphics) {
        int x = RecyclerStationLayout.INTAKE_PANEL_X1 + PANEL_TEXT_PADDING_X;
        int maxWidth = RecyclerStationLayout.INTAKE_PANEL_X2 - RecyclerStationLayout.INTAKE_PANEL_X1 - PANEL_TEXT_PADDING_X * 2;
        ItemStack input = menu.getInputStack();

        if (input.isEmpty()) {
            drawWrappedText(guiGraphics, Component.translatable("screen.cyberneticenhancements.recycler_station.insert_recycle_target"), x, SUMMARY_Y, maxWidth, TEXT_SECONDARY, SUMMARY_MAX_LINES);
            return;
        }

        drawTrimmedText(guiGraphics, input.getHoverName(), x, SUMMARY_Y, maxWidth, TEXT_PRIMARY);
        drawWrappedText(
                guiGraphics,
                Component.translatable(
                        "screen.cyberneticenhancements.ripper_station.slot_supported_tier",
                        Component.translatable(menu.getSupportedTier().translationKey()).withStyle(menu.getSupportedTier().getColor())
                ),
                x,
                SUMMARY_Y + SUMMARY_LINE_HEIGHT,
                maxWidth,
                TEXT_SECONDARY,
                1
        );
    }

    private void renderRecoverySummary(GuiGraphics guiGraphics) {
        int x = RecyclerStationLayout.RECOVERY_PANEL_X1 + PANEL_TEXT_PADDING_X;
        int maxWidth = RecyclerStationLayout.RECOVERY_PANEL_X2 - RecyclerStationLayout.RECOVERY_PANEL_X1 - PANEL_TEXT_PADDING_X * 2;
        List<ItemStack> outputs = menu.getDisplayedResultStacks();

        if (outputs.isEmpty()) {
            drawWrappedText(guiGraphics, Component.translatable("screen.cyberneticenhancements.recycler_station.recovered_parts_hint"), x, SUMMARY_Y, maxWidth, TEXT_SECONDARY, SUMMARY_MAX_LINES);
            return;
        }

        for (int index = 0; index < Math.min(outputs.size(), SUMMARY_MAX_LINES); index++) {
            ItemStack stack = outputs.get(index);
            drawTrimmedText(
                    guiGraphics,
                    Component.literal(stack.getCount() + "x ").append(stack.getHoverName()),
                    x,
                    SUMMARY_Y + SUMMARY_LINE_HEIGHT * index,
                    maxWidth,
                    TEXT_PRIMARY
            );
        }
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

    private void drawWrappedText(GuiGraphics guiGraphics, Component text, int x, int y, int maxWidth, int color, int maxLines) {
        List<FormattedCharSequence> lines = font.split(text, maxWidth);
        int lineCount = Math.min(lines.size(), maxLines);
        for (int index = 0; index < lineCount; index++) {
            guiGraphics.drawString(font, lines.get(index), x, y + index * SUMMARY_LINE_HEIGHT, color, false);
        }
    }

    private void drawUpgradeableSlotBack(GuiGraphics guiGraphics, int x, int y, CyberwareTier tier) {
        StationSlotRenderer.drawUpgradeableSlot(guiGraphics, leftPos, topPos, x, y, SLOT_BG, StationSlotRenderer.getTierFrameColor(tier));
    }

    private void renderUpgradeSlotTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (!isHoveringInputSlot(mouseX, mouseY) || !menu.getInputStack().isEmpty()) {
            return;
        }
        StationUpgradeTooltipBuilder.Tooltip tooltip = buildUpgradeTooltip();
        if (!tooltip.showProgressBar()) {
            guiGraphics.renderTooltip(font, tooltip.lines(), java.util.Optional.empty(), mouseX, mouseY);
            return;
        }
        StationUpgradeTooltipRenderer.renderTooltip(guiGraphics, font, width, height, tooltip.lines(), getHeldUpgradeProgress(), mouseX, mouseY);
    }

    private StationUpgradeTooltipBuilder.Tooltip buildUpgradeTooltip() {
        return StationUpgradeTooltipBuilder.build(
                Component.translatable("screen.cyberneticenhancements.recycler_station.scrap_input"),
                menu.getSupportedTier(),
                menu.getNextSupportedTier(),
                menu.getRequiredUpgradeComponentStack(),
                menu.canUpgradeSupportedTier(),
                menu.hasRequiredUpgradeComponent(),
                "screen.cyberneticenhancements.recycler_station.input_slot_hint"
        );
    }

    private float getHeldUpgradeProgress() {
        if (heldUpgradeStartTier == null) {
            return 0.0F;
        }
        if (upgradeTriggeredThisHold) {
            return 1.0F;
        }
        long elapsedMs = System.currentTimeMillis() - heldUpgradeStartMs;
        return Math.max(0.0F, Math.min(1.0F, (float) elapsedMs / (float) SLOT_UPGRADE_HOLD_MS));
    }

    private void updateHeldSlotUpgrade(int mouseX, int mouseY) {
        if (heldUpgradeStartTier == null) {
            return;
        }
        if (!isHeldUpgradeStillValid(mouseX, mouseY)) {
            clearHeldSlotUpgrade();
            return;
        }
        if (menu.getSupportedTier() != heldUpgradeStartTier) {
            clearHeldSlotUpgrade();
            return;
        }
        if (!upgradeTriggeredThisHold && System.currentTimeMillis() - heldUpgradeStartMs >= SLOT_UPGRADE_HOLD_MS) {
            PacketDistributor.sendToServer(new UpgradeStationInputSlotPayload(0));
            upgradeTriggeredThisHold = true;
        }
    }

    private boolean isHeldUpgradeStillValid(int mouseX, int mouseY) {
        return isHoveringInputSlot(mouseX, mouseY)
                && menu.getInputStack().isEmpty()
                && menu.canUpgradeSupportedTier()
                && menu.hasRequiredUpgradeComponent();
    }

    private void clearHeldSlotUpgrade() {
        heldUpgradeStartTier = null;
        heldUpgradeStartMs = 0L;
        upgradeTriggeredThisHold = false;
    }

    private boolean isHoveringInputSlot(int mouseX, int mouseY) {
        return isHovering(RecyclerStationLayout.INPUT_X, RecyclerStationLayout.INPUT_Y, StationSlotRenderer.SLOT_SIZE, StationSlotRenderer.SLOT_SIZE, mouseX, mouseY);
    }
}
