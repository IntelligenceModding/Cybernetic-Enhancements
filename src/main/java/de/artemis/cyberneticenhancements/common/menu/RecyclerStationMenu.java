package de.artemis.cyberneticenhancements.common.menu;

import de.artemis.cyberneticenhancements.common.cyberware.CyberwareRecycleHelper;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareRecyclePlan;
import de.artemis.cyberneticenhancements.common.item.ChipwareItem;
import de.artemis.cyberneticenhancements.common.item.CyberConsumableItem;
import de.artemis.cyberneticenhancements.common.item.CyberwareItem;
import de.artemis.cyberneticenhancements.common.item.CyberwareModuleItem;
import de.artemis.cyberneticenhancements.common.registry.ModBlocks;
import de.artemis.cyberneticenhancements.common.registry.ModMenuTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class RecyclerStationMenu extends AbstractBaseMenu implements NamedBlockMenu {
    private static final int INPUT_SLOT = 0;
    private static final int RESULT_SLOT = 1;
    private static final int MACHINE_SLOT_COUNT = 2;
    private static final int INPUT_X = 80;
    private static final int INPUT_Y = 54;
    private static final int RESULT_X = 184;
    private static final int RESULT_Y = 54;
    private static final int PLAYER_INVENTORY_X = 48;
    private static final int PLAYER_INVENTORY_Y = 120;
    private static final int PLAYER_HOTBAR_Y = 178;

    private final Container inputInventory;
    private final Container resultInventory;
    private final BlockPos blockPos;
    private final String blockDisplayName;

    public RecyclerStationMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, extraData.readBlockPos());
    }

    public RecyclerStationMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        super(ModMenuTypes.RECYCLER_STATION.get(), containerId);
        this.blockPos = blockPos.immutable();
        this.blockDisplayName = ModBlocks.RECYCLER_STATION.get().getName().getString();
        this.inputInventory = new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
                RecyclerStationMenu.this.slotsChanged(this);
            }
        };
        this.resultInventory = new SimpleContainer(1);

        addStationSlots();
        addPlayerInventorySlots(playerInventory, PLAYER_INVENTORY_X, PLAYER_INVENTORY_Y);
        addPlayerHotbarSlots(playerInventory, PLAYER_INVENTORY_X, PLAYER_HOTBAR_Y);
        updateResult();
    }

    private void addStationSlots() {
        this.addSlot(new Slot(inputInventory, INPUT_SLOT, INPUT_X, INPUT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return isRecyclable(stack);
            }
        });
        this.addSlot(new Slot(resultInventory, 0, RESULT_X, RESULT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                inputInventory.removeItem(INPUT_SLOT, 1);
                super.onTake(player, stack);
                updateResult();
            }
        });
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        updateResult();
    }

    private void updateResult() {
        CyberwareRecyclePlan plan = getRecyclePlan();
        resultInventory.setItem(0, plan.isAvailable() ? plan.output().copy() : ItemStack.EMPTY);
        broadcastChanges();
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = this.slots.get(index);
        if (sourceSlot == null || !sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copiedStack = sourceStack.copy();

        if (index == RESULT_SLOT) {
            if (!this.moveItemStackTo(sourceStack, MACHINE_SLOT_COUNT, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            sourceSlot.onTake(player, sourceStack);
            return copiedStack;
        }

        if (index < MACHINE_SLOT_COUNT) {
            if (!this.moveItemStackTo(sourceStack, MACHINE_SLOT_COUNT, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (isRecyclable(sourceStack)) {
            if (!moveToContainerSlot(sourceStack, MACHINE_SLOT_COUNT, INPUT_SLOT)) {
                return ItemStack.EMPTY;
            }
        } else {
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
        return copiedStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), blockPos), player, ModBlocks.RECYCLER_STATION.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        clearContainer(player, inputInventory);
        resultInventory.clearContent();
    }

    @Override
    public BlockPos getBlockPos() {
        return blockPos;
    }

    @Override
    public String getBlockDisplayName() {
        return blockDisplayName;
    }

    public ItemStack getInputStack() {
        return inputInventory.getItem(INPUT_SLOT);
    }

    public CyberwareRecyclePlan getRecyclePlan() {
        return CyberwareRecycleHelper.resolve(getInputStack());
    }

    private static boolean isRecyclable(ItemStack stack) {
        return stack.getItem() instanceof CyberwareItem
                || stack.getItem() instanceof ChipwareItem
                || stack.getItem() instanceof CyberwareModuleItem
                || stack.getItem() instanceof CyberConsumableItem;
    }
}
