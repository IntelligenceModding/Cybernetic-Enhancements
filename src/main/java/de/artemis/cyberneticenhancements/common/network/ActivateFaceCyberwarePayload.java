package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ActivateFaceCyberwarePayload() implements CustomPacketPayload {
    public static final Type<ActivateFaceCyberwarePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "activate_face_cyberware"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ActivateFaceCyberwarePayload> STREAM_CODEC =
            CustomPacketPayload.codec((payload, buffer) -> {
            }, buffer -> new ActivateFaceCyberwarePayload());

    @Override
    public Type<ActivateFaceCyberwarePayload> type() {
        return TYPE;
    }
}
