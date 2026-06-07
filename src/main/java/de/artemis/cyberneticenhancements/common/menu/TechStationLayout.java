package de.artemis.cyberneticenhancements.common.menu;

public final class TechStationLayout {
    public static final int IMAGE_WIDTH = 432;
    public static final int IMAGE_HEIGHT = 196 + StationLayoutMetrics.INVENTORY_SECTION_HEIGHT + StationLayoutMetrics.BOTTOM_FRAME;

    public static final int REPAIR_PANEL_X1 = StationLayoutMetrics.OUTER_FRAME;
    public static final int REPAIR_PANEL_Y1 = StationLayoutMetrics.OUTER_FRAME;
    public static final int REPAIR_PANEL_X2 = 211;
    public static final int REPAIR_PANEL_Y2 = 186;

    public static final int UPGRADE_PANEL_X1 = REPAIR_PANEL_X2 + StationLayoutMetrics.PANEL_DIVIDER;
    public static final int UPGRADE_PANEL_Y1 = StationLayoutMetrics.OUTER_FRAME;
    public static final int UPGRADE_PANEL_X2 = 420;
    public static final int UPGRADE_PANEL_Y2 = 186;

    public static final int INVENTORY_PANEL_X1 = StationLayoutMetrics.OUTER_FRAME;
    public static final int INVENTORY_PANEL_Y1 = 196;
    public static final int INVENTORY_PANEL_X2 = 420;
    public static final int INVENTORY_PANEL_Y2 = INVENTORY_PANEL_Y1 + StationLayoutMetrics.INVENTORY_SECTION_HEIGHT;

    public static final int REPAIR_INPUT_X = 47;
    public static final int REPAIR_INPUT_Y = 74;
    public static final int REPAIR_MATERIAL_X = 103;
    public static final int REPAIR_PRIMARY_Y = 52;
    public static final int REPAIR_MATERIAL_Y = 74;
    public static final int REPAIR_TERTIARY_Y = 96;
    public static final int REPAIR_RESULT_X = 159;
    public static final int REPAIR_RESULT_Y = 74;

    public static final int UPGRADE_INPUT_X = 256;
    public static final int UPGRADE_INPUT_Y = 74;
    public static final int UPGRADE_PRIMARY_X = 312;
    public static final int UPGRADE_PRIMARY_Y = 52;
    public static final int UPGRADE_SECONDARY_X = 312;
    public static final int UPGRADE_SECONDARY_Y = 96;
    public static final int UPGRADE_RESULT_X = 368;
    public static final int UPGRADE_RESULT_Y = 74;

    public static final int PLAYER_INVENTORY_X = 135;
    public static final int PLAYER_INVENTORY_Y = 212;
    public static final int PLAYER_HOTBAR_Y = 270;

    private TechStationLayout() {
    }
}
