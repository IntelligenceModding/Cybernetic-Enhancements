package de.artemis.cyberneticenhancements.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import de.artemis.cyberneticenhancements.common.cyberware.CyberstrainManager;
import de.artemis.cyberneticenhancements.common.cyberware.PlayerCyberwareInventory;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class CyberpsychosisCommand {
    private CyberpsychosisCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("cyberneticenhancements")
                        .requires(source -> source.hasPermission(Commands.LEVEL_ALL))
                        .then(Commands.literal("cyberpsychosis")
                                .then(Commands.literal("trigger")
                                        .executes(context -> trigger(context.getSource(), 15))
                                        .then(Commands.argument("seconds", IntegerArgumentType.integer(5, 120))
                                                .executes(context -> trigger(context.getSource(), IntegerArgumentType.getInteger(context, "seconds")))))
                                .then(Commands.literal("clear")
                                        .executes(context -> clear(context.getSource())))
                                .then(Commands.literal("status")
                                        .executes(context -> status(context.getSource()))))
        );
    }

    private static int trigger(CommandSourceStack source, int durationSeconds) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        CyberstrainManager.forcePsychosisEpisode(player, durationSeconds);
        source.sendSuccess(() -> Component.literal("Forced cyberpsychosis for " + durationSeconds + " seconds."), false);
        return 1;
    }

    private static int clear(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        CyberstrainManager.clearPsychosis(player);
        source.sendSuccess(() -> Component.literal("Cleared cyberpsychosis state."), false);
        return 1;
    }

    private static int status(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        PlayerCyberwareInventory inventory = new PlayerCyberwareInventory(player);
        source.sendSuccess(() -> Component.literal("Psychosis state: " + CyberstrainManager.getPsychosisStateLabel(player, inventory)), false);
        source.sendSuccess(() -> Component.literal("Episode active: " + CyberstrainManager.isPsychosisActive(player)), false);
        source.sendSuccess(() -> Component.literal("Episode remaining: " + CyberstrainManager.getPsychosisSecondsRemaining(player) + " s"), false);
        source.sendSuccess(() -> Component.literal("Effective cyberstrain: " + CyberstrainManager.getEffectiveCyberstrain(player, inventory)), false);
        return 1;
    }
}
