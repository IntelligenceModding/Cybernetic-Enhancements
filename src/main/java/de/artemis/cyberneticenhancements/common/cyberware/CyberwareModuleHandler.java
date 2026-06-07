package de.artemis.cyberneticenhancements.common.cyberware;

import de.artemis.cyberneticenhancements.common.item.CyberwareItem;
import de.artemis.cyberneticenhancements.common.item.CyberwareModuleItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public final class CyberwareModuleHandler implements IItemHandlerModifiable {
    private static final String MODULES_KEY = "InstalledModules";
    private static final String SUPPORTED_TIERS_KEY = "SupportedModuleTiers";
    private static final String SLOT_KEY = "Slot";
    private static final String STACK_KEY = "Stack";
    private static final String TIER_KEY = "Tier";
    public static final int MAX_MODULE_SLOTS = 3;

    private final PlayerCyberwareInventory cyberwareInventory;
    private final Player player;
    private final int parentSlot;
    private final CyberwareModuleCategory category;

    public CyberwareModuleHandler(PlayerCyberwareInventory cyberwareInventory, Player player, int parentSlot, CyberwareModuleCategory category) {
        this.cyberwareInventory = cyberwareInventory;
        this.player = player;
        this.parentSlot = parentSlot;
        this.category = category;
    }

    @Override
    public int getSlots() {
        return MAX_MODULE_SLOTS;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlot(slot);
        if (!isSlotUnlocked(slot)) {
            return ItemStack.EMPTY;
        }
        return readModules().get(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        validateSlot(slot);
        if (stack.isEmpty() || !isItemValid(slot, stack) || !getStackInSlot(slot).isEmpty()) {
            return stack;
        }

        ItemStack remaining = stack.copy();
        remaining.shrink(1);
        if (!simulate) {
            NonNullList<ItemStack> modules = readModules();
            modules.set(slot, stack.copyWithCount(1));
            writeModules(modules);
        }
        return remaining;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        validateSlot(slot);
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }

        ItemStack current = getStackInSlot(slot);
        if (current.isEmpty()) {
            return ItemStack.EMPTY;
        }

        if (!simulate) {
            NonNullList<ItemStack> modules = readModules();
            modules.set(slot, ItemStack.EMPTY);
            writeModules(modules);
        }

        return current.copyWithCount(1);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (!isSlotUnlocked(slot) || !(stack.getItem() instanceof CyberwareModuleItem moduleItem)) {
            return false;
        }
        return moduleItem.getDefinition().category() == category
                && moduleItem.getDefinition().tier().ordinal() <= getSupportedTier(slot).ordinal();
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        validateSlot(slot);
        NonNullList<ItemStack> modules = readModules();
        modules.set(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1));
        writeModules(modules);
    }

    public boolean isSlotUnlocked(int slot) {
        return slot >= 0 && slot < getUnlockedSlotCount();
    }

    public int getUnlockedSlotCount() {
        ItemStack parentStack = getParentStack();
        if (!(parentStack.getItem() instanceof CyberwareItem cyberwareItem)) {
            return 0;
        }
        CyberwareDefinition definition = cyberwareItem.getDefinition();
        return definition.supportsModules() && definition.moduleCategory() == category
                ? CyberwareUpgradeHelper.getModuleSlotCount(parentStack, definition)
                : 0;
    }

    public String getHostDisplayName() {
        ItemStack parentStack = getParentStack();
        return parentStack.isEmpty() ? "" : parentStack.getHoverName().getString();
    }

    public CyberwareTier getSupportedTier(int slot) {
        validateSlot(slot);
        return readSupportedTiers()[slot];
    }

    public boolean canUpgradeSupportedTier(int slot) {
        validateSlot(slot);
        return isSlotUnlocked(slot)
                && getStackInSlot(slot).isEmpty()
                && getSupportedTier(slot) != CyberwareTier.TIER_5;
    }

    public CyberwareTier getNextSupportedTier(int slot) {
        CyberwareTier currentTier = getSupportedTier(slot);
        return currentTier == CyberwareTier.TIER_5 ? CyberwareTier.TIER_5 : CyberwareTier.values()[currentTier.ordinal() + 1];
    }

    public ItemStack getRequiredUpgradeComponentStack(int slot) {
        return PlayerCyberwareInventory.getRequiredUpgradeComponentStack(getSupportedTier(slot));
    }

    public boolean hasRequiredUpgradeComponent(int slot) {
        return cyberwareInventory.hasRequiredUpgradeComponent(getRequiredUpgradeComponentStack(slot));
    }

    public boolean tryUpgradeSupportedTier(int slot) {
        if (!canUpgradeSupportedTier(slot)) {
            return false;
        }

        ItemStack required = getRequiredUpgradeComponentStack(slot);
        if (!cyberwareInventory.consumeRequiredUpgradeComponent(required)) {
            return false;
        }

        CyberwareTier[] supportedTiers = readSupportedTiers();
        supportedTiers[slot] = getNextSupportedTier(slot);
        writeSupportedTiers(supportedTiers);
        return true;
    }

    public static NonNullList<ItemStack> getStoredModules(ItemStack parentStack, HolderLookup.Provider registries) {
        NonNullList<ItemStack> modules = NonNullList.withSize(MAX_MODULE_SLOTS, ItemStack.EMPTY);
        if (parentStack.isEmpty()) {
            return modules;
        }

        CompoundTag tag = parentStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains(MODULES_KEY, Tag.TAG_LIST)) {
            return modules;
        }

        ListTag entries = tag.getList(MODULES_KEY, Tag.TAG_COMPOUND);
        for (Tag entry : entries) {
            if (!(entry instanceof CompoundTag entryTag) || !entryTag.contains(SLOT_KEY, Tag.TAG_INT) || !entryTag.contains(STACK_KEY, Tag.TAG_COMPOUND)) {
                continue;
            }

            int slot = entryTag.getInt(SLOT_KEY);
            if (slot < 0 || slot >= MAX_MODULE_SLOTS) {
                continue;
            }

            ItemStack moduleStack = ItemStack.parseOptional(registries, entryTag.getCompound(STACK_KEY));
            if (!moduleStack.isEmpty()) {
                modules.set(slot, moduleStack);
            }
        }
        return modules;
    }

    private ItemStack getParentStack() {
        return cyberwareInventory.getStackInSlot(parentSlot);
    }

    private CyberwareDefinition getHostDefinition() {
        ItemStack parentStack = getParentStack();
        if (!(parentStack.getItem() instanceof CyberwareItem cyberwareItem) || !cyberwareItem.getDefinition().supportsModules()) {
            return null;
        }
        return cyberwareItem.getDefinition();
    }

    private NonNullList<ItemStack> readModules() {
        ItemStack parentStack = getParentStack();
        NonNullList<ItemStack> modules = NonNullList.withSize(MAX_MODULE_SLOTS, ItemStack.EMPTY);
        if (parentStack.isEmpty()) {
            return modules;
        }
        return getStoredModules(parentStack, player.level().registryAccess());
    }

    private void writeModules(NonNullList<ItemStack> modules) {
        ItemStack parentStack = getParentStack();
        if (parentStack.isEmpty()) {
            return;
        }

        CompoundTag tag = parentStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        ListTag entries = new ListTag();
        for (int slot = 0; slot < modules.size(); slot++) {
            ItemStack moduleStack = modules.get(slot);
            if (moduleStack.isEmpty()) {
                continue;
            }

            CompoundTag entryTag = new CompoundTag();
            entryTag.putInt(SLOT_KEY, slot);
            entryTag.put(STACK_KEY, moduleStack.save(player.level().registryAccess(), new CompoundTag()));
            entries.add(entryTag);
        }

        if (entries.isEmpty()) {
            tag.remove(MODULES_KEY);
        } else {
            tag.put(MODULES_KEY, entries);
        }

        if (tag.isEmpty()) {
            parentStack.remove(DataComponents.CUSTOM_DATA);
        } else {
            parentStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }

        cyberwareInventory.save();
    }

    private CyberwareTier[] readSupportedTiers() {
        CyberwareTier[] supportedTiers = createDefaultSupportedTiers();
        ItemStack parentStack = getParentStack();
        if (parentStack.isEmpty()) {
            return supportedTiers;
        }

        CompoundTag tag = parentStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains(SUPPORTED_TIERS_KEY, Tag.TAG_LIST)) {
            return supportedTiers;
        }

        ListTag entries = tag.getList(SUPPORTED_TIERS_KEY, Tag.TAG_COMPOUND);
        for (Tag entry : entries) {
            if (!(entry instanceof CompoundTag entryTag) || !entryTag.contains(SLOT_KEY, Tag.TAG_INT) || !entryTag.contains(TIER_KEY, Tag.TAG_INT)) {
                continue;
            }

            int slot = entryTag.getInt(SLOT_KEY);
            int tierOrdinal = entryTag.getInt(TIER_KEY);
            if (slot < 0 || slot >= MAX_MODULE_SLOTS || tierOrdinal < 0 || tierOrdinal >= CyberwareTier.values().length) {
                continue;
            }

            supportedTiers[slot] = CyberwareTier.values()[tierOrdinal];
        }
        return supportedTiers;
    }

    private void writeSupportedTiers(CyberwareTier[] supportedTiers) {
        ItemStack parentStack = getParentStack();
        if (parentStack.isEmpty()) {
            return;
        }

        CompoundTag tag = parentStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        ListTag entries = new ListTag();
        for (int slot = 0; slot < supportedTiers.length; slot++) {
            if (supportedTiers[slot] == CyberwareTier.TIER_1) {
                continue;
            }

            CompoundTag entryTag = new CompoundTag();
            entryTag.putInt(SLOT_KEY, slot);
            entryTag.putInt(TIER_KEY, supportedTiers[slot].ordinal());
            entries.add(entryTag);
        }

        if (entries.isEmpty()) {
            tag.remove(SUPPORTED_TIERS_KEY);
        } else {
            tag.put(SUPPORTED_TIERS_KEY, entries);
        }

        if (tag.isEmpty()) {
            parentStack.remove(DataComponents.CUSTOM_DATA);
        } else {
            parentStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }

        cyberwareInventory.save();
    }

    private static CyberwareTier[] createDefaultSupportedTiers() {
        CyberwareTier[] supportedTiers = new CyberwareTier[MAX_MODULE_SLOTS];
        for (int slot = 0; slot < MAX_MODULE_SLOTS; slot++) {
            supportedTiers[slot] = CyberwareTier.TIER_1;
        }
        return supportedTiers;
    }

    private static void validateSlot(int slot) {
        if (slot < 0 || slot >= MAX_MODULE_SLOTS) {
            throw new IllegalArgumentException("Module slot out of range: " + slot);
        }
    }
}
