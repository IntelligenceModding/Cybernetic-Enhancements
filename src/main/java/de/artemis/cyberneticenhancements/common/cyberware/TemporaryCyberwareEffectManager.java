package de.artemis.cyberneticenhancements.common.cyberware;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public final class TemporaryCyberwareEffectManager {
    private static final String EFFECTS_KEY = "cyberneticenhancements.temporary_effects";
    private static final String TYPE_KEY = "Type";
    private static final String AMOUNT_KEY = "Amount";
    private static final String UNTIL_KEY = "Until";
    private static final String DURATION_KEY = "Duration";

    private TemporaryCyberwareEffectManager() {
    }

    public record ActiveEffectStatus(CyberwareEffectType type, double amount, int remainingSeconds, int totalSeconds) {
    }

    public static void addEffect(Player player, CyberwareEffect effect, long durationTicks) {
        if (durationTicks <= 0L || Math.abs(effect.amount()) < 0.0001D) {
            return;
        }
        durationTicks = FrontalCortexManager.adjustTemporaryEffectDuration(player, durationTicks);

        CompoundTag entry = new CompoundTag();
        entry.putString(TYPE_KEY, effect.type().name());
        entry.putDouble(AMOUNT_KEY, effect.amount());
        entry.putLong(UNTIL_KEY, player.level().getGameTime() + durationTicks);
        entry.putLong(DURATION_KEY, durationTicks);

        CompoundTag persistentData = player.getPersistentData();
        ListTag effects = persistentData.getList(EFFECTS_KEY, Tag.TAG_COMPOUND);
        effects.add(entry);
        persistentData.put(EFFECTS_KEY, effects);
    }

    public static void addEffects(Player player, long durationTicks, CyberwareEffect... effects) {
        for (CyberwareEffect effect : effects) {
            addEffect(player, effect, durationTicks);
        }
    }

    public static void mergeActiveEffects(Player player, EnumMap<CyberwareEffectType, Double> totals, long gameTime) {
        CompoundTag persistentData = player.getPersistentData();
        ListTag storedEffects = persistentData.getList(EFFECTS_KEY, Tag.TAG_COMPOUND);
        if (storedEffects.isEmpty()) {
            persistentData.remove(EFFECTS_KEY);
            return;
        }

        ListTag activeEffects = new ListTag();
        for (Tag tag : storedEffects) {
            if (!(tag instanceof CompoundTag entry) || entry.getLong(UNTIL_KEY) <= gameTime) {
                continue;
            }

            CyberwareEffectType type;
            try {
                type = CyberwareEffectType.valueOf(entry.getString(TYPE_KEY));
            } catch (IllegalArgumentException ignored) {
                continue;
            }

            totals.merge(type, entry.getDouble(AMOUNT_KEY), type.isMobEffect() ? Math::max : Double::sum);
            activeEffects.add(entry.copy());
        }

        if (activeEffects.isEmpty()) {
            persistentData.remove(EFFECTS_KEY);
        } else {
            persistentData.put(EFFECTS_KEY, activeEffects);
        }
    }

    public static double getActiveAmount(Player player, CyberwareEffectType targetType, long gameTime) {
        CompoundTag persistentData = player.getPersistentData();
        ListTag storedEffects = persistentData.getList(EFFECTS_KEY, Tag.TAG_COMPOUND);
        if (storedEffects.isEmpty()) {
            persistentData.remove(EFFECTS_KEY);
            return 0.0D;
        }

        ListTag activeEffects = new ListTag();
        double total = 0.0D;
        for (Tag tag : storedEffects) {
            if (!(tag instanceof CompoundTag entry) || entry.getLong(UNTIL_KEY) <= gameTime) {
                continue;
            }

            CyberwareEffectType type;
            try {
                type = CyberwareEffectType.valueOf(entry.getString(TYPE_KEY));
            } catch (IllegalArgumentException ignored) {
                continue;
            }

            if (type == targetType) {
                total += entry.getDouble(AMOUNT_KEY);
            }
            activeEffects.add(entry.copy());
        }

        if (activeEffects.isEmpty()) {
            persistentData.remove(EFFECTS_KEY);
        } else {
            persistentData.put(EFFECTS_KEY, activeEffects);
        }
        return total;
    }

    public static void copyState(Player fromPlayer, Player toPlayer) {
        CompoundTag fromData = fromPlayer.getPersistentData();
        CompoundTag toData = toPlayer.getPersistentData();
        if (fromData.contains(EFFECTS_KEY, Tag.TAG_LIST)) {
            toData.put(EFFECTS_KEY, fromData.getList(EFFECTS_KEY, Tag.TAG_COMPOUND).copy());
        } else {
            toData.remove(EFFECTS_KEY);
        }
    }

    public static void clearEffects(Player player) {
        player.getPersistentData().remove(EFFECTS_KEY);
    }

    public static boolean hasStoredEffects(Player player) {
        return !player.getPersistentData().getList(EFFECTS_KEY, Tag.TAG_COMPOUND).isEmpty();
    }

    public static List<ActiveEffectStatus> collectActiveStatuses(Player player, long gameTime) {
        CompoundTag persistentData = player.getPersistentData();
        ListTag storedEffects = persistentData.getList(EFFECTS_KEY, Tag.TAG_COMPOUND);
        if (storedEffects.isEmpty()) {
            persistentData.remove(EFFECTS_KEY);
            return List.of();
        }

        ListTag activeEffects = new ListTag();
        EnumMap<CyberwareEffectType, Double> totals = new EnumMap<>(CyberwareEffectType.class);
        EnumMap<CyberwareEffectType, Integer> remainingSeconds = new EnumMap<>(CyberwareEffectType.class);
        EnumMap<CyberwareEffectType, Integer> totalSeconds = new EnumMap<>(CyberwareEffectType.class);
        for (Tag tag : storedEffects) {
            if (!(tag instanceof CompoundTag entry) || entry.getLong(UNTIL_KEY) <= gameTime) {
                continue;
            }

            CyberwareEffectType type;
            try {
                type = CyberwareEffectType.valueOf(entry.getString(TYPE_KEY));
            } catch (IllegalArgumentException ignored) {
                continue;
            }

            totals.merge(type, entry.getDouble(AMOUNT_KEY), type.isMobEffect() ? Math::max : Double::sum);
            remainingSeconds.merge(type, (int) ((entry.getLong(UNTIL_KEY) - gameTime + 19L) / 20L), Math::max);
            long storedDuration = entry.contains(DURATION_KEY) ? entry.getLong(DURATION_KEY) : Math.max(20L, entry.getLong(UNTIL_KEY) - gameTime);
            totalSeconds.merge(type, (int) Math.max(1L, (storedDuration + 19L) / 20L), Math::max);
            activeEffects.add(entry.copy());
        }

        if (activeEffects.isEmpty()) {
            persistentData.remove(EFFECTS_KEY);
            return List.of();
        }
        persistentData.put(EFFECTS_KEY, activeEffects);

        List<ActiveEffectStatus> statuses = new ArrayList<>(totals.size());
        for (var entry : totals.entrySet()) {
            statuses.add(new ActiveEffectStatus(
                    entry.getKey(),
                    entry.getValue(),
                    remainingSeconds.getOrDefault(entry.getKey(), 0),
                    totalSeconds.getOrDefault(entry.getKey(), remainingSeconds.getOrDefault(entry.getKey(), 0))
            ));
        }
        return statuses;
    }
}
