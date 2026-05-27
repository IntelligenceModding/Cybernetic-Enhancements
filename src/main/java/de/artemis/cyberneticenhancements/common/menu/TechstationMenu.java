package de.artemis.cyberneticenhancements.common.menu;

import de.artemis.cyberneticenhancements.common.cyberware.CyberwareServiceHelper;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareServicePlan;
import de.artemis.cyberneticenhancements.common.item.CyberwareItem;
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

public final class TechstationMenu extends AbstractBaseMenu implements NamedBlockMenu {
    private static final int INPUT_SLOT = 0;
    private static final int PRIMARY_MATERIAL_SLOT = 1;
    private static final int SECONDARY_MATERIAL_SLOT = 2;
    private static final int RESULT_SLOT = 3;
    private static final int MACHINE_SLOT_COUNT = 4;
    private static final int INPUT_X = 44;
    private static final int INPUT_Y = 60;
    private static final int PRIMARY_X = 116;
    private static final int PRIMARY_Y = 42;
    private static final int SECONDARY_X = 116;
    private static final int SECONDARY_Y = 78;
    private static final int RESULT_X = 206;
    private static final int RESULT_Y = 60;
    private static final int PLAYER_INVENTORY_X = 48;
    private static final int PLAYER_INVENTORY_Y = 136;
    private static final int PLAYER_HOTBAR_Y = 194;

    private final Container serviceInventory;
    private final Container resultInventory;
    private final BlockPos blockPos;
    private final String blockDisplayName;

