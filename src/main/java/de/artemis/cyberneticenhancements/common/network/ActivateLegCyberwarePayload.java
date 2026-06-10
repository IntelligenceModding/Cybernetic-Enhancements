package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ActivateLegCyberwarePayload() implements CustomPacketPayload {
    public static final Type<ActivateLegCyberwarePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "activate_leg_cyberware"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ActivateLegCyberwarePayload> STREAM_CODEC =
            CustomPacketPayload.codec((payload, buffer) -> {
            }, buffer -> new ActivateLegCyberwarePayload());

    @Override
    public Type<ActivateLegCyberwarePayload> type() {
        return TYPE;
    }
}
