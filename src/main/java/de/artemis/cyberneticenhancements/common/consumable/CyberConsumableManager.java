package de.artemis.cyberneticenhancements.common.consumable;

import de.artemis.cyberneticenhancements.common.item.CyberConsumableItem;
import de.artemis.cyberneticenhancements.common.registry.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

public final class CyberConsumableManager {
    private static final String OVERDOSE_POINTS_KEY = "cyberneticenhancements.consumable_overdose_points";
    private static final String OVERDOSE_DECAY_UNTIL_KEY = "cyberneticenhancements.consumable_overdose_decay_until";

    private CyberConsumableManager() {
    }

    public static boolean canConsume(Player player, CyberConsumableDefinition definition) {
        return !hasCategoryCooldown(player, definition.category());
    }

    public static void onConsumed(Player player, CyberConsumableDefinition definition) {
        long gameTime = player.level().getGameTime();
        applyCategoryCooldown(player, definition.category(), definition.cooldownTicks());
        addOverdosePoints(player, definition.overdosePoints(), gameTime);
        applyOverdoseEffects(player, gameTime);
        notifyUse(player, definition);
    }

    public static void onPlayerTick(Player player) {
        if (player.level().isClientSide()) {
            return;
        }

        long gameTime = player.level().getGameTime();
        if (player.tickCount % 20 != 0) {
            return;
        }

        decayOverdose(player, gameTime);
        applyOverdoseEffects(player, gameTime);
    }

    public static int getOverdosePoints(Player player) {
        cleanupExpiredOverdose(player, player.level().getGameTime());
        return player.getPersistentData().getInt(OVERDOSE_POINTS_KEY);
    }

    public static void copyState(Player fromPlayer, Player toPlayer) {
        CompoundTag fromData = fromPlayer.getPersistentData();
        CompoundTag toData = toPlayer.getPersistentData();
        copyInt(fromData, toData, OVERDOSE_POINTS_KEY);
        copyLong(fromData, toData, OVERDOSE_DECAY_UNTIL_KEY);
    }

    public static int getRemainingCategoryCooldownSeconds(Player player, CyberConsumableCategory category) {
        int remainingTicks = 0;
        for (var item : ModItems.consumableItems()) {
            CyberConsumableDefinition definition = item.get().getDefinition();
            if (definition.category() != category) {
                continue;
            }
            remainingTicks = Math.max(remainingTicks, player.getCooldowns().getCooldownPercent(item.get(), 0.0F) > 0.0F ? 1 : 0);
        }

        if (remainingTicks == 0) {
            return 0;
        }

        for (var item : ModItems.consumableItems()) {
            CyberConsumableDefinition definition = item.get().getDefinition();
            if (definition.category() == category && player.getCooldowns().isOnCooldown(item.get())) {
                return Math.max(1, definition.cooldownTicks() / 20);
            }
        }
        return 0;
    }

    private static boolean hasCategoryCooldown(Player player, CyberConsumableCategory category) {
        for (var item : ModItems.consumableItems()) {
            CyberConsumableItem consumableItem = item.get();
            if (consumableItem.getDefinition().category() == category && player.getCooldowns().isOnCooldown(consumableItem)) {
                return true;
            }
        }
        return false;
    }

    private static void applyCategoryCooldown(Player player, CyberConsumableCategory category, int cooldownTicks) {
        for (var item : ModItems.consumableItems()) {
            CyberConsumableItem consumableItem = item.get();
            if (consumableItem.getDefinition().category() == category) {
                player.getCooldowns().addCooldown(consumableItem, cooldownTicks);
            }
        }
    }

    private static void addOverdosePoints(Player player, int amount, long gameTime) {
        if (amount <= 0) {
            return;
        }

        CompoundTag persistentData = player.getPersistentData();
        int current = persistentData.getInt(OVERDOSE_POINTS_KEY);
        persistentData.putInt(OVERDOSE_POINTS_KEY, current + amount);
        persistentData.putLong(OVERDOSE_DECAY_UNTIL_KEY, Math.max(gameTime + 12_000L, persistentData.getLong(OVERDOSE_DECAY_UNTIL_KEY)));
    }

    private static void decayOverdose(Player player, long gameTime) {
        CompoundTag persistentData = player.getPersistentData();
        cleanupExpiredOverdose(player, gameTime);
        int current = persistentData.getInt(OVERDOSE_POINTS_KEY);
        if (current <= 0) {
            return;
        }

        persistentData.putInt(OVERDOSE_POINTS_KEY, Math.max(0, current - 1));
        if (persistentData.getInt(OVERDOSE_POINTS_KEY) == 0) {
            persistentData.remove(OVERDOSE_POINTS_KEY);
            persistentData.remove(OVERDOSE_DECAY_UNTIL_KEY);
        }
    }

    private static void cleanupExpiredOverdose(Player player, long gameTime) {
        CompoundTag persistentData = player.getPersistentData();
        if (persistentData.contains(OVERDOSE_DECAY_UNTIL_KEY) && persistentData.getLong(OVERDOSE_DECAY_UNTIL_KEY) <= gameTime) {
            persistentData.remove(OVERDOSE_POINTS_KEY);
            persistentData.remove(OVERDOSE_DECAY_UNTIL_KEY);
        }
    }

    private static void copyInt(CompoundTag fromData, CompoundTag toData, String key) {
        if (fromData.contains(key)) {
            toData.putInt(key, fromData.getInt(key));
        } else {
            toData.remove(key);
        }
    }

    private static void copyLong(CompoundTag fromData, CompoundTag toData, String key) {
        if (fromData.contains(key)) {
            toData.putLong(key, fromData.getLong(key));
        } else {
            toData.remove(key);
        }
    }

    private static void applyOverdoseEffects(Player player, long gameTime) {
        int overdose = getOverdosePoints(player);
        if (overdose >= 30) {
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 80, 0, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 0, true, false, false));
            if (gameTime % 100 == 0) {
                player.displayClientMessage(Component.translatable("message.cyberneticenhancements.consumable.overdose_major").withStyle(ChatFormatting.RED), true);
            }
        } else if (overdose >= 18) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 0, true, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 0, true, false, false));
            if (gameTime % 100 == 0) {
                player.displayClientMessage(Component.translatable("message.cyberneticenhancements.consumable.overdose_minor").withStyle(ChatFormatting.GOLD), true);
            }
        }
    }

    private static void notifyUse(Player player, CyberConsumableDefinition definition) {
        player.displayClientMessage(Component.translatable("message.cyberneticenhancements.consumable.used", definition.displayName()).withStyle(ChatFormatting.AQUA), true);
    }
}