    public TechstationMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, playerInventory, extraData.readBlockPos());
    }

    public TechstationMenu(int containerId, Inventory playerInventory, BlockPos blockPos) {
        super(ModMenuTypes.TECHSTATION.get(), containerId);
        this.blockPos = blockPos.immutable();
        this.blockDisplayName = ModBlocks.TECHSTATION.get().getName().getString();
        this.serviceInventory = new SimpleContainer(3) {
            @Override
            public void setChanged() {
                super.setChanged();
                TechstationMenu.this.slotsChanged(this);
            }
        };
        this.resultInventory = new SimpleContainer(1);

        addStationSlots();
        addPlayerInventorySlots(playerInventory, PLAYER_INVENTORY_X, PLAYER_INVENTORY_Y);
        addPlayerHotbarSlots(playerInventory, PLAYER_INVENTORY_X, PLAYER_HOTBAR_Y);
        updateResult();
    }

    private void addStationSlots() {
        this.addSlot(new Slot(serviceInventory, INPUT_SLOT, INPUT_X, INPUT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof CyberwareItem;
            }
        });
        this.addSlot(new Slot(serviceInventory, PRIMARY_MATERIAL_SLOT, PRIMARY_X, PRIMARY_Y));
        this.addSlot(new Slot(serviceInventory, SECONDARY_MATERIAL_SLOT, SECONDARY_X, SECONDARY_Y));
        this.addSlot(new Slot(resultInventory, 0, RESULT_X, RESULT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                CyberwareServicePlan plan = getResolvedPlan();
                if (plan.isAvailable()) {
                    consumeInputs(plan);
                }
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
        CyberwareServicePlan plan = getResolvedPlan();
        resultInventory.setItem(0, plan.isAvailable() ? plan.output().copy() : ItemStack.EMPTY);
        broadcastChanges();
    }

    private void consumeInputs(CyberwareServicePlan plan) {
        serviceInventory.removeItem(INPUT_SLOT, 1);

        ItemStack firstMaterial = serviceInventory.getItem(PRIMARY_MATERIAL_SLOT);
        ItemStack secondMaterial = serviceInventory.getItem(SECONDARY_MATERIAL_SLOT);
        if (ItemStack.isSameItemSameComponents(firstMaterial.copyWithCount(1), plan.primaryMaterial().copyWithCount(1))) {
            firstMaterial.shrink(plan.primaryCount());
            if (plan.requiresSecondaryMaterial()) {
                secondMaterial.shrink(plan.secondaryCount());
            }
        } else if (plan.requiresSecondaryMaterial()) {
            firstMaterial.shrink(plan.secondaryCount());
            secondMaterial.shrink(plan.primaryCount());
        }

        if (firstMaterial.isEmpty()) {
            serviceInventory.setItem(PRIMARY_MATERIAL_SLOT, ItemStack.EMPTY);
        }
        if (secondMaterial.isEmpty()) {
            serviceInventory.setItem(SECONDARY_MATERIAL_SLOT, ItemStack.EMPTY);
        }
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
        } else if (sourceStack.getItem() instanceof CyberwareItem) {
            if (!moveToContainerSlot(sourceStack, MACHINE_SLOT_COUNT, INPUT_SLOT)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveMaterialIntoServiceSlots(sourceStack)) {
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

    private boolean moveMaterialIntoServiceSlots(ItemStack sourceStack) {
        int preferredSlot = findPreferredMaterialSlot(sourceStack);
        if (preferredSlot >= 0 && this.moveItemStackTo(sourceStack, preferredSlot, preferredSlot + 1, false)) {
            return true;
        }
        return this.moveItemStackTo(sourceStack, PRIMARY_MATERIAL_SLOT, SECONDARY_MATERIAL_SLOT + 1, false);
    }

    private int findPreferredMaterialSlot(ItemStack sourceStack) {
        CyberwareServicePlan upgradePlan = getUpgradePlan();
        int slot = findPreferredMaterialSlot(sourceStack, upgradePlan);
        if (slot >= 0) {
            return slot;
        }

        CyberwareServicePlan repairPlan = getRepairPlan();
        return findPreferredMaterialSlot(sourceStack, repairPlan);
    }

    private int findPreferredMaterialSlot(ItemStack sourceStack, CyberwareServicePlan plan) {
        if (!plan.isAvailable()) {
            return -1;
        }
        if (isMatchingMaterial(sourceStack, plan.primaryMaterial()) && canMergeInto(PRIMARY_MATERIAL_SLOT, sourceStack)) {
            return PRIMARY_MATERIAL_SLOT;
        }
        if (plan.requiresSecondaryMaterial()
                && isMatchingMaterial(sourceStack, plan.secondaryMaterial())
                && canMergeInto(SECONDARY_MATERIAL_SLOT, sourceStack)) {
            return SECONDARY_MATERIAL_SLOT;
        }
        return -1;
    }

    private boolean isMatchingMaterial(ItemStack sourceStack, ItemStack expectedStack) {
        return !expectedStack.isEmpty() && ItemStack.isSameItemSameComponents(sourceStack.copyWithCount(1), expectedStack.copyWithCount(1));
    }

    private boolean canMergeInto(int slotIndex, ItemStack sourceStack) {
        ItemStack existing = serviceInventory.getItem(slotIndex);
        return existing.isEmpty() || ItemStack.isSameItemSameComponents(existing, sourceStack);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(player.level(), blockPos), player, ModBlocks.TECHSTATION.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        clearContainer(player, serviceInventory);
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
        return serviceInventory.getItem(INPUT_SLOT);
    }

    public ItemStack getPrimaryMaterialStack() {
        return serviceInventory.getItem(PRIMARY_MATERIAL_SLOT);
    }

    public ItemStack getSecondaryMaterialStack() {
        return serviceInventory.getItem(SECONDARY_MATERIAL_SLOT);
    }

    public CyberwareServicePlan getResolvedPlan() {
        return CyberwareServiceHelper.resolve(getInputStack(), getPrimaryMaterialStack(), getSecondaryMaterialStack());
    }

    public CyberwareServicePlan getRepairPlan() {
        return CyberwareServiceHelper.getRepairPlan(getInputStack());
    }

    public CyberwareServicePlan getUpgradePlan() {
        return CyberwareServiceHelper.getUpgradePlan(getInputStack());
    }
}
