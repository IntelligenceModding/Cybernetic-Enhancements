package de.artemis.cyberneticenhancements.common.consumable;

public enum CyberConsumableCategory {
    MEDICAL("medical"),
    BOOSTER("booster"),
    NEURAL("neural"),
    SUPPRESSANT("suppressant"),
    STREET("street");

    private final String id;

    CyberConsumableCategory(String id) {
        this.id = id;
    }

    public String translationKey() {
        return "tooltip.cyberneticenhancements.consumable_category." + id;
    }
}
