package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CyberpsychosisControlPayload(
        boolean locked,
        float yaw,
        float pitch,
        float forward,
        float strafe,
        boolean jump,
        boolean sprint
) implements CustomPacketPayload {
    public static final Type<CyberpsychosisControlPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "cyberpsychosis_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CyberpsychosisControlPayload> STREAM_CODEC =
            CustomPacketPayload.codec((payload, buffer) -> {
                buffer.writeBoolean(payload.locked());
                buffer.writeFloat(payload.yaw());
                buffer.writeFloat(payload.pitch());
                buffer.writeFloat(payload.forward());
                buffer.writeFloat(payload.strafe());
                buffer.writeBoolean(payload.jump());
                buffer.writeBoolean(payload.sprint());
            }, buffer -> new CyberpsychosisControlPayload(
                    buffer.readBoolean(),
                    buffer.readFloat(),
                    buffer.readFloat(),
                    buffer.readFloat(),
                    buffer.readFloat(),
                    buffer.readBoolean(),
                    buffer.readBoolean()
            ));

    @Override
    public Type<CyberpsychosisControlPayload> type() {
        return TYPE;
    }
}
