package de.artemis.cyberneticenhancements.common.menu;

import de.artemis.cyberneticenhancements.common.cyberware.ChipwareSocketHandler;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareModuleCategory;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareModuleHandler;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareSlot;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareSlotType;
import de.artemis.cyberneticenhancements.common.cyberware.PlayerCyberwareInventory;
import de.artemis.cyberneticenhancements.common.item.ChipwareItem;
import de.artemis.cyberneticenhancements.common.item.CyberwareItem;
import de.artemis.cyberneticenhancements.common.item.CyberwareModuleItem;
import de.artemis.cyberneticenhancements.common.registry.ModBlocks;
import de.artemis.cyberneticenhancements.common.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class RipperStationMenu extends AbstractBaseMenu implements NamedBlockMenu {
    private static final int[] SLOT_X = {
            194, 216, 238,
            216,
            216,
            186,
            246,
            324, 346,
            412, 434, 456,
            412, 434, 456,
            412, 434, 456,
            335
    };
    private static final int[] SLOT_Y = {
            58, 58, 58,
            88,
            116,
            170,
            170,
            88, 88,
            58, 58, 58,
            116, 116, 116,
            174, 174, 174,
            224
    };
    private static final int[] CHIP_X = {20, 42, 64};
    private static final int[] CHIP_Y = {278, 300, 322};
    private static final int[] ARM_MODULE_X = {214, 236, 258};
    private static final int[] LEG_MODULE_X = {548, 570, 592};
    private static final int ARM_MODULE_Y = 286;
    private static final int LEG_MODULE_Y = 286;
    private static final int PLAYER_INVENTORY_X = 279;
    private static final int PLAYER_INVENTORY_Y = 366;
    private static final int PLAYER_HOTBAR_Y = 424;
    private static final int CHIP_HANDLER_COUNT = 3;
    private static final int CHIP_SLOT_COUNT = 3;

    private final PlayerCyberwareInventory cyberwareInventory;
    private final ChipwareSocketHandler[] chipwareInventories;
    private final CyberwareModuleHandler armModuleInventory;
    private final CyberwareModuleHandler legModuleInventory;
    private final BlockPos blockPos;
    private final String blockDisplayName;
    private final int cyberwareSlotCount;
    private final int containerSlotCount;
    private final int playerInventoryStart;
    private final int playerHotbarStart;
    private int installedChromeClient;
    private int chromeCapacityClient;
    private int cyberstrainClient;
    private int heatClient;
    private int integrityClient;
    private int installedCountClient;
    private int stageIndexClient;

    public RipperStationMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, extraData.readBlockPos());
    }

    public RipperStationMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        super(ModMenuTypes.RIPPER_STATION.get(), containerId);
        this.cyberwareInventory = new PlayerCyberwareInventory(playerInventory.player);
        this.chipwareInventories = new ChipwareSocketHandler[] {
                new ChipwareSocketHandler(cyberwareInventory, playerInventory.player, CyberwareSlot.FRONTAL_CORTEX_1.ordinal()),
                new ChipwareSocketHandler(cyberwareInventory, playerInventory.player, CyberwareSlot.FRONTAL_CORTEX_2.ordinal()),
                new ChipwareSocketHandler(cyberwareInventory, playerInventory.player, CyberwareSlot.FRONTAL_CORTEX_3.ordinal())
        };
        this.armModuleInventory = new CyberwareModuleHandler(cyberwareInventory, playerInventory.player, CyberwareSlot.ARMS_1.ordinal(), CyberwareModuleCategory.ARMS);
        this.legModuleInventory = new CyberwareModuleHandler(cyberwareInventory, playerInventory.player, CyberwareSlot.LEGS_1.ordinal(), CyberwareModuleCategory.LEGS);
        this.blockPos = blockPos.immutable();
        this.blockDisplayName = ModBlocks.RIPPER_STATION.get().getName().getString();

        addCyberwareSlots();
        this.cyberwareSlotCount = this.slots.size();
        addChipSlots();
        addModuleSlots();
        this.containerSlotCount = this.slots.size();
        this.playerInventoryStart = containerSlotCount;
        addPlayerInventorySlots(playerInventory, PLAYER_INVENTORY_X, PLAYER_INVENTORY_Y);
        this.playerHotbarStart = this.slots.size();
        addPlayerHotbarSlots(playerInventory, PLAYER_INVENTORY_X, PLAYER_HOTBAR_Y);
        addStatSlots();
    }

    private void addCyberwareSlots() {
        for (int slot = 0; slot < PlayerCyberwareInventory.SLOT_COUNT; slot++) {
            this.addSlot(new SlotItemHandler(cyberwareInventory, slot, SLOT_X[slot], SLOT_Y[slot]) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return cyberwareInventory.isItemValid(getContainerSlot(), stack);
                }

                @Override
                public void setChanged() {
                    super.setChanged();
                    cyberwareInventory.save();
                }

                @Override
                public void onTake(Player player, ItemStack stack) {
                    super.onTake(player, stack);
                    cyberwareInventory.save();
                }
            });
        }
    }

    private void addChipSlots() {
        for (int handlerIndex = 0; handlerIndex < CHIP_HANDLER_COUNT; handlerIndex++) {
            ChipwareSocketHandler chipHandler = chipwareInventories[handlerIndex];
            for (int slot = 0; slot < CHIP_SLOT_COUNT; slot++) {
                final int chipSlot = slot;
                this.addSlot(new SlotItemHandler(chipHandler, chipSlot, CHIP_X[slot], CHIP_Y[handlerIndex]) {
                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return chipHandler.isItemValid(chipSlot, stack);
                    }

                    @Override
                    public boolean isActive() {
                        return chipHandler.isSlotUnlocked(chipSlot);
                    }

                    @Override
                    public void setChanged() {
                        super.setChanged();
                        cyberwareInventory.save();
                    }

                    @Override
                    public void onTake(Player player, ItemStack stack) {
                        super.onTake(player, stack);
                        cyberwareInventory.save();
                    }
                });
            }
        }
    }

    private void addModuleSlots() {
        addModuleSlotBank(armModuleInventory, ARM_MODULE_X, ARM_MODULE_Y);
        addModuleSlotBank(legModuleInventory, LEG_MODULE_X, LEG_MODULE_Y);
    }

    private void addModuleSlotBank(CyberwareModuleHandler moduleHandler, int[] slotX, int y) {
        for (int slot = 0; slot < slotX.length; slot++) {
            final int moduleSlot = slot;
            this.addSlot(new SlotItemHandler(moduleHandler, moduleSlot, slotX[slot], y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return moduleHandler.isItemValid(moduleSlot, stack);
                }

                @Override
                public boolean isActive() {
                    return moduleHandler.isSlotUnlocked(moduleSlot);
                }

                @Override
                public void setChanged() {
                    super.setChanged();
                    cyberwareInventory.save();
                }

                @Override
                public void onTake(Player player, ItemStack stack) {
                    super.onTake(player, stack);
                    cyberwareInventory.save();
                }
            });
        }
    }

    private void addStatSlots() {
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return cyberwareInventory.getInstalledChromeCost();
            }

            @Override
            public void set(int value) {
                installedChromeClient = value;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return cyberwareInventory.getChromeCapacity();
            }

            @Override
            public void set(int value) {
                chromeCapacityClient = value;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return cyberwareInventory.getCyberstrain();
            }

            @Override
            public void set(int value) {
                cyberstrainClient = value;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return cyberwareInventory.getEstimatedHeat();
            }

            @Override
            public void set(int value) {
                heatClient = value;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return cyberwareInventory.getIntegrityRating();
            }

            @Override
            public void set(int value) {
                integrityClient = value;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return cyberwareInventory.getInstalledCount();
            }

            @Override
            public void set(int value) {
                installedCountClient = value;
            }
        });
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return cyberwareInventory.getStageIndex();
            }

            @Override
            public void set(int value) {
                stageIndexClient = value;
            }
        });
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        cyberwareInventory.save();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), blockPos), player, ModBlocks.RIPPER_STATION.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = this.slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copiedStack = sourceStack.copy();

        if (index < containerSlotCount) {
            if (!this.moveItemStackTo(sourceStack, playerInventoryStart, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (sourceStack.getItem() instanceof ChipwareItem) {
            int targetSlot = findPreferredChipMenuSlot();
            if (targetSlot < 0 || !this.moveItemStackTo(sourceStack, targetSlot, targetSlot + 1, false)) {
                return moveWithinPlayerInventory(index, sourceStack) ? copiedStack : ItemStack.EMPTY;
            }
        } else if (sourceStack.getItem() instanceof CyberwareModuleItem moduleItem) {
            int targetSlot = findPreferredModuleMenuSlot(moduleItem.getDefinition().category());
            if (targetSlot < 0 || !this.moveItemStackTo(sourceStack, targetSlot, targetSlot + 1, false)) {
                return moveWithinPlayerInventory(index, sourceStack) ? copiedStack : ItemStack.EMPTY;
            }
        } else if (sourceStack.getItem() instanceof CyberwareItem cyberwareItem) {
            int targetSlot = findPreferredCyberwareMenuSlot(cyberwareItem.getSlotType());
            if (targetSlot < 0 || !this.moveItemStackTo(sourceStack, targetSlot, targetSlot + 1, false)) {
                return moveWithinPlayerInventory(index, sourceStack) ? copiedStack : ItemStack.EMPTY;
            }
        } else if (!moveWithinPlayerInventory(index, sourceStack)) {
            return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }

        if (sourceStack.getCount() == copiedStack.getCount()) {
            return ItemStack.EMPTY;
        }

        sourceSlot.onTake(player, sourceStack);
        return copiedStack;
    }

    private boolean moveWithinPlayerInventory(int index, ItemStack sourceStack) {
        if (index < playerHotbarStart) {
            return this.moveItemStackTo(sourceStack, playerHotbarStart, this.slots.size(), false);
        }
        return this.moveItemStackTo(sourceStack, playerInventoryStart, playerHotbarStart, false);
    }

    private int findPreferredCyberwareMenuSlot(CyberwareSlotType slotType) {
        int fallback = -1;
        for (CyberwareSlot slot : CyberwareSlot.values()) {
            if (slot.getType() != slotType) {
                continue;
            }

            int menuIndex = findSlotIndexForContainerSlot(cyberwareSlotCount, slot.ordinal());
            if (menuIndex < 0) {
                continue;
            }
            if (!this.slots.get(menuIndex).hasItem()) {
                return menuIndex;
            }
            if (fallback < 0) {
                fallback = menuIndex;
            }
        }
        return fallback;
    }

    private int findPreferredChipMenuSlot() {
        int fallback = -1;
        for (int handlerIndex = 0; handlerIndex < chipwareInventories.length; handlerIndex++) {
            ChipwareSocketHandler chipHandler = chipwareInventories[handlerIndex];
            int startIndex = cyberwareSlotCount + handlerIndex * CHIP_SLOT_COUNT;
            for (int slot = 0; slot < CHIP_SLOT_COUNT; slot++) {
                int menuIndex = startIndex + slot;
                if (!chipHandler.isSlotUnlocked(slot)) {
                    continue;
                }
                if (!this.slots.get(menuIndex).hasItem()) {
                    return menuIndex;
                }
                if (fallback < 0) {
                    fallback = menuIndex;
                }
            }
        }
        return fallback;
    }

    private int findPreferredModuleMenuSlot(CyberwareModuleCategory category) {
        CyberwareModuleHandler handler = category == CyberwareModuleCategory.ARMS ? armModuleInventory : legModuleInventory;
        int startIndex = category == CyberwareModuleCategory.ARMS
                ? cyberwareSlotCount + CHIP_HANDLER_COUNT * CHIP_SLOT_COUNT
                : cyberwareSlotCount + CHIP_HANDLER_COUNT * CHIP_SLOT_COUNT + CyberwareModuleHandler.MAX_MODULE_SLOTS;
        int fallback = -1;
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            int menuIndex = startIndex + slot;
            if (!handler.isSlotUnlocked(slot)) {
                continue;
            }
            if (!this.slots.get(menuIndex).hasItem()) {
                return menuIndex;
            }
            if (fallback < 0) {
                fallback = menuIndex;
            }
        }
        return fallback;
    }

    @Override
    public BlockPos getBlockPos() {
        return blockPos;
    }

    @Override
    public String getBlockDisplayName() {
        return blockDisplayName;
    }

    public ItemStack getCyberwareStack(CyberwareSlot slot) {
        return cyberwareInventory.getStackInSlot(slot.ordinal());
    }

    public int getInstalledCount(CyberwareSlotType type) {
        int count = 0;
        for (CyberwareSlot slot : CyberwareSlot.values()) {
            if (slot.getType() == type && !getCyberwareStack(slot).isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public ItemStack getChipwareStack(int handlerIndex, int slot) {
        return chipwareInventories[handlerIndex].getStackInSlot(slot);
    }

    public boolean isChipSlotUnlocked(int handlerIndex, int slot) {
        return chipwareInventories[handlerIndex].isSlotUnlocked(slot);
    }

    public String getChipwareHostName(int handlerIndex) {
        return chipwareInventories[handlerIndex].getHostDisplayName();
    }

    public ItemStack getArmModuleStack(int slot) {
        return armModuleInventory.getStackInSlot(slot);
    }

    public ItemStack getLegModuleStack(int slot) {
        return legModuleInventory.getStackInSlot(slot);
    }

    public boolean isArmModuleSlotUnlocked(int slot) {
        return armModuleInventory.isSlotUnlocked(slot);
    }

    public boolean isLegModuleSlotUnlocked(int slot) {
        return legModuleInventory.isSlotUnlocked(slot);
    }

    public String getArmModuleHostName() {
        return armModuleInventory.getHostDisplayName();
    }

    public String getLegModuleHostName() {
        return legModuleInventory.getHostDisplayName();
    }

    public int getInstalledChrome() {
        return installedChromeClient;
    }

    public int getChromeCapacity() {
        return chromeCapacityClient;
    }

    public int getCyberstrain() {
        return cyberstrainClient;
    }

    public int getEstimatedHeat() {
        return heatClient;
    }

    public int getIntegrity() {
        return integrityClient;
    }

    public int getInstalledCount() {
        return installedCountClient;
    }

    public int getStageIndex() {
        return stageIndexClient;
    }

    public int getChromePercent() {
        if (getChromeCapacity() <= 0) {
            return 0;
        }
        return getInstalledChrome() * 100 / getChromeCapacity();
    }
}
