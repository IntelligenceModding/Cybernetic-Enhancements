package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record UpgradeRipperSubSlotPayload(int kind, int handlerIndex, int slotIndex) implements CustomPacketPayload {
    public static final int KIND_CHIPWARE = 0;
    public static final int KIND_ARM_MODULE = 1;
    public static final int KIND_LEG_MODULE = 2;

    public static final Type<UpgradeRipperSubSlotPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "upgrade_ripper_sub_slot"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpgradeRipperSubSlotPayload> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT, UpgradeRipperSubSlotPayload::kind,
                    ByteBufCodecs.INT, UpgradeRipperSubSlotPayload::handlerIndex,
                    ByteBufCodecs.INT, UpgradeRipperSubSlotPayload::slotIndex,
                    UpgradeRipperSubSlotPayload::new
            );

    @Override
    public Type<UpgradeRipperSubSlotPayload> type() {
        return TYPE;
    }
}
