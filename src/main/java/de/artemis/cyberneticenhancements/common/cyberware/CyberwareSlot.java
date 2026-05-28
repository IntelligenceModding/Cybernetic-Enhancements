package de.artemis.cyberneticenhancements.common.cyberware;

public enum CyberwareSlot {
    FRONTAL_CORTEX_1(CyberwareSlotType.FRONTAL_CORTEX, 1),
    FRONTAL_CORTEX_2(CyberwareSlotType.FRONTAL_CORTEX, 2),
    FRONTAL_CORTEX_3(CyberwareSlotType.FRONTAL_CORTEX, 3),
    OPERATING_SYSTEM_1(CyberwareSlotType.OPERATING_SYSTEM, 1),
    FACE_1(CyberwareSlotType.FACE, 1),
    ARMS_1(CyberwareSlotType.ARMS, 1),
    HANDS_1(CyberwareSlotType.HANDS, 1),
    SKELETON_1(CyberwareSlotType.SKELETON, 1),
    SKELETON_2(CyberwareSlotType.SKELETON, 2),
    NERVOUS_SYSTEM_1(CyberwareSlotType.NERVOUS_SYSTEM, 1),
    NERVOUS_SYSTEM_2(CyberwareSlotType.NERVOUS_SYSTEM, 2),
    NERVOUS_SYSTEM_3(CyberwareSlotType.NERVOUS_SYSTEM, 3),
    CIRCULATORY_SYSTEM_1(CyberwareSlotType.CIRCULATORY_SYSTEM, 1),
    CIRCULATORY_SYSTEM_2(CyberwareSlotType.CIRCULATORY_SYSTEM, 2),
    CIRCULATORY_SYSTEM_3(CyberwareSlotType.CIRCULATORY_SYSTEM, 3),
    INTEGUMENTARY_SYSTEM_1(CyberwareSlotType.INTEGUMENTARY_SYSTEM, 1),
    INTEGUMENTARY_SYSTEM_2(CyberwareSlotType.INTEGUMENTARY_SYSTEM, 2),
    INTEGUMENTARY_SYSTEM_3(CyberwareSlotType.INTEGUMENTARY_SYSTEM, 3),
    LEGS_1(CyberwareSlotType.LEGS, 1),
    HANDS_2(CyberwareSlotType.HANDS, 2),
    SKELETON_3(CyberwareSlotType.SKELETON, 3);

    private final CyberwareSlotType type;
    private final int slotNumber;

    CyberwareSlot(CyberwareSlotType type, int slotNumber) {
        this.type = type;
        this.slotNumber = slotNumber;
    }

    public CyberwareSlotType getType() {
        return type;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public String displayKey() {
        return type.translationKey() + "." + slotNumber;
    }
}
