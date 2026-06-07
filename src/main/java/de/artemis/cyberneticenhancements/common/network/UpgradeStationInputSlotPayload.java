package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpgradeStationInputSlotPayload(int slotIndex) implements CustomPacketPayload {
    public static final Type<UpgradeStationInputSlotPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "upgrade_station_input_slot"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpgradeStationInputSlotPayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, UpgradeStationInputSlotPayload::slotIndex, UpgradeStationInputSlotPayload::new);

    @Override
    public Type<UpgradeStationInputSlotPayload> type() {
        return TYPE;
    }
}
