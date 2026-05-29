package de.artemis.cyberneticenhancements.client.tooltip;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import de.artemis.cyberneticenhancements.client.screen.RecyclerStationScreen;
import de.artemis.cyberneticenhancements.client.screen.RipperStationScreen;
import de.artemis.cyberneticenhancements.client.screen.TechstationScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;

public final class ModTooltipStyle {
    public static final int BACKGROUND = 0xF0100010;
    public static final int BORDER = 0xFF304250;
    public static final int BORDER_HIGHLIGHT = 0xFF4C667A;
    public static final int CONTENT_PADDING = 6;
    public static final int LINE_SPACING = 2;
    public static final int BAR_GAP = 4;
    public static final int BAR_BOTTOM_PADDING = 4;
    public static final int TEXT_FALLBACK = 0xFFFFFFFF;
    public static final int SCREEN_EDGE_MARGIN = 6;
    public static final int TOOLTIP_CURSOR_OFFSET_X = 12;
    public static final int TOOLTIP_CURSOR_OFFSET_Y = 12;

    private ModTooltipStyle() {
    }

    public static boolean shouldStyle(ItemStack stack) {
        Screen screen = Minecraft.getInstance().screen;
        if (isModScreen(screen)) {
            return true;
        }

        if (stack.isEmpty()) {
            return false;
        }

        return CyberneticEnhancements.MOD_ID.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace());
    }

    public static void apply(RenderTooltipEvent.Color event) {
        event.setBackground(BACKGROUND);
        event.setBorderStart(BORDER_HIGHLIGHT);
        event.setBorderEnd(BORDER);
    }

    public static void drawTooltipFrame(net.minecraft.client.gui.GuiGraphics guiGraphics, int x1, int y1, int x2, int y2) {
        guiGraphics.fill(x1, y1, x2, y2, BORDER);
        guiGraphics.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, BACKGROUND);
        guiGraphics.fill(x1, y1, x2, y1 + 1, BORDER_HIGHLIGHT);
        guiGraphics.fill(x1, y1, x1 + 1, y2, BORDER_HIGHLIGHT);
    }

    private static boolean isModScreen(Screen screen) {
        return screen instanceof RipperStationScreen
                || screen instanceof TechstationScreen
                || screen instanceof RecyclerStationScreen;
    }
}
