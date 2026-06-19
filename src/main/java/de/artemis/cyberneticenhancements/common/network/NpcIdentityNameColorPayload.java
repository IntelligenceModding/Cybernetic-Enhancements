package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record NpcIdentityNameColorPayload(String colorId) implements CustomPacketPayload {
    public static final Type<NpcIdentityNameColorPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "npc_identity_name_color"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NpcIdentityNameColorPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8, NpcIdentityNameColorPayload::colorId, NpcIdentityNameColorPayload::new);

    @Override
    public Type<NpcIdentityNameColorPayload> type() {
        return TYPE;
    }
}
