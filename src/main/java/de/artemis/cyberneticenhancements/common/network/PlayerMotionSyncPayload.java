package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PlayerMotionSyncPayload(
        double x,
        double y,
        double z,
        double motionX,
        double motionY,
        double motionZ,
        float yRot,
        float xRot
) implements CustomPacketPayload {
    public static final Type<PlayerMotionSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "player_motion_sync"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerMotionSyncPayload> STREAM_CODEC =
            CustomPacketPayload.codec((payload, buffer) -> {
                buffer.writeDouble(payload.x());
                buffer.writeDouble(payload.y());
                buffer.writeDouble(payload.z());
                buffer.writeDouble(payload.motionX());
                buffer.writeDouble(payload.motionY());
                buffer.writeDouble(payload.motionZ());
                buffer.writeFloat(payload.yRot());
                buffer.writeFloat(payload.xRot());
            }, buffer -> new PlayerMotionSyncPayload(
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readDouble(),
                    buffer.readFloat(),
                    buffer.readFloat()
            ));

    @Override
    public Type<PlayerMotionSyncPayload> type() {
        return TYPE;
    }
}
