package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ArchiveBankingRequestPayload() implements CustomPacketPayload {
    public static final Type<ArchiveBankingRequestPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "archive_banking_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArchiveBankingRequestPayload> STREAM_CODEC =
            CustomPacketPayload.codec((payload, buffer) -> { }, buffer -> new ArchiveBankingRequestPayload());

    @Override
    public Type<ArchiveBankingRequestPayload> type() {
        return TYPE;
    }
}
