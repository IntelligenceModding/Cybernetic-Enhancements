package de.artemis.cyberneticenhancements.common.menu;

public final class RecyclerStationLayout {
    public static final int IMAGE_WIDTH = TechStationLayout.IMAGE_WIDTH;
    public static final int IMAGE_HEIGHT = TechStationLayout.IMAGE_HEIGHT;

    public static final int WORK_PANEL_X1 = TechStationLayout.REPAIR_PANEL_X1;
    public static final int WORK_PANEL_Y1 = TechStationLayout.REPAIR_PANEL_Y1;
    public static final int WORK_PANEL_X2 = TechStationLayout.UPGRADE_PANEL_X2;
    public static final int WORK_PANEL_Y2 = TechStationLayout.REPAIR_PANEL_Y2;

    public static final int INVENTORY_PANEL_X1 = TechStationLayout.INVENTORY_PANEL_X1;
    public static final int INVENTORY_PANEL_Y1 = TechStationLayout.INVENTORY_PANEL_Y1;
    public static final int INVENTORY_PANEL_X2 = TechStationLayout.INVENTORY_PANEL_X2;
    public static final int INVENTORY_PANEL_Y2 = TechStationLayout.INVENTORY_PANEL_Y2;

    public static final int INTAKE_PANEL_X1 = TechStationLayout.REPAIR_PANEL_X1;
    public static final int INTAKE_PANEL_Y1 = TechStationLayout.REPAIR_PANEL_Y1;
    public static final int INTAKE_PANEL_X2 = TechStationLayout.REPAIR_PANEL_X2;
    public static final int INTAKE_PANEL_Y2 = TechStationLayout.REPAIR_PANEL_Y2;

    public static final int RECOVERY_PANEL_X1 = TechStationLayout.UPGRADE_PANEL_X1;
    public static final int RECOVERY_PANEL_Y1 = TechStationLayout.UPGRADE_PANEL_Y1;
    public static final int RECOVERY_PANEL_X2 = TechStationLayout.UPGRADE_PANEL_X2;
    public static final int RECOVERY_PANEL_Y2 = TechStationLayout.UPGRADE_PANEL_Y2;

    public static final int INPUT_X = TechStationLayout.REPAIR_MATERIAL_X;
    public static final int INPUT_Y = TechStationLayout.REPAIR_MATERIAL_Y;
    public static final int RESULT_SLOT_COUNT = 3;
    public static final int[] RESULT_SLOT_X = {
            TechStationLayout.UPGRADE_INPUT_X,
            TechStationLayout.UPGRADE_PRIMARY_X,
            TechStationLayout.UPGRADE_RESULT_X
    };
    public static final int[] RESULT_SLOT_Y = {
            INPUT_Y,
            INPUT_Y,
            INPUT_Y
    };

    public static final int PLAYER_INVENTORY_X = TechStationLayout.PLAYER_INVENTORY_X;
    public static final int PLAYER_INVENTORY_Y = TechStationLayout.PLAYER_INVENTORY_Y;
    public static final int PLAYER_HOTBAR_Y = TechStationLayout.PLAYER_HOTBAR_Y;

    private RecyclerStationLayout() {
    }
}
