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
    private static final int REPAIR_INPUT_SLOT = 0;
    private static final int REPAIR_MATERIAL_SLOT = 1;
    private static final int UPGRADE_INPUT_SLOT = 2;
    private static final int UPGRADE_PRIMARY_SLOT = 3;
    private static final int UPGRADE_SECONDARY_SLOT = 4;
    private static final int REPAIR_RESULT_SLOT = 5;
    private static final int UPGRADE_RESULT_SLOT = 6;
    private static final int MACHINE_SLOT_COUNT = 7;

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
        this.serviceInventory = new SimpleContainer(5) {
            @Override
            public void setChanged() {
                super.setChanged();
                TechstationMenu.this.slotsChanged(this);
            }
        };
        this.resultInventory = new SimpleContainer(2);

        addStationSlots();
        addPlayerInventorySlots(playerInventory, TechstationLayout.PLAYER_INVENTORY_X, TechstationLayout.PLAYER_INVENTORY_Y);
        addPlayerHotbarSlots(playerInventory, TechstationLayout.PLAYER_INVENTORY_X, TechstationLayout.PLAYER_HOTBAR_Y);
        updateResults();
    }

    private void addStationSlots() {
        this.addSlot(new Slot(serviceInventory, REPAIR_INPUT_SLOT, TechstationLayout.REPAIR_INPUT_X, TechstationLayout.REPAIR_INPUT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof CyberwareItem;
            }
        });
        this.addSlot(new Slot(serviceInventory, REPAIR_MATERIAL_SLOT, TechstationLayout.REPAIR_MATERIAL_X, TechstationLayout.REPAIR_MATERIAL_Y));
        this.addSlot(new Slot(serviceInventory, UPGRADE_INPUT_SLOT, TechstationLayout.UPGRADE_INPUT_X, TechstationLayout.UPGRADE_INPUT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof CyberwareItem;
            }
        });
        this.addSlot(new Slot(serviceInventory, UPGRADE_PRIMARY_SLOT, TechstationLayout.UPGRADE_PRIMARY_X, TechstationLayout.UPGRADE_PRIMARY_Y));
        this.addSlot(new Slot(serviceInventory, UPGRADE_SECONDARY_SLOT, TechstationLayout.UPGRADE_SECONDARY_X, TechstationLayout.UPGRADE_SECONDARY_Y));
        this.addSlot(new Slot(resultInventory, 0, TechstationLayout.REPAIR_RESULT_X, TechstationLayout.REPAIR_RESULT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                CyberwareServicePlan plan = getRepairResolvedPlan();
                if (plan.isAvailable()) {
                    consumeRepairInputs(plan);
                }
                super.onTake(player, stack);
                updateResults();
            }
        });
        this.addSlot(new Slot(resultInventory, 1, TechstationLayout.UPGRADE_RESULT_X, TechstationLayout.UPGRADE_RESULT_Y) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                CyberwareServicePlan plan = getUpgradeResolvedPlan();
                if (plan.isAvailable()) {
                    consumeUpgradeInputs(plan);
                }
                super.onTake(player, stack);
                updateResults();
            }
        });
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        updateResults();
    }

    private void updateResults() {
        CyberwareServicePlan repairPlan = getRepairResolvedPlan();
        CyberwareServicePlan upgradePlan = getUpgradeResolvedPlan();
        resultInventory.setItem(0, repairPlan.isAvailable() ? repairPlan.output().copy() : ItemStack.EMPTY);
        resultInventory.setItem(1, upgradePlan.isAvailable() ? upgradePlan.output().copy() : ItemStack.EMPTY);
        broadcastChanges();
    }

    private void consumeRepairInputs(CyberwareServicePlan plan) {
        serviceInventory.removeItem(REPAIR_INPUT_SLOT, 1);
        ItemStack material = serviceInventory.getItem(REPAIR_MATERIAL_SLOT);
        material.shrink(plan.primaryCount());
        if (material.isEmpty()) {
            serviceInventory.setItem(REPAIR_MATERIAL_SLOT, ItemStack.EMPTY);
        }
    }

    private void consumeUpgradeInputs(CyberwareServicePlan plan) {
        serviceInventory.removeItem(UPGRADE_INPUT_SLOT, 1);

        ItemStack firstMaterial = serviceInventory.getItem(UPGRADE_PRIMARY_SLOT);
        ItemStack secondMaterial = serviceInventory.getItem(UPGRADE_SECONDARY_SLOT);
        if (ItemStack.isSameItemSameComponents(firstMaterial.copyWithCount(1), plan.primaryMaterial().copyWithCount(1))) {
            firstMaterial.shrink(plan.primaryCount());
            secondMaterial.shrink(plan.secondaryCount());
        } else {
            firstMaterial.shrink(plan.secondaryCount());
            secondMaterial.shrink(plan.primaryCount());
        }

        if (firstMaterial.isEmpty()) {
            serviceInventory.setItem(UPGRADE_PRIMARY_SLOT, ItemStack.EMPTY);
        }
        if (secondMaterial.isEmpty()) {
            serviceInventory.setItem(UPGRADE_SECONDARY_SLOT, ItemStack.EMPTY);
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

        if (index == REPAIR_RESULT_SLOT || index == UPGRADE_RESULT_SLOT) {
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
            if (!moveCyberwareIntoBays(sourceStack)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveMaterialIntoBays(sourceStack)) {
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

    private boolean moveCyberwareIntoBays(ItemStack sourceStack) {
        if (canRepair(sourceStack) && moveToContainerSlot(sourceStack, MACHINE_SLOT_COUNT, REPAIR_INPUT_SLOT)) {
            return true;
        }
        if (canUpgrade(sourceStack) && moveToContainerSlot(sourceStack, MACHINE_SLOT_COUNT, UPGRADE_INPUT_SLOT)) {
            return true;
        }
        if (moveToContainerSlot(sourceStack, MACHINE_SLOT_COUNT, REPAIR_INPUT_SLOT)) {
            return true;
        }
        return moveToContainerSlot(sourceStack, MACHINE_SLOT_COUNT, UPGRADE_INPUT_SLOT);
    }

    private boolean moveMaterialIntoBays(ItemStack sourceStack) {
        int preferredSlot = findPreferredMaterialSlot(sourceStack);
        if (preferredSlot >= 0 && this.moveItemStackTo(sourceStack, preferredSlot, preferredSlot + 1, false)) {
            return true;
        }
        if (this.moveItemStackTo(sourceStack, REPAIR_MATERIAL_SLOT, REPAIR_MATERIAL_SLOT + 1, false)) {
            return true;
        }
        return this.moveItemStackTo(sourceStack, UPGRADE_PRIMARY_SLOT, UPGRADE_SECONDARY_SLOT + 1, false);
    }

    private int findPreferredMaterialSlot(ItemStack sourceStack) {
        CyberwareServicePlan repairPlan = getRepairPlan();
        if (repairPlan.isAvailable()
                && isMatchingMaterial(sourceStack, repairPlan.primaryMaterial())
                && canMergeInto(REPAIR_MATERIAL_SLOT, sourceStack)) {
            return REPAIR_MATERIAL_SLOT;
        }

        CyberwareServicePlan upgradePlan = getUpgradePlan();
        if (!upgradePlan.isAvailable()) {
            return -1;
        }
        if (isMatchingMaterial(sourceStack, upgradePlan.primaryMaterial()) && canMergeInto(UPGRADE_PRIMARY_SLOT, sourceStack)) {
            return UPGRADE_PRIMARY_SLOT;
        }
        if (upgradePlan.requiresSecondaryMaterial()
                && isMatchingMaterial(sourceStack, upgradePlan.secondaryMaterial())
                && canMergeInto(UPGRADE_SECONDARY_SLOT, sourceStack)) {
            return UPGRADE_SECONDARY_SLOT;
        }
        return -1;
    }

    private boolean canRepair(ItemStack sourceStack) {
        return CyberwareServiceHelper.getRepairPlan(sourceStack).isAvailable();
    }

    private boolean canUpgrade(ItemStack sourceStack) {
        return CyberwareServiceHelper.getUpgradePlan(sourceStack).isAvailable();
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

    public ItemStack getRepairInputStack() {
        return serviceInventory.getItem(REPAIR_INPUT_SLOT);
    }

    public ItemStack getRepairMaterialStack() {
        return serviceInventory.getItem(REPAIR_MATERIAL_SLOT);
    }

    public ItemStack getUpgradeInputStack() {
        return serviceInventory.getItem(UPGRADE_INPUT_SLOT);
    }

    public ItemStack getUpgradePrimaryMaterialStack() {
        return serviceInventory.getItem(UPGRADE_PRIMARY_SLOT);
    }

    public ItemStack getUpgradeSecondaryMaterialStack() {
        return serviceInventory.getItem(UPGRADE_SECONDARY_SLOT);
    }

    public CyberwareServicePlan getRepairPlan() {
        return CyberwareServiceHelper.getRepairPlan(getRepairInputStack());
    }

    public CyberwareServicePlan getUpgradePlan() {
        return CyberwareServiceHelper.getUpgradePlan(getUpgradeInputStack());
    }

    public CyberwareServicePlan getRepairResolvedPlan() {
        return CyberwareServiceHelper.matchesMaterials(getRepairPlan(), getRepairMaterialStack(), ItemStack.EMPTY)
                ? getRepairPlan()
                : CyberwareServicePlan.empty();
    }

    public CyberwareServicePlan getUpgradeResolvedPlan() {
        return CyberwareServiceHelper.matchesMaterials(getUpgradePlan(), getUpgradePrimaryMaterialStack(), getUpgradeSecondaryMaterialStack())
                ? getUpgradePlan()
                : CyberwareServicePlan.empty();
    }
}
