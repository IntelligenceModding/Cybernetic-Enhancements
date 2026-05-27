package de.artemis.cyberneticenhancements.common.cyberware;

public final class CyberpsychosisClientState {
    private static volatile boolean controlLocked;

    private CyberpsychosisClientState() {
    }

    public static boolean isControlLocked() {
        return controlLocked;
    }

    public static void setControlLocked(boolean locked) {
        controlLocked = locked;
    }
}
