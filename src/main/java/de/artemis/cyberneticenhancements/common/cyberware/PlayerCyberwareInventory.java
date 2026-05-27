package de.artemis.cyberneticenhancements.common.cyberware;

import de.artemis.cyberneticenhancements.common.item.ChipwareItem;
import de.artemis.cyberneticenhancements.common.item.CyberwareItem;
import de.artemis.cyberneticenhancements.common.item.CyberwareModuleItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.ArrayList;
import java.util.List;

public final class PlayerCyberwareInventory extends ItemStackHandler {
    public static final int SLOT_COUNT = CyberwareSlot.values().length;
    public static final int BASE_CHROME_CAPACITY = 50;
    private static final String PERSISTENT_DATA_KEY = "cyberneticenhancements.cyberware_inventory";
    private static final String ENTRIES_KEY = "Entries";
    private static final String SLOT_KEY = "Slot";
    private static final String STACK_KEY = "Stack";

    private final Player player;
    private final ServerLevel serverLevel;
    private boolean suppressSave;

    public PlayerCyberwareInventory(Player player) {
        super(SLOT_COUNT);
        this.player = player;
        this.serverLevel = player.level() instanceof ServerLevel level ? level : null;
        if (serverLevel != null) {
            CompoundTag storedData = getStoredInventoryTag(player);
            loadStoredInventory(serverLevel.registryAccess(), storedData);
            if (!storedData.isEmpty() && !player.getPersistentData().contains(PERSISTENT_DATA_KEY, CompoundTag.TAG_COMPOUND)) {
                save();
            }
        }
    }

    public static CompoundTag getStoredInventoryTag(Player player) {
        CompoundTag persistentData = player.getPersistentData();
        if (persistentData.contains(PERSISTENT_DATA_KEY, CompoundTag.TAG_COMPOUND)) {
            return persistentData.getCompound(PERSISTENT_DATA_KEY).copy();
        }

        if (player.level() instanceof ServerLevel level) {
            return CyberwareSavedData.get(level).getInventory(player.getUUID());
        }

        return new CompoundTag();
    }

    public static void copyStoredInventory(Player fromPlayer, Player toPlayer) {
        CompoundTag storedData = getStoredInventoryTag(fromPlayer);
        CompoundTag targetPersistentData = toPlayer.getPersistentData();
        if (storedData.isEmpty()) {
            targetPersistentData.remove(PERSISTENT_DATA_KEY);
        } else {
            targetPersistentData.put(PERSISTENT_DATA_KEY, storedData.copy());
        }

        if (toPlayer.level() instanceof ServerLevel level) {
            CyberwareSavedData.get(level).setInventory(toPlayer.getUUID(), storedData);
        }
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (suppressSave) {
            return;
        }
        save();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return stack.getItem() instanceof CyberwareItem cyberwareItem
                && cyberwareItem.getSlotType() == CyberwareSlot.values()[slot].getType();
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    public void save() {
        if (serverLevel == null) {
            return;
        }

        CompoundTag serialized = saveStoredInventory(serverLevel.registryAccess());
        CompoundTag persistentData = player.getPersistentData();
        if (serialized.isEmpty()) {
            persistentData.remove(PERSISTENT_DATA_KEY);
        } else {
            persistentData.put(PERSISTENT_DATA_KEY, serialized.copy());
        }

        CyberwareSavedData.get(serverLevel).setInventory(player.getUUID(), serialized);
        CyberwareEffects.refreshPlayerCyberware(player);
    }

    private CompoundTag saveStoredInventory(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        ListTag entries = new ListTag();
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            ItemStack stack = getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }

            CompoundTag entryTag = new CompoundTag();
            entryTag.putInt(SLOT_KEY, slot);
            entryTag.put(STACK_KEY, stack.save(registries, new CompoundTag()));
            entries.add(entryTag);
        }

