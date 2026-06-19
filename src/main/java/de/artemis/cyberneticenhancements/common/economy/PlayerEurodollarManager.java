package de.artemis.cyberneticenhancements.common.economy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public final class PlayerEurodollarManager {
    private static final String EURODOLLARS_KEY = "cyberneticenhancements.eurodollars";
    private static final int MAX_BALANCE = 999_999_999;

    private PlayerEurodollarManager() {
    }

    public static int balance(Player player) {
        int stored = player.getPersistentData().getInt(EURODOLLARS_KEY);
        return clampBalance(stored);
    }

    public static void setBalance(Player player, int amount) {
        player.getPersistentData().putInt(EURODOLLARS_KEY, clampBalance(amount));
    }

    public static int add(Player player, int amount) {
        if (amount <= 0) {
            return balance(player);
        }
        int updated = clampBalance(balance(player) + amount);
        setBalance(player, updated);
        return updated;
    }

    public static int remove(Player player, int amount) {
        if (amount <= 0) {
            return balance(player);
        }
        int updated = Math.max(0, balance(player) - amount);
        setBalance(player, updated);
        return updated;
    }

    public static int change(Player player, int delta) {
        if (delta == 0) {
            return balance(player);
        }
        if (delta > 0) {
            return add(player, delta);
        }
        return remove(player, -delta);
    }

    public static boolean canAfford(Player player, int amount) {
        return amount <= 0 || balance(player) >= amount;
    }

    public static boolean tryWithdraw(Player player, int amount) {
        if (amount <= 0) {
            return true;
        }
        if (!canAfford(player, amount)) {
            return false;
        }
        remove(player, amount);
        return true;
    }

    public static void copyState(Player fromPlayer, Player toPlayer) {
        CompoundTag fromData = fromPlayer.getPersistentData();
        CompoundTag toData = toPlayer.getPersistentData();
        if (fromData.contains(EURODOLLARS_KEY)) {
            toData.putInt(EURODOLLARS_KEY, clampBalance(fromData.getInt(EURODOLLARS_KEY)));
        } else {
            toData.remove(EURODOLLARS_KEY);
        }
    }

    private static int clampBalance(int amount) {
        return Math.max(0, Math.min(MAX_BALANCE, amount));
    }
}
