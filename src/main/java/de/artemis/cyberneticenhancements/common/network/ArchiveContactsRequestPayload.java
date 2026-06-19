package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ArchiveContactsRequestPayload() implements CustomPacketPayload {
    public static final Type<ArchiveContactsRequestPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "archive_contacts_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArchiveContactsRequestPayload> STREAM_CODEC =
            CustomPacketPayload.codec((payload, buffer) -> {
            }, buffer -> new ArchiveContactsRequestPayload());

    @Override
    public Type<ArchiveContactsRequestPayload> type() {
        return TYPE;
    }
}
