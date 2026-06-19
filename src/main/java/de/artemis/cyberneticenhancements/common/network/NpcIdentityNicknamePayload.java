package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record NpcIdentityNicknamePayload(String nickname) implements CustomPacketPayload {
    public static final Type<NpcIdentityNicknamePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "npc_identity_nickname"));
    public static final StreamCodec<RegistryFriendlyByteBuf, NpcIdentityNicknamePayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.STRING_UTF8, NpcIdentityNicknamePayload::nickname, NpcIdentityNicknamePayload::new);

    @Override
    public Type<NpcIdentityNicknamePayload> type() {
        return TYPE;
    }
}
