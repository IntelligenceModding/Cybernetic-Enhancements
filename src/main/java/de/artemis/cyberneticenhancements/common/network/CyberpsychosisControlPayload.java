package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CyberpsychosisControlPayload(boolean locked) implements CustomPacketPayload {
    public static final Type<CyberpsychosisControlPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "cyberpsychosis_control"));
    public static final StreamCodec<RegistryFriendlyByteBuf, CyberpsychosisControlPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.BOOL, CyberpsychosisControlPayload::locked, CyberpsychosisControlPayload::new);

    @Override
    public Type<CyberpsychosisControlPayload> type() {
        return TYPE;
    }
}
