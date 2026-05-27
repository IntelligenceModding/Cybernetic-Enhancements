package de.artemis.cyberneticenhancements.common.item;

import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableManager;
import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableDefinition;
import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import java.util.List;

public final class CyberConsumableItem extends Item {
    private final CyberConsumableDefinition definition;

    public CyberConsumableItem(Properties properties, CyberConsumableDefinition definition) {
        super(properties.stacksTo(16).rarity(definition.rarity()));
        this.definition = definition;
    }

    public CyberConsumableDefinition getDefinition() {
        return definition;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        ItemStack itemStack = player.getItemInHand(usedHand);
        if (!CyberConsumableManager.canConsume(player, definition)) {
            if (!level.isClientSide()) {
                player.displayClientMessage(Component.translatable("message.cyberneticenhancements.consumable.cooldown", definition.displayName()).withStyle(ChatFormatting.RED), true);
            }
            return InteractionResultHolder.fail(itemStack);
        }

        player.startUsingItem(usedHand);
        return InteractionResultHolder.consume(itemStack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (livingEntity instanceof Player player) {
            if (!level.isClientSide()) {
                definition.profile().apply(player);
                CyberConsumableManager.onConsumed(player, definition);
                player.awardStat(Stats.ITEM_USED.get(this));
            }

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }

        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 24;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable(definition.descriptionKey()).withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable("tooltip.cyberneticenhancements.consumable_category", Component.translatable(definition.category().translationKey()))
                .withStyle(ChatFormatting.DARK_AQUA));
        tooltipComponents.add(Component.translatable("tooltip.cyberneticenhancements.consumable_cooldown", definition.cooldownTicks() / 20)
                .withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(Component.translatable("tooltip.cyberneticenhancements.consumable_overdose", CyberConsumableTooltip.overdoseRating(definition.overdosePoints()))
                .withStyle(definition.overdosePoints() >= 3 ? ChatFormatting.RED : definition.overdosePoints() >= 1 ? ChatFormatting.GOLD : ChatFormatting.GREEN));
    }
}
