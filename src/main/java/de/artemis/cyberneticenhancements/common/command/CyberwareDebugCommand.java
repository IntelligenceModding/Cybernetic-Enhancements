package de.artemis.cyberneticenhancements.common.command;

import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableManager;
import com.mojang.brigadier.CommandDispatcher;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareSlot;
import de.artemis.cyberneticenhancements.common.cyberware.CyberstrainManager;
import de.artemis.cyberneticenhancements.common.cyberware.PlayerCyberwareInventory;
import de.artemis.cyberneticenhancements.common.item.CyberwareItem;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class CyberwareDebugCommand {
    private CyberwareDebugCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("cyberneticenhancements")
                        .requires(source -> source.hasPermission(Commands.LEVEL_ALL))
                        .then(Commands.literal("debug")
                                .then(Commands.literal("cyberware")
                                        .executes(context -> execute(context.getSource()))))
        );
    }

    private static int execute(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        PlayerCyberwareInventory inventory = new PlayerCyberwareInventory(player);
        CompoundTag storedTag = PlayerCyberwareInventory.getStoredInventoryTag(player);

        source.sendSuccess(() -> Component.literal("=== Cyberware Debug ==="), false);
        source.sendSuccess(() -> Component.literal("Installed count: " + inventory.getInstalledCount()), false);
        source.sendSuccess(() -> Component.literal("Chrome: " + inventory.getInstalledChromeCost() + " / " + inventory.getChromeCapacity()), false);
        source.sendSuccess(() -> Component.literal("Cyberstrain: " + inventory.getCyberstrain()), false);
        source.sendSuccess(() -> Component.literal("Effective cyberstrain: " + CyberstrainManager.getEffectiveCyberstrain(player, inventory)), false);
        source.sendSuccess(() -> Component.literal("Psychosis state: " + CyberstrainManager.getPsychosisStateLabel(player, inventory)), false);
        source.sendSuccess(() -> Component.literal("Psychosis active: " + CyberstrainManager.isPsychosisActive(player)), false);
        source.sendSuccess(() -> Component.literal("Psychosis remaining: " + CyberstrainManager.getPsychosisSecondsRemaining(player) + " s"), false);
        source.sendSuccess(() -> Component.literal("Suppression: " + CyberstrainManager.getSuppressionAmount(player)), false);
        source.sendSuccess(() -> Component.literal("Overdose: " + CyberConsumableManager.getOverdosePoints(player)), false);
        source.sendSuccess(() -> Component.literal("Live slot count: " + inventory.getSlots()), false);
        source.sendSuccess(() -> Component.literal("Stored tag empty: " + storedTag.isEmpty()), false);
        source.sendSuccess(() -> Component.literal("Stored tag: " + storedTag), false);

        boolean anyInstalled = false;
        for (CyberwareSlot slot : CyberwareSlot.values()) {
            if (slot.ordinal() >= inventory.getSlots()) {
                String line = slot.name() + " -> missing from live handler (slot count mismatch)";
                source.sendSuccess(() -> Component.literal(line), false);
                continue;
            }

            ItemStack stack = inventory.getStackInSlot(slot.ordinal());
            if (stack.getItem() instanceof CyberwareItem cyberwareItem) {
                anyInstalled = true;
                String line = slot.name() + " -> " + cyberwareItem.getDefinition().id() + " x" + stack.getCount();
                source.sendSuccess(() -> Component.literal(line), false);
            }
        }

        if (!anyInstalled) {
            source.sendSuccess(() -> Component.literal("No installed cyberware found in live inventory wrapper."), false);
        }

        return 1;
    }
}
