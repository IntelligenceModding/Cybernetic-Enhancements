package de.artemis.cyberneticenhancements.client.screen;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareSlot;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareSlotType;
import de.artemis.cyberneticenhancements.common.menu.RipperStationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.List;
import java.util.Optional;

public final class RipperStationScreen extends AbstractContainerScreen<RipperStationMenu> {
    private static final ResourceLocation UI_ID = ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "ripper_station");
    private static final int IMAGE_WIDTH = 720;
    private static final int IMAGE_HEIGHT = 456;
    private static final int PANEL_COLOR = 0xFF11161D;
    private static final int PANEL_EDGE = 0xFF2B3C4D;
    private static final int PANEL_ACCENT = 0xFF1EE2B5;
    private static final int SLOT_BACKGROUND = 0xFF202A34;
    private static final int SLOT_ACTIVE = 0xFF2D4A4F;
    private static final int TEXT_PRIMARY = 0xFFE5F2FF;
    private static final int TEXT_SECONDARY = 0xFF9FB5C7;
    private static final int WARNING = 0xFFFF6B6B;
    private static final int[] SLOT_X = {
            194, 216, 238,
            216,
            216,
            186,
            246,
            324, 346,
            412, 434, 456,
            412, 434, 456,
            412, 434, 456,
            335
    };
    private static final int[] SLOT_Y = {
            58, 58, 58,
            88,
            116,
            170,
            170,
            88, 88,
            58, 58, 58,
            116, 116, 116,
            174, 174, 174,
            224
    };
    private static final int[] CHIP_X = {20, 42, 64};
    private static final int[] CHIP_Y = {278, 300, 322};
    private static final int[] ARM_MODULE_X = {214, 236, 258};
    private static final int[] LEG_MODULE_X = {548, 570, 592};
    private static final int ARM_MODULE_Y = 286;
    private static final int LEG_MODULE_Y = 286;
    private static final int PLAYER_INVENTORY_X = 279;
    private static final int PLAYER_INVENTORY_LABEL_Y = 352;
    private static final int CHROME_BAR_X = 18;
    private static final int CHROME_BAR_Y = 344;
    private static final int CHROME_BAR_WIDTH = 684;

    public RipperStationScreen(RipperStationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = IMAGE_WIDTH;
        this.imageHeight = IMAGE_HEIGHT;
        this.inventoryLabelX = PLAYER_INVENTORY_X;
        this.inventoryLabelY = PLAYER_INVENTORY_LABEL_Y;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x0 = leftPos;
        int y0 = topPos;
        guiGraphics.fill(x0, y0, x0 + imageWidth, y0 + imageHeight, PANEL_EDGE);
        guiGraphics.fill(x0 + 1, y0 + 1, x0 + imageWidth - 1, y0 + imageHeight - 1, PANEL_COLOR);

        guiGraphics.fill(x0 + 12, y0 + 12, x0 + 166, y0 + 248, 0xFF131B23);
        guiGraphics.fill(x0 + 176, y0 + 12, x0 + 496, y0 + 248, 0xFF15202A);
        guiGraphics.fill(x0 + 506, y0 + 12, x0 + 708, y0 + 248, 0xFF131B23);

        guiGraphics.fill(x0 + 12, y0 + 258, x0 + 166, y0 + 328, 0xFF121920);
        guiGraphics.fill(x0 + 176, y0 + 258, x0 + 496, y0 + 328, 0xFF121920);
        guiGraphics.fill(x0 + 506, y0 + 258, x0 + 708, y0 + 328, 0xFF121920);
        guiGraphics.fill(x0 + 12, y0 + 334, x0 + 708, y0 + 444, 0xFF0F151C);

        drawBodySilhouette(guiGraphics, x0 + 262, y0 + 74);
        drawSlotFrames(guiGraphics);
        drawChromeMeter(guiGraphics);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, TEXT_SECONDARY, false);

        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.ripper_station.body_overview"), 18, 20, TEXT_PRIMARY, false);
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.ripper_station.ripper_matrix"), 184, 20, TEXT_PRIMARY, false);
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.ripper_station.system_summary"), 514, 20, TEXT_PRIMARY, false);

        drawStatLine(guiGraphics, Component.translatable("screen.cyberneticenhancements.ripper_station.stage"), getStageLabel(), 18, 46, 154);
        drawStatLine(guiGraphics, Component.translatable("screen.cyberneticenhancements.ripper_station.chrome"), Component.literal(menu.getInstalledChrome() + " / " + menu.getChromeCapacity()), 18, 64, 154);
        drawStatLine(guiGraphics, Component.translatable("screen.cyberneticenhancements.ripper_station.cyberstrain"), Component.literal(Integer.toString(menu.getCyberstrain())), 18, 82, 154);
        drawStatLine(guiGraphics, Component.translatable("screen.cyberneticenhancements.ripper_station.thermal_load"), Component.literal(Integer.toString(menu.getEstimatedHeat())), 18, 100, 154);
        drawStatLine(guiGraphics, Component.translatable("screen.cyberneticenhancements.ripper_station.integrity"), Component.literal(menu.getIntegrity() + "%"), 18, 118, 154);
        drawStatLine(guiGraphics, Component.translatable("screen.cyberneticenhancements.ripper_station.installed_parts"), Component.literal(Integer.toString(menu.getInstalledCount())), 18, 136, 154);

        renderBodyLabels(guiGraphics);
        renderSystemSummary(guiGraphics);
        renderChipSummary(guiGraphics);
        renderModuleSummary(guiGraphics);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderSlotTooltips(guiGraphics, mouseX, mouseY);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    private void drawStatLine(GuiGraphics guiGraphics, Component label, Component value, int x, int y, int rightX) {
        guiGraphics.drawString(font, label, x, y, TEXT_SECONDARY, false);
        guiGraphics.drawString(font, value, rightX - font.width(value), y, TEXT_PRIMARY, false);
    }

    private void drawBodySilhouette(GuiGraphics guiGraphics, int x, int y) {
        guiGraphics.fill(x + 18, y, x + 34, y + 16, PANEL_ACCENT);
        guiGraphics.fill(x + 14, y + 18, x + 38, y + 56, 0xFF24465D);
        guiGraphics.fill(x + 10, y + 58, x + 42, y + 76, 0xFF20394A);
        guiGraphics.fill(x + 14, y + 78, x + 22, y + 114, 0xFF20394A);
        guiGraphics.fill(x + 30, y + 78, x + 38, y + 114, 0xFF20394A);
    }

    private void drawSlotFrames(GuiGraphics guiGraphics) {
        for (int slot = 0; slot < SLOT_X.length; slot++) {
            int x = leftPos + SLOT_X[slot] - 2;
            int y = topPos + SLOT_Y[slot] - 2;
            boolean installed = !menu.getCyberwareStack(CyberwareSlot.values()[slot]).isEmpty();
            guiGraphics.fill(x, y, x + 20, y + 20, installed ? SLOT_ACTIVE : SLOT_BACKGROUND);
            guiGraphics.renderOutline(x, y, 20, 20, installed ? PANEL_ACCENT : PANEL_EDGE);
        }

        for (int row = 0; row < CHIP_Y.length; row++) {
            for (int column = 0; column < CHIP_X.length; column++) {
                drawInsetSlot(guiGraphics, CHIP_X[column], CHIP_Y[row], menu.isChipSlotUnlocked(row, column), !menu.getChipwareStack(row, column).isEmpty());
            }
        }

        for (int slot = 0; slot < ARM_MODULE_X.length; slot++) {
            drawInsetSlot(guiGraphics, ARM_MODULE_X[slot], ARM_MODULE_Y, menu.isArmModuleSlotUnlocked(slot), !menu.getArmModuleStack(slot).isEmpty());
            drawInsetSlot(guiGraphics, LEG_MODULE_X[slot], LEG_MODULE_Y, menu.isLegModuleSlotUnlocked(slot), !menu.getLegModuleStack(slot).isEmpty());
        }
    }

    private void drawChromeMeter(GuiGraphics guiGraphics) {
        int barX = leftPos + CHROME_BAR_X;
        int barY = topPos + CHROME_BAR_Y;
        int width = CHROME_BAR_WIDTH;
        int fillWidth = Math.max(0, Math.min(width, menu.getChromePercent() * width / 100));
        int fillColor = menu.getCyberstrain() > 0 ? WARNING : PANEL_ACCENT;
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.ripper_station.chrome_profile"), barX, barY - 10, TEXT_SECONDARY, false);
        guiGraphics.fill(barX, barY, barX + width, barY + 8, SLOT_BACKGROUND);
        guiGraphics.fill(barX, barY, barX + fillWidth, barY + 8, fillColor);
        guiGraphics.renderOutline(barX, barY, width, 8, PANEL_EDGE);
    }

    private void renderBodyLabels(GuiGraphics guiGraphics) {
        guiGraphics.drawString(font, Component.translatable(CyberwareSlotType.FRONTAL_CORTEX.translationKey()), 176, 42, TEXT_SECONDARY, false);
        guiGraphics.drawString(font, Component.translatable(CyberwareSlotType.OPERATING_SYSTEM.translationKey()), 176, 78, TEXT_SECONDARY, false);
        guiGraphics.drawString(font, Component.translatable(CyberwareSlotType.FACE.translationKey()), 176, 106, TEXT_SECONDARY, false);
        guiGraphics.drawString(font, Component.translatable(CyberwareSlotType.ARMS.translationKey()), 176, 188, TEXT_SECONDARY, false);
        guiGraphics.drawString(font, Component.translatable(CyberwareSlotType.HANDS.translationKey()), 236, 188, TEXT_SECONDARY, false);
        guiGraphics.drawString(font, Component.translatable(CyberwareSlotType.SKELETON.translationKey()), 312, 78, TEXT_SECONDARY, false);
        guiGraphics.drawString(font, Component.translatable(CyberwareSlotType.LEGS.translationKey()), 324, 242, TEXT_SECONDARY, false);
    }

    private void renderSystemSummary(GuiGraphics guiGraphics) {
        int labelX = 514;
        int countRightX = 694;
        int startY = 46;
        CyberwareSlotType[] types = CyberwareSlotType.values();
        for (int i = 0; i < types.length; i++) {
            int y = startY + i * 18;
            CyberwareSlotType type = types[i];
            int installed = menu.getInstalledCount(type);
            Component label = Component.translatable(type.translationKey());
            String value = installed + " / " + type.getBaseSlotCount();
            guiGraphics.drawString(font, label, labelX, y, installed > 0 ? PANEL_ACCENT : TEXT_SECONDARY, false);
            guiGraphics.drawString(font, value, countRightX - font.width(value), y, TEXT_PRIMARY, false);
        }
    }

    private void renderChipSummary(GuiGraphics guiGraphics) {
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.ripper_station.chipware"), 18, 262, TEXT_PRIMARY, false);
        for (int row = 0; row < CHIP_Y.length; row++) {
            int labelY = CHIP_Y[row] + 4;
            guiGraphics.drawString(font, Component.literal("FC" + (row + 1)), 18, labelY, TEXT_SECONDARY, false);
            String hostName = menu.getChipwareHostName(row);
            Component host = hostName.isEmpty()
                    ? Component.translatable("screen.cyberneticenhancements.ripper_station.no_chip_socket")
                    : Component.literal(hostName);
            guiGraphics.drawString(font, host, 82, labelY, hostName.isEmpty() ? TEXT_SECONDARY : PANEL_ACCENT, false);
        }
    }

    private void renderModuleSummary(GuiGraphics guiGraphics) {
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.ripper_station.arm_modules"), 176, 262, TEXT_PRIMARY, false);
        guiGraphics.drawString(font, Component.translatable("screen.cyberneticenhancements.ripper_station.leg_modules"), 514, 262, TEXT_PRIMARY, false);

        String armHost = menu.getArmModuleHostName();
        Component armHostComponent = armHost.isEmpty()
                ? Component.translatable("screen.cyberneticenhancements.ripper_station.no_arm_cyberware")
                : Component.literal(armHost);
        guiGraphics.drawString(font, armHostComponent, 176, 274, armHost.isEmpty() ? TEXT_SECONDARY : PANEL_ACCENT, false);

        String legHost = menu.getLegModuleHostName();
        Component legHostComponent = legHost.isEmpty()
                ? Component.translatable("screen.cyberneticenhancements.ripper_station.no_leg_cyberware")
                : Component.literal(legHost);
        guiGraphics.drawString(font, legHostComponent, 514, 274, legHost.isEmpty() ? TEXT_SECONDARY : PANEL_ACCENT, false);
    }

    private void renderSlotTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        for (int i = 0; i < CyberwareSlot.values().length; i++) {
            Slot slot = menu.slots.get(i);
            if (isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY) && !slot.hasItem()) {
                guiGraphics.renderTooltip(font, List.of(
                        Component.translatable(CyberwareSlot.values()[i].displayKey()),
                        Component.translatable("screen.cyberneticenhancements.ripper_station.slot_hint")
                ), Optional.empty(), mouseX, mouseY);
                return;
            }
        }

        for (int row = 0; row < CHIP_Y.length; row++) {
            for (int column = 0; column < CHIP_X.length; column++) {
                if (isHovering(CHIP_X[column], CHIP_Y[row], 16, 16, mouseX, mouseY) && menu.getChipwareStack(row, column).isEmpty()) {
                    guiGraphics.renderTooltip(font, List.of(
                            Component.translatable("screen.cyberneticenhancements.ripper_station.chipware"),
                            Component.translatable(menu.isChipSlotUnlocked(row, column)
                                    ? "screen.cyberneticenhancements.ripper_station.chip_slot_hint"
                                    : "screen.cyberneticenhancements.ripper_station.chip_slot_locked")
                    ), Optional.empty(), mouseX, mouseY);
                    return;
                }
            }
        }

        for (int i = 0; i < ARM_MODULE_X.length; i++) {
            if (isHovering(ARM_MODULE_X[i], ARM_MODULE_Y, 16, 16, mouseX, mouseY) && menu.getArmModuleStack(i).isEmpty()) {
                guiGraphics.renderTooltip(font, List.of(
                        Component.translatable("screen.cyberneticenhancements.ripper_station.arm_modules"),
                        Component.translatable(menu.isArmModuleSlotUnlocked(i)
                                ? "screen.cyberneticenhancements.ripper_station.module_slot_hint"
                                : "screen.cyberneticenhancements.ripper_station.module_slot_locked")
                ), Optional.empty(), mouseX, mouseY);
                return;
            }

            if (isHovering(LEG_MODULE_X[i], LEG_MODULE_Y, 16, 16, mouseX, mouseY) && menu.getLegModuleStack(i).isEmpty()) {
                guiGraphics.renderTooltip(font, List.of(
                        Component.translatable("screen.cyberneticenhancements.ripper_station.leg_modules"),
                        Component.translatable(menu.isLegModuleSlotUnlocked(i)
                                ? "screen.cyberneticenhancements.ripper_station.module_slot_hint"
                                : "screen.cyberneticenhancements.ripper_station.module_slot_locked")
                ), Optional.empty(), mouseX, mouseY);
                return;
            }
        }
    }

    private Component getStageLabel() {
        return Component.translatable("screen.cyberneticenhancements.ripper_station.stage." + switch (menu.getStageIndex()) {
            case 0 -> "organic";
            case 1 -> "augmented";
            case 2 -> "cybernetic";
            case 3 -> "synthetic";
            default -> "android";
        });
    }

    private void drawInsetSlot(GuiGraphics guiGraphics, int x, int y, boolean unlocked, boolean installed) {
        int drawX = leftPos + x - 2;
        int drawY = topPos + y - 2;
        int fill = !unlocked ? 0xFF171D24 : installed ? SLOT_ACTIVE : SLOT_BACKGROUND;
        int outline = !unlocked ? 0xFF28323C : installed ? PANEL_ACCENT : PANEL_EDGE;
        guiGraphics.fill(drawX, drawY, drawX + 20, drawY + 20, fill);
        guiGraphics.renderOutline(drawX, drawY, 20, 20, outline);
    }
}
