package de.artemis.cyberneticenhancements.common.cyberware;

public enum CyberwareAcquisitionMethod {
    CRAFTABLE,
    LOOT,
    QUEST;

    public String translationKey() {
        return "tooltip.cyberneticenhancements.acquisition." + name().toLowerCase();
    }
}
