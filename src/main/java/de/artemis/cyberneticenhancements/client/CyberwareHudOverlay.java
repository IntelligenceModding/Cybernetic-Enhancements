package de.artemis.cyberneticenhancements.client;

import com.mojang.blaze3d.systems.RenderSystem;
import de.artemis.cyberneticenhancements.client.screen.StationScreenStyle;
import de.artemis.cyberneticenhancements.client.tooltip.ModTooltipStyle;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareEffect;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareEffectType;
import de.artemis.cyberneticenhancements.common.network.CyberwareHudPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

final class CyberwareHudOverlay {
    private static final int SIDE_MARGIN = 6;
    private static final int BOTTOM_MARGIN = 6;
    private static final int CARD_GAP = 4;
    private static final int CARD_PADDING = 4;
    private static final int CARD_HEADER_HEIGHT = 15;
    private static final int ROW_HEIGHT = 13;
    private static final int ROW_GAP = 2;
    private static final int BAR_HEIGHT = 3;
    private static final int BAR_TEXT_GAP = 1;

    private static final int LEFT_CARD_MIN_WIDTH = 112;
    private static final int RIGHT_CARD_MIN_WIDTH = 96;
    private static final int RIGHT_ABILITY_MIN_WIDTH = 120;
    private static final int RIGHT_BAND_GAP = 6;

    private static final int CARD_BACKGROUND = StationScreenStyle.PANEL_BG;
    private static final int CARD_HEADER_BACKGROUND = StationScreenStyle.PANEL_ALT;
    private static final int CARD_BODY_BACKGROUND = StationScreenStyle.PANEL_DEEP;
    private static final int CARD_FRAME_HIGHLIGHT = StationScreenStyle.FRAME_HIGHLIGHT;
    private static final int CARD_FRAME_HIGHLIGHT_SOFT = StationScreenStyle.FRAME_HIGHLIGHT_SOFT;
    private static final int CARD_FRAME_SHADOW_SOFT = StationScreenStyle.FRAME_SHADOW_SOFT;
    private static final int CARD_FRAME_SHADOW = StationScreenStyle.FRAME_SHADOW;
    private static final int CARD_HEADER_ACCENT = 0xCC1EE2B5;

    private static final int TEXT_PRIMARY = StationScreenStyle.TEXT_PRIMARY;
    private static final int TEXT_SECONDARY = StationScreenStyle.TEXT_SECONDARY;

    private static final int BAR_BACKGROUND = StationScreenStyle.PANEL_DEEP;
    private static final int BAR_IDLE = StationScreenStyle.SECTION_HIGHLIGHT;
    private static final int BAR_CHROME = 0xFF1EE2B5;
    private static final int BAR_CHROME_IDLE = StationScreenStyle.SECTION_SHADOW;
    private static final int BAR_STRAIN_LOW = 0xFF4E9FFF;
    private static final int BAR_STRAIN_MEDIUM = 0xFFFFAE42;
    private static final int BAR_STRAIN_HIGH = 0xFFFF5C5C;
    private static final int BAR_ABILITY_ACTIVE = 0xFF54E7FF;
    private static final int BAR_ABILITY_READY = 0xFF38D9B2;
    private static final int BAR_ABILITY_COOLDOWN = 0xFF3E718C;
    private static final int BAR_STATUS_MEDICAL = 0xFF5AE58A;
    private static final int BAR_STATUS_NEURAL = 0xFF49C8FF;
    private static final int BAR_STATUS_SUPPRESSANT = 0xFF8CB3FF;
    private static final int BAR_STATUS_STREET = 0xFFFF6B5E;
    private static final int BAR_STATUS_SYSTEM = 0xFFF3D45A;
    private static final int BAR_STATUS_BIOMONITOR = 0xFF64F0A4;
    private static final int BAR_STATUS_BLOOD_PUMP = 0xFFFF4F4F;
    private static final int BAR_STATUS_SECOND_HEART = 0xFFFF7CA8;
    private static final int BAR_STATUS_REFLEX = 0xFFF6D55F;
    private CyberwareHudOverlay() {
    }

