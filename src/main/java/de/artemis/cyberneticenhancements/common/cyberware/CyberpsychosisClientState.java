package de.artemis.cyberneticenhancements.common.cyberware;

import net.minecraft.util.Mth;

public final class CyberpsychosisClientState {
    private static volatile boolean controlLocked;
    private static volatile float targetYaw;
    private static volatile float targetPitch;
    private static volatile float appliedYaw;
    private static volatile float appliedPitch;
    private static volatile float forward;
    private static volatile float strafe;
    private static volatile boolean jump;
    private static volatile boolean sprint;

    private CyberpsychosisClientState() {
    }

    public static boolean isControlLocked() {
        return controlLocked;
    }

    public static float yaw() {
        return appliedYaw;
    }

    public static float pitch() {
        return appliedPitch;
    }

    public static float targetYaw() {
        return targetYaw;
    }

    public static float targetPitch() {
        return targetPitch;
    }

    public static float forward() {
        return forward;
    }

    public static float strafe() {
        return strafe;
    }

    public static boolean jump() {
        return jump;
    }

    public static boolean sprint() {
        return sprint;
    }

    public static void apply(boolean locked, float newYaw, float newPitch, float newForward, float newStrafe, boolean newJump, boolean newSprint) {
        boolean wasLocked = controlLocked;
        controlLocked = locked;
        targetYaw = newYaw;
        targetPitch = newPitch;
        if (!wasLocked || !locked) {
            appliedYaw = newYaw;
            appliedPitch = newPitch;
        }
        forward = newForward;
        strafe = newStrafe;
        jump = newJump;
        sprint = newSprint;
    }

    public static void tickSmoothing() {
        if (!controlLocked) {
            return;
        }

        float yawDelta = Mth.wrapDegrees(targetYaw - appliedYaw);
        float pitchDelta = targetPitch - appliedPitch;
        appliedYaw += Mth.clamp(yawDelta * 0.18F, -5.5F, 5.5F);
        appliedPitch += Mth.clamp(pitchDelta * 0.20F, -4.0F, 4.0F);
        appliedPitch = Mth.clamp(appliedPitch, -90.0F, 90.0F);
    }

    public static void setControlLocked(boolean locked) {
        apply(locked, 0.0F, 0.0F, 0.0F, 0.0F, false, false);
    }
}