        if (!entries.isEmpty()) {
            tag.put(ENTRIES_KEY, entries);
        }
        return tag;
    }

    private void loadStoredInventory(HolderLookup.Provider registries, CompoundTag tag) {
        suppressSave = true;
        try {
            clearSlots();
            if (tag.isEmpty()) {
                return;
            }

            if (tag.contains(ENTRIES_KEY, Tag.TAG_LIST)) {
                loadCurrentFormat(registries, tag.getList(ENTRIES_KEY, Tag.TAG_COMPOUND));
                return;
            }

            if (tag.contains("Items", Tag.TAG_LIST)) {
                loadLegacyFormat(registries, tag);
            }
        } finally {
            suppressSave = false;
        }
    }

    private void loadCurrentFormat(HolderLookup.Provider registries, ListTag entries) {
        for (Tag entry : entries) {
            if (!(entry instanceof CompoundTag entryTag) || !entryTag.contains(SLOT_KEY, Tag.TAG_INT)) {
                continue;
            }

            int slot = entryTag.getInt(SLOT_KEY);
            if (slot < 0 || slot >= SLOT_COUNT || !entryTag.contains(STACK_KEY, Tag.TAG_COMPOUND)) {
                continue;
            }

            ItemStack stack = ItemStack.parseOptional(registries, entryTag.getCompound(STACK_KEY));
            if (!stack.isEmpty()) {
                setStackInSlot(slot, stack);
            }
        }
    }

    private void loadLegacyFormat(HolderLookup.Provider registries, CompoundTag tag) {
        int legacySize = tag.contains("Size", Tag.TAG_INT) ? tag.getInt("Size") : SLOT_COUNT;
        ListTag items = tag.getList("Items", Tag.TAG_COMPOUND);
        for (Tag entry : items) {
            if (!(entry instanceof CompoundTag stackTag)) {
                continue;
            }

            int legacySlot = readLegacySlotIndex(stackTag);
            if (legacySlot < 0) {
                continue;
            }

            int targetSlot = mapLegacySlot(legacySlot, legacySize);
            if (targetSlot < 0 || targetSlot >= SLOT_COUNT) {
                continue;
            }

            ItemStack stack = ItemStack.parseOptional(registries, stackTag);
            if (!stack.isEmpty()) {
                setStackInSlot(targetSlot, stack);
            }
        }
    }

    private int readLegacySlotIndex(CompoundTag stackTag) {
        if (stackTag.contains(SLOT_KEY, Tag.TAG_INT)) {
            return stackTag.getInt(SLOT_KEY);
        }
        if (stackTag.contains(SLOT_KEY, Tag.TAG_BYTE)) {
            return Byte.toUnsignedInt(stackTag.getByte(SLOT_KEY));
        }
        return -1;
    }

    private int mapLegacySlot(int legacySlot, int legacySize) {
        if (legacySize <= 8) {
            return switch (legacySlot) {
                case 0 -> CyberwareSlot.FACE_1.ordinal();
                case 1 -> CyberwareSlot.ARMS_1.ordinal();
                case 2 -> CyberwareSlot.LEGS_1.ordinal();
                case 3 -> CyberwareSlot.OPERATING_SYSTEM_1.ordinal();
                case 4 -> CyberwareSlot.INTEGUMENTARY_SYSTEM_1.ordinal();
                case 5 -> CyberwareSlot.NERVOUS_SYSTEM_1.ordinal();
                case 6 -> CyberwareSlot.CIRCULATORY_SYSTEM_1.ordinal();
                case 7 -> CyberwareSlot.FRONTAL_CORTEX_1.ordinal();
                default -> -1;
            };
        }
        return legacySlot >= 0 && legacySlot < SLOT_COUNT ? legacySlot : -1;
    }

    private void clearSlots() {
        for (int slot = 0; slot < SLOT_COUNT; slot++) {
            setStackInSlot(slot, ItemStack.EMPTY);
        }
    }

    public int getInstalledChromeCost() {
        int total = 0;
        for (int slot = 0; slot < getSlots(); slot++) {
            ItemStack stack = getStackInSlot(slot);
            if (stack.getItem() instanceof CyberwareItem cyberwareItem) {
                total += cyberwareItem.getChromeCost();
            }
        }
        for (CyberwareModuleItem moduleItem : getInstalledModuleItems()) {
            total += moduleItem.getDefinition().chromeCost();
        }
        for (ChipwareItem chipwareItem : getInstalledChipwareItems()) {
            total += chipwareItem.getDefinition().chromeCost();
        }
        return total;
    }

    public int getInstalledCount() {
        int total = 0;
        for (int slot = 0; slot < getSlots(); slot++) {
            if (!getStackInSlot(slot).isEmpty()) {
                total++;
            }
        }
        return total;
    }

    public List<CyberwareItem> getInstalledCyberwareItems() {
        List<CyberwareItem> installed = new ArrayList<>();
        for (int slot = 0; slot < getSlots(); slot++) {
            if (getStackInSlot(slot).getItem() instanceof CyberwareItem cyberwareItem) {
                installed.add(cyberwareItem);
            }
        }
        return installed;
    }

    public List<CyberwareModuleItem> getInstalledModuleItems() {
        List<CyberwareModuleItem> installed = new ArrayList<>();
        for (int slot = 0; slot < getSlots(); slot++) {
            CyberwareDefinition definition = getInstalledDefinition(slot);
            if (definition == null || !definition.supportsModules()) {
                continue;
            }

            CyberwareModuleHandler moduleHandler = new CyberwareModuleHandler(this, player, slot, definition.moduleCategory());
            for (int moduleSlot = 0; moduleSlot < moduleHandler.getUnlockedSlotCount(); moduleSlot++) {
                ItemStack moduleStack = moduleHandler.getStackInSlot(moduleSlot);
                if (moduleStack.getItem() instanceof CyberwareModuleItem moduleItem) {
                    installed.add(moduleItem);
                }
            }
        }
        return installed;
    }

    public List<ChipwareItem> getInstalledChipwareItems() {
        List<ChipwareItem> installed = new ArrayList<>();
        for (int slot = 0; slot < getSlots(); slot++) {
            CyberwareDefinition definition = getInstalledDefinition(slot);
            if (definition == null || !definition.supportsChipware()) {
                continue;
            }

            ChipwareSocketHandler chipHandler = new ChipwareSocketHandler(this, player, slot);
            for (int chipSlot = 0; chipSlot < chipHandler.getUnlockedSlotCount(); chipSlot++) {
                ItemStack chipStack = chipHandler.getStackInSlot(chipSlot);
                if (chipStack.getItem() instanceof ChipwareItem chipwareItem) {
                    installed.add(chipwareItem);
                }
            }
        }
        return installed;
    }

    public CyberwareItem getInstalledCyberwareBySlotType(CyberwareSlotType slotType) {
        for (int slot = 0; slot < getSlots(); slot++) {
            if (getStackInSlot(slot).getItem() instanceof CyberwareItem cyberwareItem && cyberwareItem.getSlotType() == slotType) {
                return cyberwareItem;
            }
        }
        return null;
    }

    public boolean hasInstalledCyberware(String definitionId) {
        for (int slot = 0; slot < getSlots(); slot++) {
            if (getStackInSlot(slot).getItem() instanceof CyberwareItem cyberwareItem
                    && cyberwareItem.getDefinition().id().equals(definitionId)) {
                return true;
            }
        }
        return false;
    }

    public int getChromeCapacity() {
        int capacity = BASE_CHROME_CAPACITY;
        for (int slot = 0; slot < getSlots(); slot++) {
            ItemStack stack = getStackInSlot(slot);
            if (stack.getItem() instanceof CyberwareItem cyberwareItem) {
                capacity += cyberwareItem.getCapacityBonus();
            }
        }
        return capacity + (int) Math.round(getChromeCapacityEffectBonus());
    }

    public int getCyberstrain() {
        return Math.max(0, getInstalledChromeCost() - getChromeCapacity());
    }

    public int getEstimatedHeat() {
        return getInstalledChromeCost() * 2 + getInstalledCount() + getCyberstrain() * 3;
    }

    public int getIntegrityRating() {
        int installed = 0;
        double totalRatio = 0.0D;
        for (int slot = 0; slot < getSlots(); slot++) {
            ItemStack stack = getStackInSlot(slot);
            if (!(stack.getItem() instanceof CyberwareItem cyberwareItem)) {
                continue;
            }

            installed++;
            totalRatio += CyberwareConditionHelper.getIntegrityRatio(stack, cyberwareItem.getDefinition());
        }

        if (installed == 0) {
            return 100;
        }
        return (int) Math.round((totalRatio / installed) * 100.0D);
    }

    public int getChromePercent() {
        if (getChromeCapacity() <= 0) {
            return 0;
        }
        return Math.min(999, getInstalledChromeCost() * 100 / getChromeCapacity());
    }

    public int getStageIndex() {
        double ratio = getInstalledChromeCapacityRatio();
        if (ratio < 0.20D) {
            return 0;
        }
        if (ratio < 0.40D) {
            return 1;
        }
        if (ratio < 0.70D) {
            return 2;
        }
        if (ratio < 0.95D) {
            return 3;
        }
        return 4;
    }

    private double getInstalledChromeCapacityRatio() {
        return getChromeCapacity() <= 0 ? 0.0D : (double) getInstalledChromeCost() / (double) getChromeCapacity();
    }

    private double getChromeCapacityEffectBonus() {
        double capacityBonus = 0.0D;
        long gameTime = player.level().getGameTime();

        for (int slot = 0; slot < getSlots(); slot++) {
            ItemStack stack = getStackInSlot(slot);
            if (!(stack.getItem() instanceof CyberwareItem cyberwareItem)) {
                continue;
            }

            double integrityScale = CyberwareConditionHelper.getIntegrityRatio(stack, cyberwareItem.getDefinition());
            capacityBonus += getEffectAmount(cyberwareItem.getEffects(), CyberwareEffectType.CHROME_CAPACITY) * integrityScale;
        }
        for (CyberwareModuleItem moduleItem : getInstalledModuleItems()) {
            capacityBonus += getEffectAmount(moduleItem.getDefinition().effects(), CyberwareEffectType.CHROME_CAPACITY);
        }
        for (ChipwareItem chipwareItem : getInstalledChipwareItems()) {
            capacityBonus += getEffectAmount(chipwareItem.getDefinition().effects(), CyberwareEffectType.CHROME_CAPACITY);
        }

        capacityBonus += TemporaryCyberwareEffectManager.getActiveAmount(player, CyberwareEffectType.CHROME_CAPACITY, gameTime);
        return capacityBonus;
    }

    private static double getEffectAmount(Iterable<CyberwareEffect> effects, CyberwareEffectType targetType) {
        double total = 0.0D;
        for (CyberwareEffect effect : effects) {
            if (effect.type() == targetType) {
                total += effect.amount();
            }
        }
        return total;
    }

    private CyberwareDefinition getInstalledDefinition(int slot) {
        ItemStack stack = getStackInSlot(slot);
        return stack.getItem() instanceof CyberwareItem cyberwareItem ? cyberwareItem.getDefinition() : null;
    }
}