    static void render(GuiGraphics guiGraphics) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.options.hideGui) {
            return;
        }

        CyberwareHudPayload snapshot = CyberwareHudClientState.get();
        if (snapshot == null) {
            return;
        }

        Font font = minecraft.font;
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        Vec2 mouse = scaledMousePosition(minecraft, width, height);
        int mouseX = (int) mouse.x;
        int mouseY = (int) mouse.y;
        int hotbarLeft = width / 2 - 91;
        int hotbarRight = width / 2 + 91;

        int leftBandWidth = Math.max(LEFT_CARD_MIN_WIDTH, hotbarLeft - RIGHT_BAND_GAP - SIDE_MARGIN);
        int leftCardWidth = leftBandWidth;
        int rightBandX = hotbarRight + RIGHT_BAND_GAP;
        int rightBandWidth = Math.max(56, width - SIDE_MARGIN - rightBandX);

        List<HoverRegion> hoverRegions = new ArrayList<>();

        CardLayout systemCard = buildSystemCard(snapshot, leftCardWidth);
        int leftCardX = SIDE_MARGIN;
        int leftCardY = height - BOTTOM_MARGIN - systemCard.height();
        renderCard(guiGraphics, font, leftCardX, leftCardY, systemCard, hoverRegions);

        int rightCardCount = countVisibleRightCards(snapshot);
        if (rightCardCount > 0) {
            int rowCardWidth = Math.max(1, (rightBandWidth - CARD_GAP * (rightCardCount - 1)) / rightCardCount);
            List<CardLayout> rightCards = buildBottomAnchoredRightCards(snapshot, rowCardWidth);
            int rowWidth = rowCardWidth * rightCards.size() + CARD_GAP * Math.max(0, rightCards.size() - 1);
            int rowX = width - SIDE_MARGIN - rowWidth;
            int rowBottomY = height - BOTTOM_MARGIN;

            for (int index = 0; index < rightCards.size(); index++) {
                CardLayout card = rightCards.get(index);
                int cardX = rowX + index * (rowCardWidth + CARD_GAP);
                int cardY = rowBottomY - card.height();
                renderCard(guiGraphics, font, cardX, cardY, card, hoverRegions);
            }
        }

        for (HoverRegion hoverRegion : hoverRegions) {
            if (hoverRegion.contains(mouseX, mouseY)) {
                renderHoverTooltip(guiGraphics, font, width, height, hoverRegion.tooltip(), mouseX, mouseY);
                break;
            }
        }
    }

    private static CardLayout buildSystemCard(CyberwareHudPayload snapshot, int width) {
        List<RowData> rows = new ArrayList<>(2);
        int chromePercent = snapshot.chromeCapacity() <= 0 ? 0 : Math.round(snapshot.installedChrome() * 100.0F / snapshot.chromeCapacity());
        rows.add(new RowData(
                Component.translatable("hud.cyberneticenhancements.chrome"),
                Component.literal(snapshot.installedChrome() + " / " + snapshot.chromeCapacity()),
                safeRatio(snapshot.installedChrome(), snapshot.chromeCapacity()),
                BAR_CHROME,
                BAR_CHROME_IDLE,
                false,
                List.of(
                        Component.translatable("hud.cyberneticenhancements.chrome"),
                        Component.literal("Installed: " + snapshot.installedChrome() + " / " + snapshot.chromeCapacity()),
                        Component.literal("Load: " + chromePercent + "%")
                )
        ));

        List<Component> strainTooltip = new ArrayList<>();
        strainTooltip.add(Component.translatable("hud.cyberneticenhancements.cyberstrain"));
        strainTooltip.add(Component.literal("State: ").append(Component.translatable(snapshot.psychosisStateKey())));
        strainTooltip.add(Component.literal("Effective: " + snapshot.effectiveCyberstrain() + " / 50"));
        strainTooltip.add(Component.literal("Raw: " + snapshot.cyberstrain() + " / 50"));
        if (snapshot.suppressionSeconds() > 0) {
            strainTooltip.add(Component.literal("Suppression: " + snapshot.suppressionAmount() + " for " + formatSeconds(snapshot.suppressionSeconds())));
        }
        if (snapshot.psychosisSeconds() > 0) {
            strainTooltip.add(Component.literal("Episode risk: " + formatSeconds(snapshot.psychosisSeconds())));
        }
        rows.add(new RowData(
                Component.translatable("hud.cyberneticenhancements.cyberstrain"),
                Component.translatable(snapshot.psychosisStateKey()),
                Math.min(1.0F, snapshot.effectiveCyberstrain() / 50.0F),
                strainColor(snapshot.effectiveCyberstrain()),
                BAR_BACKGROUND,
                snapshot.psychosisSeconds() > 0 || snapshot.effectiveCyberstrain() >= 35,
                strainTooltip
        ));

        return new CardLayout(Component.literal("System"), width, rows);
    }

    private static int countVisibleRightCards(CyberwareHudPayload snapshot) {
        int count = 0;
        if (!snapshot.abilityKey().isEmpty()) {
            count++;
        }
        if (!snapshot.statuses().isEmpty()) {
            count++;
        }
        for (CyberwareHudPayload.CooldownEntry entry : snapshot.cooldowns()) {
            if (entry.remainingSeconds() > 0) {
                count++;
                break;
            }
        }
        return count;
    }

    private static List<CardLayout> buildBottomAnchoredRightCards(CyberwareHudPayload snapshot, int width) {
        List<CardLayout> cards = new ArrayList<>(3);
        CardLayout abilityCard = buildAbilityCard(snapshot, width);
        if (abilityCard != null) {
            cards.add(abilityCard);
        }

        CardLayout statusCard = buildStatusCard(snapshot, width);
        if (statusCard != null) {
            cards.add(statusCard);
        }

        CardLayout cooldownCard = buildCooldownCard(snapshot, width);
        if (cooldownCard != null) {
            cards.add(cooldownCard);
        }
        return cards;
    }

    private static CardLayout buildAbilityCard(CyberwareHudPayload snapshot, int width) {
        if (snapshot.abilityKey().isEmpty()) {
            return null;
        }

        List<Component> tooltip = new ArrayList<>();
        Component abilityName = Component.translatable(snapshot.abilityKey());
        tooltip.add(abilityName);

        Component detail;
        float ratio;
        int color;
        if (snapshot.abilityActiveSeconds() > 0) {
            detail = Component.literal("Active " + formatSeconds(snapshot.abilityActiveSeconds()));
            ratio = safeRatio(snapshot.abilityActiveSeconds(), snapshot.abilityActiveTotalSeconds());
            color = BAR_ABILITY_ACTIVE;
            tooltip.add(Component.literal("Active: " + formatSeconds(snapshot.abilityActiveSeconds()) + " / " + formatSeconds(snapshot.abilityActiveTotalSeconds())));
        } else if (snapshot.abilityCooldownSeconds() > 0) {
            detail = Component.literal("Cooldown " + formatSeconds(snapshot.abilityCooldownSeconds()));
            ratio = safeRatio(snapshot.abilityCooldownSeconds(), snapshot.abilityCooldownTotalSeconds());
            color = BAR_ABILITY_COOLDOWN;
            tooltip.add(Component.literal("Cooldown: " + formatSeconds(snapshot.abilityCooldownSeconds()) + " / " + formatSeconds(snapshot.abilityCooldownTotalSeconds())));
        } else {
            detail = Component.translatable("hud.cyberneticenhancements.ready");
            ratio = 1.0F;
            color = BAR_ABILITY_READY;
            tooltip.add(Component.translatable("hud.cyberneticenhancements.ready"));
        }

        return new CardLayout(
                Component.literal("Ability"),
                width,
                List.of(new RowData(abilityName, detail, ratio, color, BAR_IDLE, false, tooltip))
        );
    }

    private static CardLayout buildStatusCard(CyberwareHudPayload snapshot, int width) {
        if (snapshot.statuses().isEmpty()) {
            return null;
        }

        List<RowData> rows = new ArrayList<>(snapshot.statuses().size());
        for (CyberwareHudPayload.StatusEntry entry : snapshot.statuses()) {
            Component label = statusLabel(entry.translationKey());
            rows.add(new RowData(
                    label,
                    Component.literal(formatSeconds(entry.remainingSeconds())),
                    safeRatio(entry.remainingSeconds(), entry.totalSeconds()),
                    statusColor(entry.translationKey()),
                    BAR_BACKGROUND,
                    isWarningStatus(entry.translationKey()),
                    buildStatusTooltip(entry, label)
            ));
        }

        return new CardLayout(Component.literal("Status"), width, rows);
    }

    private static CardLayout buildCooldownCard(CyberwareHudPayload snapshot, int width) {
        List<RowData> rows = new ArrayList<>();
        for (CyberwareHudPayload.CooldownEntry entry : snapshot.cooldowns()) {
            if (entry.remainingSeconds() <= 0) {
                continue;
            }

            Component label = cooldownLabel(entry.translationKey());
            rows.add(new RowData(
                    label,
                    Component.literal(formatSeconds(entry.remainingSeconds())),
                    safeRatio(entry.remainingSeconds(), entry.totalSeconds()),
                    cooldownColor(entry.translationKey()),
                    BAR_IDLE,
                    false,
                    List.of(
                            label.copy(),
                            Component.literal("Cooldown: " + formatSeconds(entry.remainingSeconds()) + " / " + formatSeconds(entry.totalSeconds()))
                    )
            ));
        }

        if (rows.isEmpty()) {
            return null;
        }
        return new CardLayout(Component.literal("Cooldowns"), width, rows);
    }

    private static List<Component> buildStatusTooltip(CyberwareHudPayload.StatusEntry entry, Component label) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(label.copy());
        tooltip.add(describeStatus(entry));
        tooltip.add(Component.literal("Duration: " + formatSeconds(entry.remainingSeconds()) + " / " + formatSeconds(entry.totalSeconds())));
        return tooltip;
    }

    private static Component describeStatus(CyberwareHudPayload.StatusEntry entry) {
        if ("hud.cyberneticenhancements.suppression".equals(entry.translationKey())) {
            return Component.literal("Amount: " + formatNumber(entry.amount()));
        }
        if ("hud.cyberneticenhancements.ram_jolt".equals(entry.translationKey())) {
            return Component.literal("Cooldown multiplier: x" + formatNumber(entry.amount()));
        }

        CyberwareEffectType effectType = effectTypeFromTranslationKey(entry.translationKey());
        if (effectType != null) {
            return new CyberwareEffect(effectType, entry.amount()).describe();
        }
        return Component.empty();
    }

    private static CyberwareEffectType effectTypeFromTranslationKey(String translationKey) {
        for (CyberwareEffectType effectType : CyberwareEffectType.values()) {
            if (effectType.translationKey().equals(translationKey)) {
                return effectType;
            }
        }
        return null;
    }

    private static Component statusLabel(String translationKey) {
        if ("hud.cyberneticenhancements.suppression".equals(translationKey)) {
            return Component.literal("Suppression");
        }
        if ("hud.cyberneticenhancements.ram_jolt".equals(translationKey)) {
            return Component.literal("RAM Jolt");
        }
        return humanizedLabel(translationKey);
    }

    private static Component cooldownLabel(String translationKey) {
        if (translationKey.startsWith("tooltip.cyberneticenhancements.consumable_category.")
                || translationKey.startsWith("item.cyberneticenhancements.")) {
            return Component.translatable(translationKey);
        }
        return humanizedLabel(translationKey);
    }

    private static Component humanizedLabel(String translationKey) {
        int lastDot = translationKey.lastIndexOf('.');
        String suffix = lastDot >= 0 ? translationKey.substring(lastDot + 1) : translationKey;
        StringBuilder builder = new StringBuilder();
        boolean capitalize = true;
        for (int index = 0; index < suffix.length(); index++) {
            char character = suffix.charAt(index);
            if (character == '_') {
                builder.append(' ');
                capitalize = true;
                continue;
            }
            if (capitalize) {
                builder.append(Character.toUpperCase(character));
                capitalize = false;
            } else {
                builder.append(character);
            }
        }
        return Component.literal(builder.toString());
    }

    private static void renderCard(
        GuiGraphics guiGraphics,
        Font font,
        int x,
        int y,
        CardLayout card,
        List<HoverRegion> hoverRegions
    ) {
        drawCardFrame(guiGraphics, x, y, card.width(), card.height());
        Component title = trimComponent(font, card.title(), card.width() - CARD_PADDING * 2);
        guiGraphics.drawString(font, title, x + CARD_PADDING, y + 5, TEXT_PRIMARY, false);

        int rowY = y + CARD_HEADER_HEIGHT + CARD_PADDING;
        for (RowData row : card.rows()) {
            renderRow(guiGraphics, font, x + CARD_PADDING, rowY, card.width() - CARD_PADDING * 2, row);
            hoverRegions.add(new HoverRegion(x + CARD_PADDING, rowY, x + card.width() - CARD_PADDING, rowY + ROW_HEIGHT, row.tooltip()));
            rowY += ROW_HEIGHT + ROW_GAP;
        }
    }

    private static void renderRow(GuiGraphics guiGraphics, Font font, int x, int y, int width, RowData row) {
        int valueWidth = font.width(row.value());
        int labelMaxWidth = Math.max(24, width - valueWidth - 8);
        Component label = trimComponent(font, row.label(), labelMaxWidth);
        guiGraphics.drawString(font, label, x, y, TEXT_SECONDARY, false);
        guiGraphics.drawString(font, row.value(), x + width - valueWidth, y, TEXT_PRIMARY, false);

        int barY = y + font.lineHeight + BAR_TEXT_GAP;
        drawHorizontalBar(guiGraphics, x, barY, width, BAR_HEIGHT, row.ratio(), row.idleColor(), row.fillColor(), row.warning());
    }

    private static void drawCardFrame(GuiGraphics guiGraphics, int x, int y, int width, int height) {
        int x2 = x + width;
        int y2 = y + height;

        guiGraphics.fill(x, y, x2, y2, CARD_FRAME_SHADOW);
        guiGraphics.fill(x + 1, y + 1, x2 - 1, y2 - 1, CARD_BACKGROUND);
        guiGraphics.fill(x, y, x2 - 1, y + 1, CARD_FRAME_HIGHLIGHT);
        guiGraphics.fill(x, y, x + 1, y2 - 1, CARD_FRAME_HIGHLIGHT);
        guiGraphics.fill(x + 1, y + 1, x2 - 1, y + 2, CARD_FRAME_HIGHLIGHT_SOFT);
        guiGraphics.fill(x + 1, y + 1, x + 2, y2 - 1, CARD_FRAME_HIGHLIGHT_SOFT);
        guiGraphics.fill(x + 1, y2 - 2, x2, y2 - 1, CARD_FRAME_SHADOW_SOFT);
        guiGraphics.fill(x2 - 2, y + 1, x2 - 1, y2, CARD_FRAME_SHADOW_SOFT);
        guiGraphics.fill(x + 2, y + 2, x2 - 2, y + CARD_HEADER_HEIGHT, CARD_HEADER_BACKGROUND);
        guiGraphics.fill(x + 2, y + CARD_HEADER_HEIGHT, x2 - 2, y + CARD_HEADER_HEIGHT + 1, CARD_HEADER_ACCENT);
        guiGraphics.fill(x + 2, y + CARD_HEADER_HEIGHT + 1, x2 - 2, y2 - 2, CARD_BODY_BACKGROUND);
    }

    private static void drawHorizontalBar(
            GuiGraphics guiGraphics,
            int x,
            int y,
            int width,
            int height,
            float ratio,
            int backgroundColor,
            int fillColor,
            boolean warning
    ) {
        guiGraphics.fill(x, y, x + width, y + height, StationScreenStyle.PANEL_EDGE);
        guiGraphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, backgroundColor);

        int innerWidth = Math.max(0, width - 2);
        int fillWidth = Math.max(0, Math.min(innerWidth, Math.round(innerWidth * ratio)));
        if (fillWidth > 0) {
            guiGraphics.fill(x + 1, y + 1, x + 1 + fillWidth, y + height - 1, fillColor);
            guiGraphics.fill(x + 1, y + 1, x + 1 + fillWidth, y + 2, 0x55FFFFFF);
        }
    }

    private static void renderHoverTooltip(
            GuiGraphics guiGraphics,
            Font font,
            int screenWidth,
            int screenHeight,
            List<Component> lines,
            int mouseX,
            int mouseY
    ) {
        if (lines.isEmpty()) {
            return;
        }

        int tooltipWidth = 0;
        for (Component line : lines) {
            tooltipWidth = Math.max(tooltipWidth, font.width(line));
        }
        tooltipWidth += ModTooltipStyle.CONTENT_PADDING * 2;

        int lineAdvance = font.lineHeight + ModTooltipStyle.LINE_SPACING;
        int tooltipHeight = ModTooltipStyle.CONTENT_PADDING * 2 + lines.size() * lineAdvance - ModTooltipStyle.LINE_SPACING;

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

        guiGraphics.flush();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0.0F, 0.0F, 400.0F);
        RenderSystem.disableDepthTest();

        ModTooltipStyle.drawTooltipFrame(guiGraphics, tooltipX, tooltipY, tooltipX + tooltipWidth, tooltipY + tooltipHeight);

        int textY = tooltipY + ModTooltipStyle.CONTENT_PADDING;
        for (Component line : lines) {
            guiGraphics.drawString(font, line, tooltipX + ModTooltipStyle.CONTENT_PADDING, textY, ModTooltipStyle.TEXT_FALLBACK, false);
            textY += lineAdvance;
        }

        guiGraphics.flush();
        RenderSystem.enableDepthTest();
        guiGraphics.pose().popPose();
    }

    private static Component trimComponent(Font font, Component component, int maxWidth) {
        if (font.width(component) <= maxWidth) {
            return component;
        }
        String raw = component.getString();
        String trimmed = font.plainSubstrByWidth(raw, Math.max(0, maxWidth - font.width("..."))) + "...";
        return Component.literal(trimmed);
    }

    private static Vec2 scaledMousePosition(Minecraft minecraft, int guiWidth, int guiHeight) {
        double screenWidth = minecraft.getWindow().getScreenWidth();
        double screenHeight = minecraft.getWindow().getScreenHeight();
        if (screenWidth <= 0.0D || screenHeight <= 0.0D) {
            return new Vec2(0.0F, 0.0F);
        }
        float scaledX = (float) (minecraft.mouseHandler.xpos() * guiWidth / screenWidth);
        float scaledY = (float) (minecraft.mouseHandler.ypos() * guiHeight / screenHeight);
        return new Vec2(scaledX, scaledY);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private static String formatSeconds(int seconds) {
        return seconds + "s";
    }

    private static String formatNumber(double value) {
        if (Math.abs(value - Math.rint(value)) < 0.0001D) {
            return Integer.toString((int) Math.rint(value));
        }
        return String.format(Locale.ROOT, "%.1f", value);
    }

    private static float safeRatio(int value, int max) {
        if (max <= 0) {
            return 0.0F;
        }
        return Math.max(0.0F, Math.min(1.0F, (float) value / (float) max));
    }

    private static int strainColor(int effectiveCyberstrain) {
        if (effectiveCyberstrain >= 35) {
            return BAR_STRAIN_HIGH;
        }
        if (effectiveCyberstrain >= 20) {
            return BAR_STRAIN_MEDIUM;
        }
        return BAR_STRAIN_LOW;
    }

    private static int statusColor(String translationKey) {
        if ("hud.cyberneticenhancements.suppression".equals(translationKey)) {
            return BAR_STATUS_SUPPRESSANT;
        }
        if ("hud.cyberneticenhancements.ram_jolt".equals(translationKey)) {
            return BAR_STATUS_NEURAL;
        }
        return switch (translationKey) {
            case "tooltip.cyberneticenhancements.effect.health_regen",
                 "tooltip.cyberneticenhancements.effect.max_health",
                 "tooltip.cyberneticenhancements.effect.bonus_absorption" -> BAR_STATUS_MEDICAL;
            case "tooltip.cyberneticenhancements.effect.movement_speed",
                 "tooltip.cyberneticenhancements.effect.attack_speed",
                 "tooltip.cyberneticenhancements.effect.attack_damage",
                 "tooltip.cyberneticenhancements.effect.block_break_speed" -> BAR_STATUS_SYSTEM;
            case "tooltip.cyberneticenhancements.effect.damage_reduction",
                 "tooltip.cyberneticenhancements.effect.chrome_capacity",
                 "tooltip.cyberneticenhancements.effect.water_breathing" -> BAR_STATUS_NEURAL;
            default -> BAR_STATUS_MEDICAL;
        };
    }

    private static boolean isWarningStatus(String translationKey) {
        return "hud.cyberneticenhancements.suppression".equals(translationKey);
    }

    private static int cooldownColor(String translationKey) {
        return switch (translationKey) {
            case "tooltip.cyberneticenhancements.consumable_category.medical" -> BAR_STATUS_MEDICAL;
            case "tooltip.cyberneticenhancements.consumable_category.booster" -> BAR_STATUS_SYSTEM;
            case "tooltip.cyberneticenhancements.consumable_category.street" -> BAR_STATUS_STREET;
            case "tooltip.cyberneticenhancements.consumable_category.suppressant" -> BAR_STATUS_SUPPRESSANT;
            case "tooltip.cyberneticenhancements.consumable_category.neural" -> BAR_STATUS_NEURAL;
            case "item.cyberneticenhancements.biomonitor" -> BAR_STATUS_BIOMONITOR;
            case "item.cyberneticenhancements.blood_pump" -> BAR_STATUS_BLOOD_PUMP;
            case "item.cyberneticenhancements.second_heart" -> BAR_STATUS_SECOND_HEART;
            case "item.cyberneticenhancements.reflex_tuner" -> BAR_STATUS_REFLEX;
            default -> BAR_STATUS_SYSTEM;
        };
    }

    private record CardLayout(Component title, int width, List<RowData> rows) {
        int height() {
            return CARD_HEADER_HEIGHT + CARD_PADDING * 2 + rows.size() * ROW_HEIGHT + Math.max(0, rows.size() - 1) * ROW_GAP;
        }
    }

    private record RowData(
            Component label,
            Component value,
            float ratio,
            int fillColor,
            int idleColor,
            boolean warning,
            List<Component> tooltip
    ) {
    }

    private record HoverRegion(int x1, int y1, int x2, int y2, List<Component> tooltip) {
        boolean contains(int x, int y) {
            return x >= x1 && x < x2 && y >= y1 && y < y2;
        }
    }
}
