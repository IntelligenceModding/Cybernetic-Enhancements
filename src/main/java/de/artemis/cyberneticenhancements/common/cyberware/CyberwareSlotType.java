package de.artemis.cyberneticenhancements.common.cyberware;

public enum CyberwareSlotType {
    FRONTAL_CORTEX("frontal_cortex", 3),
    OPERATING_SYSTEM("operating_system", 1),
    ARMS("arms", 1),
    FACE("face", 1),
    SKELETON("skeleton", 2),
    HANDS("hands", 1),
    NERVOUS_SYSTEM("nervous_system", 3),
    CIRCULATORY_SYSTEM("circulatory_system", 3),
    INTEGUMENTARY_SYSTEM("integumentary_system", 3),
    LEGS("legs", 1);

    private final String id;
    private final int baseSlotCount;

    CyberwareSlotType(String id, int baseSlotCount) {
        this.id = id;
        this.baseSlotCount = baseSlotCount;
    }

    public String translationKey() {
        return "cyberwareSlot.cyberneticenhancements." + id;
    }

    public int getBaseSlotCount() {
        return baseSlotCount;
    }
}
