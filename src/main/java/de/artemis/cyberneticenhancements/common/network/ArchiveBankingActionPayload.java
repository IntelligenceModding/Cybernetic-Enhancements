package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ArchiveBankingActionPayload(
        String actionId,
        String targetName,
        int amount,
        String requestId
) implements CustomPacketPayload {
    public static final String ACTION_SEND = "send";
    public static final String ACTION_REQUEST = "request";
    public static final String ACTION_APPROVE = "approve";
    public static final String ACTION_DECLINE = "decline";

    public static final Type<ArchiveBankingActionPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "archive_banking_action"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArchiveBankingActionPayload> STREAM_CODEC =
            CustomPacketPayload.codec((payload, buffer) -> {
                buffer.writeUtf(payload.actionId());
                buffer.writeUtf(payload.targetName());
                buffer.writeVarInt(payload.amount());
                buffer.writeUtf(payload.requestId());
            }, buffer -> new ArchiveBankingActionPayload(
                    buffer.readUtf(),
                    buffer.readUtf(),
                    buffer.readVarInt(),
                    buffer.readUtf()
            ));

    @Override
    public Type<ArchiveBankingActionPayload> type() {
        return TYPE;
    }
}
