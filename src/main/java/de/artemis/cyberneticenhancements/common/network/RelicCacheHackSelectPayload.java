package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RelicCacheHackSelectPayload(int cellIndex) implements CustomPacketPayload {
    public static final Type<RelicCacheHackSelectPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "relic_cache_hack_select"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RelicCacheHackSelectPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, RelicCacheHackSelectPayload::cellIndex, RelicCacheHackSelectPayload::new);

    @Override
    public Type<RelicCacheHackSelectPayload> type() {
        return TYPE;
    }
}
