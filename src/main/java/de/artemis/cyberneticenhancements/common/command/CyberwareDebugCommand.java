package de.artemis.cyberneticenhancements.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableManager;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareAbilities;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareEffects;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareSlot;
import de.artemis.cyberneticenhancements.common.cyberware.CyberstrainManager;
import de.artemis.cyberneticenhancements.common.cyberware.PlayerCyberwareInventory;
import de.artemis.cyberneticenhancements.common.cyberware.TemporaryCyberwareEffectManager;
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
        LiteralArgumentBuilder<CommandSourceStack> debugCommand = Commands.literal("debug")
                .then(Commands.literal("cyberware")
                        .executes(context -> execute(context.getSource())))
                .then(Commands.literal("reset")
                        .then(Commands.literal("cooldowns")
                                .executes(context -> resetCooldowns(context.getSource())))
                        .then(Commands.literal("buffs")
                                .executes(context -> resetBuffs(context.getSource())))
                        .then(Commands.literal("statuses")
                                .executes(context -> resetBuffs(context.getSource())))
                        .then(Commands.literal("all")
                                .executes(context -> resetAll(context.getSource())))
                        .then(Commands.literal("both")
                                .executes(context -> resetAll(context.getSource()))));

        dispatcher.register(
                Commands.literal("cyberneticenhancements")
                        .requires(source -> source.hasPermission(Commands.LEVEL_ALL))
                        .then(debugCommand)
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

    private static int resetCooldowns(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        CyberConsumableManager.clearCooldowns(player);
        CyberwareAbilities.clearCooldowns(player);
        CyberwareEffects.refreshPlayerCyberware(player);
        source.sendSuccess(() -> Component.literal("Cleared all cyberware and consumable cooldowns."), false);
        return 1;
    }

    private static int resetBuffs(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        TemporaryCyberwareEffectManager.clearEffects(player);
        CyberstrainManager.clearTemporaryStatuses(player);
        CyberwareEffects.refreshPlayerCyberware(player);
        source.sendSuccess(() -> Component.literal("Cleared all temporary cyberware statuses and buffs."), false);
        return 1;
    }

    private static int resetAll(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        CyberConsumableManager.clearCooldowns(player);
        CyberwareAbilities.clearCooldowns(player);
        TemporaryCyberwareEffectManager.clearEffects(player);
        CyberstrainManager.clearTemporaryStatuses(player);
        CyberwareEffects.refreshPlayerCyberware(player);
        source.sendSuccess(() -> Component.literal("Cleared all cooldowns and temporary cyberware statuses."), false);
        return 1;
    }
}
