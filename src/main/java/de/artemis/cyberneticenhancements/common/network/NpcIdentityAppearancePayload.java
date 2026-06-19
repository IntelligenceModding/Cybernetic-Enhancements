package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record NpcIdentityAppearancePayload(String appearanceId) implements CustomPacketPayload {
    public static final Type<NpcIdentityAppearancePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "npc_identity_appearance"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NpcIdentityAppearancePayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8, NpcIdentityAppearancePayload::appearanceId, NpcIdentityAppearancePayload::new);

    @Override
    public Type<NpcIdentityAppearancePayload> type() {
        return TYPE;
    }
}
