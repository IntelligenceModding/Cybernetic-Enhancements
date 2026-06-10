package de.artemis.cyberneticenhancements.common.cyberware;

public enum CombatStatusType {
    SHOCK("shock", 3),
    OVERHEAT("overheat", 3),
    CORROSION("corrosion", 3),
    TRAUMA("trauma", 2),
    BLEED("bleed", 3),
    MARK("mark", 2);

    private final String id;
    private final int maxStacks;

    CombatStatusType(String id, int maxStacks) {
        this.id = id;
        this.maxStacks = maxStacks;
    }

    public String id() {
        return id;
    }

    public int maxStacks() {
        return maxStacks;
    }

    public String translationKey() {
        return "hud.cyberneticenhancements.status." + id;
    }

    public String descriptionKey() {
        return "tooltip.cyberneticenhancements.status." + id;
    }
}
