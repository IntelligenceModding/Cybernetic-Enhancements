package de.artemis.cyberneticenhancements.common.cyberware;

import de.artemis.cyberneticenhancements.common.item.ChipwareItem;
import de.artemis.cyberneticenhancements.common.item.CyberwareItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.items.IItemHandlerModifiable;

public final class ChipwareSocketHandler implements IItemHandlerModifiable {
    private static final String CHIPWARE_KEY = "InstalledChipware";
    private static final String SLOT_KEY = "Slot";
    private static final String STACK_KEY = "Stack";
    public static final int MAX_CHIP_SLOTS = 3;

    private final PlayerCyberwareInventory cyberwareInventory;
    private final Player player;
    private final int parentSlot;

    public ChipwareSocketHandler(PlayerCyberwareInventory cyberwareInventory, Player player, int parentSlot) {
        this.cyberwareInventory = cyberwareInventory;
        this.player = player;
        this.parentSlot = parentSlot;
    }

    @Override
    public int getSlots() {
        return MAX_CHIP_SLOTS;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        validateSlot(slot);
        if (!isSlotUnlocked(slot)) {
            return ItemStack.EMPTY;
        }
        return readChips().get(slot);
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
            NonNullList<ItemStack> chips = readChips();
            chips.set(slot, stack.copyWithCount(1));
            writeChips(chips);
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
            NonNullList<ItemStack> chips = readChips();
            chips.set(slot, ItemStack.EMPTY);
            writeChips(chips);
        }
        return current.copyWithCount(1);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return isSlotUnlocked(slot) && stack.getItem() instanceof ChipwareItem;
    }

    @Override
    public void setStackInSlot(int slot, ItemStack stack) {
        validateSlot(slot);
        NonNullList<ItemStack> chips = readChips();
        chips.set(slot, stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1));
        writeChips(chips);
    }

    public boolean isSlotUnlocked(int slot) {
        return slot >= 0 && slot < getUnlockedSlotCount();
    }

    public int getUnlockedSlotCount() {
        CyberwareDefinition definition = getHostDefinition();
        return definition == null ? 0 : definition.chipSlotCount();
    }

    public String getHostDisplayName() {
        CyberwareDefinition hostDefinition = getHostDefinition();
        if (hostDefinition == null) {
            return "";
        }

        ItemStack parentStack = getParentStack();
        return parentStack.isEmpty() ? "" : parentStack.getHoverName().getString();
    }

    private ItemStack getParentStack() {
        if (parentSlot < 0) {
            for (CyberwareSlot slot : CyberwareSlot.values()) {
                if (slot.getType() != CyberwareSlotType.FRONTAL_CORTEX) {
                    continue;
                }

                ItemStack stack = cyberwareInventory.getStackInSlot(slot.ordinal());
                if (stack.getItem() instanceof CyberwareItem cyberwareItem && cyberwareItem.getDefinition().supportsChipware()) {
                    return stack;
                }
            }
            return ItemStack.EMPTY;
        }
        return cyberwareInventory.getStackInSlot(parentSlot);
    }

    private CyberwareDefinition getHostDefinition() {
        ItemStack parentStack = getParentStack();
        if (!(parentStack.getItem() instanceof CyberwareItem cyberwareItem) || !cyberwareItem.getDefinition().supportsChipware()) {
            return null;
        }
        return cyberwareItem.getDefinition();
    }

    private NonNullList<ItemStack> readChips() {
        NonNullList<ItemStack> chips = NonNullList.withSize(MAX_CHIP_SLOTS, ItemStack.EMPTY);
        if (getHostDefinition() == null) {
            return chips;
        }

        ItemStack parentStack = getParentStack();
        if (parentStack.isEmpty()) {
            return chips;
        }

        CompoundTag tag = parentStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!tag.contains(CHIPWARE_KEY, Tag.TAG_LIST)) {
            return chips;
        }

        ListTag entries = tag.getList(CHIPWARE_KEY, Tag.TAG_COMPOUND);
        for (Tag entry : entries) {
            if (!(entry instanceof CompoundTag entryTag) || !entryTag.contains(SLOT_KEY, Tag.TAG_INT) || !entryTag.contains(STACK_KEY, Tag.TAG_COMPOUND)) {
                continue;
            }

            int slot = entryTag.getInt(SLOT_KEY);
            if (slot < 0 || slot >= MAX_CHIP_SLOTS) {
                continue;
            }

            chips.set(slot, ItemStack.parseOptional(player.level().registryAccess(), entryTag.getCompound(STACK_KEY)));
        }
        return chips;
    }

    private void writeChips(NonNullList<ItemStack> chips) {
        if (getHostDefinition() == null) {
            return;
        }

        ItemStack parentStack = getParentStack();
        if (parentStack.isEmpty()) {
            return;
        }

        CompoundTag tag = parentStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        ListTag entries = new ListTag();
        for (int slot = 0; slot < chips.size(); slot++) {
            ItemStack chipStack = chips.get(slot);
            if (chipStack.isEmpty()) {
                continue;
            }

            CompoundTag entryTag = new CompoundTag();
            entryTag.putInt(SLOT_KEY, slot);
            entryTag.put(STACK_KEY, chipStack.save(player.level().registryAccess(), new CompoundTag()));
            entries.add(entryTag);
        }

        if (entries.isEmpty()) {
            tag.remove(CHIPWARE_KEY);
        } else {
            tag.put(CHIPWARE_KEY, entries);
        }

        if (tag.isEmpty()) {
            parentStack.remove(DataComponents.CUSTOM_DATA);
        } else {
            parentStack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }

        cyberwareInventory.save();
    }

    private static void validateSlot(int slot) {
        if (slot < 0 || slot >= MAX_CHIP_SLOTS) {
            throw new IllegalArgumentException("Chip slot out of range: " + slot);
        }
    }
}
