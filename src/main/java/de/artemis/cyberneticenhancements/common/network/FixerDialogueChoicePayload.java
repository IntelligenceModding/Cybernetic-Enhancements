package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record FixerDialogueChoicePayload(int optionId) implements CustomPacketPayload {
    public static final Type<FixerDialogueChoicePayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "fixer_dialogue_choice"));
    public static final StreamCodec<RegistryFriendlyByteBuf, FixerDialogueChoicePayload> STREAM_CODEC =
            StreamCodec.composite(ByteBufCodecs.INT, FixerDialogueChoicePayload::optionId, FixerDialogueChoicePayload::new);

    @Override
    public Type<FixerDialogueChoicePayload> type() {
        return TYPE;
    }
}
