package de.artemis.cyberneticenhancements.common.cyberware;

import de.artemis.cyberneticenhancements.common.network.PlayerMotionSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public final class PlayerMotionSyncHelper {
    private PlayerMotionSyncHelper() {
    }

    public static void sync(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            sync(serverPlayer);
        }
    }

    public static void sync(Entity entity) {
        if (entity instanceof ServerPlayer serverPlayer) {
            sync(serverPlayer);
        }
    }

    public static void sync(ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new PlayerMotionSyncPayload(
                player.getX(),
                player.getY(),
                player.getZ(),
                player.getDeltaMovement().x,
                player.getDeltaMovement().y,
                player.getDeltaMovement().z,
                player.getYRot(),
                player.getXRot()
        ));
    }
}
