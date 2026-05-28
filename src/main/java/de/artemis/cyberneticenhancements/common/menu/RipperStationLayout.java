package de.artemis.cyberneticenhancements.common.menu;

public final class RipperStationLayout {
    public static final int IMAGE_WIDTH = 800;
    public static final int IMAGE_HEIGHT = 456;

    public static final int[] CYBERWARE_SLOT_X = {
            370, 392, 414,
            392,
            526,
            258,
            515,
            504, 526,
            504, 526, 548,
            236, 258, 280,
            236, 258, 280,
            258,
            537,
            548
    };
    public static final int[] CYBERWARE_SLOT_Y = {
            214, 214, 214,
            46,
            46,
            108,
            108,
            214, 214,
            164, 164, 164,
            164, 164, 164,
            214, 214, 214,
            46,
            108,
            214
    };

    public static final int[] CHIP_CLUSTER_X = {244, 378, 512};
    public static final int[] CHIP_SLOT_OFFSET_X = {0, 22, 44};
    public static final int CHIP_SLOT_Y = 304;

    public static final int[] ARM_MODULE_X = {71, 93, 115};
    public static final int ARM_MODULE_Y = 304;

    public static final int[] LEG_MODULE_X = {669, 691, 713};
    public static final int LEG_MODULE_Y = 304;

    public static final int PLAYER_INVENTORY_X = 324;
    public static final int PLAYER_INVENTORY_Y = 355;
    public static final int PLAYER_HOTBAR_Y = 411;
    public static final int PLAYER_SLOT_SPACING = 17;

    private RipperStationLayout() {
    }
}
