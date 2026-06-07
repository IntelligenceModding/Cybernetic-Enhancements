package de.artemis.cyberneticenhancements.client.jei;

import de.artemis.cyberneticenhancements.common.recipe.RecyclerRecipe;
import de.artemis.cyberneticenhancements.common.item.CyberwareItem;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareServiceHelper;
import de.artemis.cyberneticenhancements.common.registry.ModBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

public final class RecyclerRecipeCategory implements IRecipeCategory<RecipeHolder<RecyclerRecipe>> {
    private static final int WIDTH = 148;
    private static final int HEIGHT = 62;
    private static final int OUTPUT_SLOT_Y = 18;
    private static final int[] OUTPUT_SLOT_X = {86, 108, 130};

    private final IDrawable icon;
    private final IDrawableStatic arrow;

    public RecyclerRecipeCategory(IGuiHelper guiHelper) {
        this.icon = guiHelper.createDrawableItemLike(ModBlocks.RECYCLER_STATION.get());
        this.arrow = guiHelper.getRecipeArrow();
    }

    @Override
    public mezz.jei.api.recipe.RecipeType<RecipeHolder<RecyclerRecipe>> getRecipeType() {
        return CyberneticEnhancementsJeiPlugin.RECYCLING;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("screen.cyberneticenhancements.recycler_station.recycler_bay");
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<RecyclerRecipe> recipeHolder, IFocusGroup focuses) {
        RecyclerRecipe recipe = recipeHolder.value();
        List<List<ItemStack>> outputSlots = buildDisplayOutputs(recipe);
        builder.addInputSlot(1, 18)
                .setStandardSlotBackground()
                .addIngredients(recipe.getInputIngredient());
        for (int slotIndex = 0; slotIndex < Math.min(outputSlots.size(), OUTPUT_SLOT_X.length); slotIndex++) {
            builder.addOutputSlot(OUTPUT_SLOT_X[slotIndex], OUTPUT_SLOT_Y)
                    .setStandardSlotBackground()
                    .addItemStacks(outputSlots.get(slotIndex));
        }
    }

    @Override
    public void draw(RecipeHolder<RecyclerRecipe> recipeHolder, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.arrow.draw(guiGraphics, 41, 18);
    }

    private List<List<ItemStack>> buildDisplayOutputs(RecyclerRecipe recipe) {
        List<List<ItemStack>> outputSlots = new ArrayList<>();
        outputSlots.add(recipe.getDisplayResults());

        ItemStack[] ingredientStacks = recipe.getInputIngredient().getItems();
        if (ingredientStacks.length > 0 && ingredientStacks[0].getItem() instanceof CyberwareItem cyberwareItem) {
            outputSlots.add(List.of(new ItemStack(CyberwareServiceHelper.slotSupportItem(cyberwareItem.getDefinition().slotType()))));
        }
        return outputSlots;
    }
}
