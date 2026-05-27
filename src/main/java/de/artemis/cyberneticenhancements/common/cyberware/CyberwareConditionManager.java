package de.artemis.cyberneticenhancements.common.cyberware;

import de.artemis.cyberneticenhancements.common.item.CyberwareItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public final class CyberwareConditionManager {
    private static final String LAST_WEAR_TICK_KEY = "cyberneticenhancements.last_cyberware_wear_tick";
    private static final long WEAR_INTERVAL_TICKS = 1200L;

    private CyberwareConditionManager() {
    }

    public static void onPlayerTick(Player player) {
        if (player.level().isClientSide()) {
            return;
        }

        long gameTime = player.level().getGameTime();
        long lastWearTick = player.getPersistentData().getLong(LAST_WEAR_TICK_KEY);
        if (lastWearTick == 0L) {
            player.getPersistentData().putLong(LAST_WEAR_TICK_KEY, gameTime);
            return;
        }
        if (gameTime - lastWearTick < WEAR_INTERVAL_TICKS) {
            return;
        }

        player.getPersistentData().putLong(LAST_WEAR_TICK_KEY, gameTime);
        PlayerCyberwareInventory inventory = new PlayerCyberwareInventory(player);
        int wearAmount = 1 + Math.max(0, inventory.getCyberstrain() / 20);
        if (CyberstrainManager.isPsychosisActive(player, gameTime)) {
            wearAmount += 1;
        }

        boolean changed = false;
        for (int slot = 0; slot < inventory.getSlots(); slot++) {
            ItemStack stack = inventory.getStackInSlot(slot);
            if (!(stack.getItem() instanceof CyberwareItem cyberwareItem)) {
                continue;
            }
            changed |= CyberwareConditionHelper.damage(stack, cyberwareItem.getDefinition(), wearAmount);
        }

        if (changed) {
            inventory.save();
        }
    }
}
