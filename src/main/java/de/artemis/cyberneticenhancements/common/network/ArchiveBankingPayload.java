package de.artemis.cyberneticenhancements.common.network;

import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import de.artemis.cyberneticenhancements.common.economy.PlayerEurodollarManager;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public record ArchiveBankingPayload(
        int balance,
        int totalIncome,
        int totalExpense,
        int net,
        List<Integer> graphNet,
        List<LedgerEntry> entries,
        List<RequestEntry> requests
) implements CustomPacketPayload {
    public static final Type<ArchiveBankingPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, "archive_banking"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ArchiveBankingPayload> STREAM_CODEC =
            CustomPacketPayload.codec((payload, buffer) -> {
                buffer.writeVarInt(payload.balance);
                buffer.writeVarInt(payload.totalIncome);
                buffer.writeVarInt(payload.totalExpense);
                buffer.writeVarInt(payload.net);
                buffer.writeVarInt(payload.graphNet.size());
                for (Integer point : payload.graphNet) {
                    buffer.writeVarInt(point);
                }
                buffer.writeVarInt(payload.entries.size());
                for (LedgerEntry entry : payload.entries) {
                    buffer.writeVarLong(entry.sequence());
                    buffer.writeVarLong(entry.day());
                    buffer.writeUtf(entry.typeId());
                    buffer.writeVarInt(entry.amount());
                    buffer.writeUtf(entry.counterpartyName());
                    buffer.writeUtf(entry.note());
                }
                buffer.writeVarInt(payload.requests.size());
                for (RequestEntry request : payload.requests) {
                    buffer.writeUtf(request.requestId());
                    buffer.writeUtf(request.fromName());
                    buffer.writeUtf(request.toName());
                    buffer.writeVarInt(request.amount());
                    buffer.writeBoolean(request.incoming());
                    buffer.writeVarLong(request.createdDay());
                }
            }, buffer -> {
                int balance = buffer.readVarInt();
                int totalIncome = buffer.readVarInt();
                int totalExpense = buffer.readVarInt();
                int net = buffer.readVarInt();
                int graphCount = buffer.readVarInt();
                List<Integer> graph = new ArrayList<>(graphCount);
                for (int i = 0; i < graphCount; i++) {
                    graph.add(buffer.readVarInt());
                }
                int entryCount = buffer.readVarInt();
                List<LedgerEntry> entries = new ArrayList<>(entryCount);
                for (int i = 0; i < entryCount; i++) {
                    entries.add(new LedgerEntry(
                            buffer.readVarLong(),
                            buffer.readVarLong(),
                            buffer.readUtf(),
                            buffer.readVarInt(),
                            buffer.readUtf(),
                            buffer.readUtf()
                    ));
                }
                int requestCount = buffer.readVarInt();
                List<RequestEntry> requests = new ArrayList<>(requestCount);
                for (int i = 0; i < requestCount; i++) {
                    requests.add(new RequestEntry(
                            buffer.readUtf(),
                            buffer.readUtf(),
                            buffer.readUtf(),
                            buffer.readVarInt(),
                            buffer.readBoolean(),
                            buffer.readVarLong()
                    ));
                }
                return new ArchiveBankingPayload(balance, totalIncome, totalExpense, net, List.copyOf(graph), List.copyOf(entries), List.copyOf(requests));
            });

    @Override
    public Type<ArchiveBankingPayload> type() {
        return TYPE;
    }

    public static ArchiveBankingPayload capture(ServerPlayer player) {
        List<Integer> graph = new ArrayList<>();
        for (int point : PlayerEurodollarManager.graphNet(player, 12)) {
            graph.add(point);
        }
        List<LedgerEntry> entries = new ArrayList<>();
        for (PlayerEurodollarManager.TransactionEntry entry : PlayerEurodollarManager.ledger(player)) {
            entries.add(new LedgerEntry(
                    entry.sequence(),
                    entry.day(),
                    entry.typeId(),
                    entry.amount(),
                    entry.counterpartyName(),
                    entry.note()
            ));
        }
        List<RequestEntry> requests = new ArrayList<>();
        for (PlayerEurodollarManager.PaymentRequest request : PlayerEurodollarManager.requests(player)) {
            requests.add(new RequestEntry(
                    request.requestId().toString(),
                    request.fromPlayerName(),
                    request.toPlayerName(),
                    request.amount(),
                    request.toPlayerId().equals(player.getUUID()),
                    request.createdDay()
            ));
        }
        int income = PlayerEurodollarManager.totalIncome(player);
        int expense = PlayerEurodollarManager.totalExpense(player);
        return new ArchiveBankingPayload(
                PlayerEurodollarManager.balance(player),
                income,
                expense,
                income - expense,
                List.copyOf(graph),
                List.copyOf(entries),
                List.copyOf(requests)
        );
    }

    public record LedgerEntry(
            long sequence,
            long day,
            String typeId,
            int amount,
            String counterpartyName,
            String note
    ) {
    }

    public record RequestEntry(
            String requestId,
            String fromName,
            String toName,
            int amount,
            boolean incoming,
            long createdDay
    ) {
    }
}
