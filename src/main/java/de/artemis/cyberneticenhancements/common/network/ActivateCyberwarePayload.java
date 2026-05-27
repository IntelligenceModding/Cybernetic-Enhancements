package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ActivateCyberwarePayload() implements CustomPacketPayload {
    public static final Type<ActivateCyberwarePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "activate_cyberware"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ActivateCyberwarePayload> STREAM_CODEC =
            CustomPacketPayload.codec((payload, buffer) -> {
            }, buffer -> new ActivateCyberwarePayload());

    @Override
    public Type<ActivateCyberwarePayload> type() {
        return TYPE;
    }
}
