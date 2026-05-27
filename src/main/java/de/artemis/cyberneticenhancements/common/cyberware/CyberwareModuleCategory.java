package de.artemis.cyberneticenhancements.common.cyberware;

public enum CyberwareModuleCategory {
    ARMS("arms"),
    LEGS("legs");

    private final String id;

    CyberwareModuleCategory(String id) {
        this.id = id;
    }

    public String translationKey() {
        return "tooltip.cyberneticenhancements.module_category." + id;
    }
}
