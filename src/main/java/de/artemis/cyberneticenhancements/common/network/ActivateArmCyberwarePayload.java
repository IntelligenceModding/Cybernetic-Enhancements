package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ActivateArmCyberwarePayload() implements CustomPacketPayload {
    public static final Type<ActivateArmCyberwarePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "activate_arm_cyberware"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ActivateArmCyberwarePayload> STREAM_CODEC =
            CustomPacketPayload.codec((payload, buffer) -> {
            }, buffer -> new ActivateArmCyberwarePayload());

    @Override
    public Type<ActivateArmCyberwarePayload> type() {
        return TYPE;
    }
}
