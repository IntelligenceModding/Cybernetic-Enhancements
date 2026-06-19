package de.artemis.cyberneticenhancements.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableManager;
import de.artemis.cyberneticenhancements.common.cyberware.ArmCyberwareManager;
import de.artemis.cyberneticenhancements.common.cyberware.CirculatoryCyberwareManager;
import de.artemis.cyberneticenhancements.common.cyberware.CombatStatusType;
import de.artemis.cyberneticenhancements.common.cyberware.CombatStatusManager;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareAbilities;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareEffects;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareSlot;
import de.artemis.cyberneticenhancements.common.cyberware.CyberstrainManager;
import de.artemis.cyberneticenhancements.common.cyberware.FaceCyberwareManager;
import de.artemis.cyberneticenhancements.common.cyberware.FrontalCortexManager;
import de.artemis.cyberneticenhancements.common.cyberware.HandsCyberwareManager;
import de.artemis.cyberneticenhancements.common.cyberware.IntegumentaryCyberwareManager;
import de.artemis.cyberneticenhancements.common.cyberware.LegCyberwareManager;
import de.artemis.cyberneticenhancements.common.cyberware.NervousSystemCyberwareManager;
import de.artemis.cyberneticenhancements.common.cyberware.PlayerCyberwareInventory;
import de.artemis.cyberneticenhancements.common.cyberware.SkeletonCyberwareManager;
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
                .then(Commands.literal("applystatus")
                        .then(Commands.argument("type", StringArgumentType.word())
                                .executes(context -> applyCombatStatus(context.getSource(),
                                        StringArgumentType.getString(context, "type"), 1, 8, true))
                                .then(Commands.argument("stacks", IntegerArgumentType.integer(1, 3))
                                        .executes(context -> applyCombatStatus(context.getSource(),
                                                StringArgumentType.getString(context, "type"),
                                                IntegerArgumentType.getInteger(context, "stacks"),
                                                8,
                                                true))
                                        .then(Commands.argument("seconds", IntegerArgumentType.integer(1, 120))
                                                .executes(context -> applyCombatStatus(context.getSource(),
                                                        StringArgumentType.getString(context, "type"),
                                                        IntegerArgumentType.getInteger(context, "stacks"),
                                                        IntegerArgumentType.getInteger(context, "seconds"),
                                                        true))
                                                .then(Commands.argument("source", StringArgumentType.word())
                                                        .executes(context -> applyCombatStatus(context.getSource(),
                                                                StringArgumentType.getString(context, "type"),
                                                                IntegerArgumentType.getInteger(context, "stacks"),
                                                                IntegerArgumentType.getInteger(context, "seconds"),
                                                                !"ambient".equalsIgnoreCase(StringArgumentType.getString(context, "source")))))))))
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

        source.sendSuccess(() -> Component.translatable("command.cyberneticenhancements.debug.header"), false);
        source.sendSuccess(() -> Component.translatable("command.cyberneticenhancements.debug.installed_count", inventory.getInstalledCount()), false);
        source.sendSuccess(() -> Component.translatable("command.cyberneticenhancements.debug.chrome", inventory.getInstalledChromeCost(), inventory.getChromeCapacity()), false);
        source.sendSuccess(() -> Component.translatable("command.cyberneticenhancements.debug.cyberstrain", inventory.getCyberstrain()), false);
        source.sendSuccess(() -> Component.translatable("command.cyberneticenhancements.debug.effective_cyberstrain", CyberstrainManager.getEffectiveCyberstrain(player, inventory)), false);
        source.sendSuccess(() -> Component.translatable("command.cyberneticenhancements.debug.psychosis_state", CyberstrainManager.getPsychosisStateLabel(player, inventory)), false);
        source.sendSuccess(() -> Component.translatable("command.cyberneticenhancements.debug.psychosis_active", CyberstrainManager.isPsychosisActive(player)), false);
        source.sendSuccess(() -> Component.translatable("command.cyberneticenhancements.debug.psychosis_remaining", CyberstrainManager.getPsychosisSecondsRemaining(player)), false);
        source.sendSuccess(() -> Component.translatable("command.cyberneticenhancements.debug.suppression", CyberstrainManager.getSuppressionAmount(player)), false);
        source.sendSuccess(() -> Component.translatable("command.cyberneticenhancements.debug.overdose", CyberConsumableManager.getOverdosePoints(player)), false);
        source.sendSuccess(() -> Component.translatable("command.cyberneticenhancements.debug.live_slot_count", inventory.getSlots()), false);
        source.sendSuccess(() -> Component.translatable("command.cyberneticenhancements.debug.stored_tag_empty", storedTag.isEmpty()), false);
        source.sendSuccess(() -> Component.translatable("command.cyberneticenhancements.debug.stored_tag", storedTag), false);

        boolean anyInstalled = false;
        for (CyberwareSlot slot : CyberwareSlot.values()) {
            if (slot.ordinal() >= inventory.getSlots()) {
                source.sendSuccess(() -> Component.translatable("command.cyberneticenhancements.debug.slot_missing", slot.name()), false);
                continue;
            }

            ItemStack stack = inventory.getStackInSlot(slot.ordinal());
            if (stack.getItem() instanceof CyberwareItem cyberwareItem) {
                anyInstalled = true;
                source.sendSuccess(() -> Component.translatable(
                        "command.cyberneticenhancements.debug.slot_installed",
                        slot.name(),
                        stack.getHoverName(),
                        stack.getCount()
                ), false);
            }
        }

        if (!anyInstalled) {
            source.sendSuccess(() -> Component.translatable("command.cyberneticenhancements.debug.no_installed"), false);
        }

        return 1;
    }

    private static int resetCooldowns(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        CyberConsumableManager.clearCooldowns(player);
        CyberwareAbilities.clearCooldowns(player);
        ArmCyberwareManager.clearCooldowns(player);
        CirculatoryCyberwareManager.clearCooldowns(player);
        FaceCyberwareManager.clearCooldowns(player);
        FrontalCortexManager.clearCooldowns(player);
        HandsCyberwareManager.clearCooldowns(player);
        IntegumentaryCyberwareManager.clearCooldowns(player);
        SkeletonCyberwareManager.clearCooldowns(player);
        NervousSystemCyberwareManager.clearCooldowns(player);
        LegCyberwareManager.clearCooldowns(player);
        CyberwareEffects.refreshPlayerCyberware(player);
        source.sendSuccess(() -> Component.translatable("command.cyberneticenhancements.debug.reset_cooldowns"), false);
        return 1;
    }

    private static int resetBuffs(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        TemporaryCyberwareEffectManager.clearEffects(player);
        CyberstrainManager.clearTemporaryStatuses(player);
        CombatStatusManager.clearStatuses(player);
        CyberwareEffects.refreshPlayerCyberware(player);
        source.sendSuccess(() -> Component.translatable("command.cyberneticenhancements.debug.reset_buffs"), false);
        return 1;
    }

    private static int resetAll(CommandSourceStack source) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        CyberConsumableManager.clearCooldowns(player);
        CyberwareAbilities.clearCooldowns(player);
        ArmCyberwareManager.clearCooldowns(player);
        CirculatoryCyberwareManager.clearCooldowns(player);
        FaceCyberwareManager.clearCooldowns(player);
        FrontalCortexManager.clearCooldowns(player);
        HandsCyberwareManager.clearCooldowns(player);
        IntegumentaryCyberwareManager.clearCooldowns(player);
        SkeletonCyberwareManager.clearCooldowns(player);
        NervousSystemCyberwareManager.clearCooldowns(player);
        LegCyberwareManager.clearCooldowns(player);
        TemporaryCyberwareEffectManager.clearEffects(player);
        CyberstrainManager.clearTemporaryStatuses(player);
        CombatStatusManager.clearStatuses(player);
        CyberwareEffects.refreshPlayerCyberware(player);
        source.sendSuccess(() -> Component.translatable("command.cyberneticenhancements.debug.reset_all"), false);
        return 1;
    }

    private static int applyCombatStatus(
            CommandSourceStack source,
            String rawType,
            int stacks,
            int seconds,
            boolean playerApplied
    ) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();
        CombatStatusType type = parseCombatStatusType(rawType);
        if (type == null) {
            source.sendFailure(Component.translatable("command.cyberneticenhancements.debug.unknown_status", rawType));
            return 0;
        }

        CombatStatusManager.applyStatus(player, type, seconds * 20L, stacks, playerApplied);
        source.sendSuccess(() -> Component.translatable("command.cyberneticenhancements.debug.applied_status", type.id(), stacks, seconds, playerApplied ? "player" : "ambient"), false);
        return 1;
    }

    private static CombatStatusType parseCombatStatusType(String rawType) {
        String normalized = rawType.trim().replace('-', '_').toUpperCase(java.util.Locale.ROOT);
        try {
            return CombatStatusType.valueOf(normalized);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
