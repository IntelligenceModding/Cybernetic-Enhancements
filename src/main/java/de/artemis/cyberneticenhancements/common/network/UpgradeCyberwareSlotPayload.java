package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpgradeCyberwareSlotPayload(int slotOrdinal) implements CustomPacketPayload {
    public static final Type<UpgradeCyberwareSlotPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "upgrade_cyberware_slot"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpgradeCyberwareSlotPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, UpgradeCyberwareSlotPayload::slotOrdinal, UpgradeCyberwareSlotPayload::new);

    @Override
    public Type<UpgradeCyberwareSlotPayload> type() {
        return TYPE;
    }
}
