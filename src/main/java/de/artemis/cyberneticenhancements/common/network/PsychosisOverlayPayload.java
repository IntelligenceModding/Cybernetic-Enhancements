package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record PsychosisOverlayPayload(List<Entry> entries) implements CustomPacketPayload {
    public static final Type<PsychosisOverlayPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "psychosis_overlay"));

    public static final StreamCodec<RegistryFriendlyByteBuf, Entry> ENTRY_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            Entry::playerName,
            ByteBufCodecs.STRING_UTF8,
            Entry::tier,
            ByteBufCodecs.INT,
            Entry::remainingSeconds,
            ByteBufCodecs.INT,
            Entry::totalSeconds,
            Entry::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, PsychosisOverlayPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, ENTRY_STREAM_CODEC),
            PsychosisOverlayPayload::entries,
            PsychosisOverlayPayload::new
    );

    public record Entry(String playerName, String tier, int remainingSeconds, int totalSeconds) {
    }

    @Override
    public Type<PsychosisOverlayPayload> type() {
        return TYPE;
    }
}
