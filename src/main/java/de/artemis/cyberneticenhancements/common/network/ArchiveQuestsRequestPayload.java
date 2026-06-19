package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ArchiveQuestsRequestPayload() implements CustomPacketPayload {
    public static final Type<ArchiveQuestsRequestPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "archive_quests_request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArchiveQuestsRequestPayload> STREAM_CODEC =
            CustomPacketPayload.codec((payload, buffer) -> {
            }, buffer -> new ArchiveQuestsRequestPayload());

    @Override
    public Type<ArchiveQuestsRequestPayload> type() {
        return TYPE;
    }
}
