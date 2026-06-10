package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ActivateAuxiliaryCyberwarePayload() implements CustomPacketPayload {
    public static final Type<ActivateAuxiliaryCyberwarePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "activate_auxiliary_cyberware"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ActivateAuxiliaryCyberwarePayload> STREAM_CODEC =
            CustomPacketPayload.codec((payload, buffer) -> {
            }, buffer -> new ActivateAuxiliaryCyberwarePayload());

    @Override
    public Type<ActivateAuxiliaryCyberwarePayload> type() {
        return TYPE;
    }
}
