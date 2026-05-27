package de.artemis.cyberneticenhancements.common.consumable;

public final class CyberConsumableTooltip {
    private CyberConsumableTooltip() {
    }

    public static String overdoseRating(int points) {
        return switch (points) {
            case 0 -> "None";
            case 1, 2 -> "Low";
            case 3, 4 -> "Medium";
            default -> "High";
        };
    }
}
