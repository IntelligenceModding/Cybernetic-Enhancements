package de.artemis.cyberneticenhancements.client;

import de.artemis.cyberneticenhancements.common.network.PlayerMotionSyncPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public final class PlayerMotionSyncClient {
    private PlayerMotionSyncClient() {
    }

    public static void apply(PlayerMotionSyncPayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }

        player.moveTo(payload.x(), payload.y(), payload.z(), payload.yRot(), payload.xRot());
        player.xOld = payload.x();
        player.yOld = payload.y();
        player.zOld = payload.z();
        player.yRotO = payload.yRot();
        player.xRotO = payload.xRot();
        player.setDeltaMovement(payload.motionX(), payload.motionY(), payload.motionZ());
        player.hasImpulse = true;
        player.hurtMarked = true;
    }
}
