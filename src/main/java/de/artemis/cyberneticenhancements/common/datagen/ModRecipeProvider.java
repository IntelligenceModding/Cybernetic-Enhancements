package de.artemis.cyberneticenhancements.common.datagen;

import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableCatalog;
import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.ChipwareCatalog;
import de.artemis.cyberneticenhancements.common.cyberware.ChipwareDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareCatalog;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareModuleCatalog;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareModuleCategory;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareModuleDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareSlotType;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareTier;
import de.artemis.cyberneticenhancements.common.registry.ModBlocks;
import de.artemis.cyberneticenhancements.common.registry.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput recipeOutput) {
        buildConsumableRecipes(recipeOutput);
        buildChipwareRecipes(recipeOutput);
        buildModuleRecipes(recipeOutput);
        buildStationRecipes(recipeOutput);
    }

    private void buildConsumableRecipes(RecipeOutput recipeOutput) {
        for (CyberConsumableDefinition definition : CyberConsumableCatalog.definitions()) {
            buildConsumableRecipe(recipeOutput, definition);
        }
    }

    private void buildConsumableRecipe(RecipeOutput recipeOutput, CyberConsumableDefinition definition) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.consumable(definition.id()).get());

        switch (definition.id()) {
            case "maxdoc_mk1" -> builder.define('G', Items.GLASS_BOTTLE).define('R', Items.REDSTONE).define('P', ModItems.CONDUCTIVE_PASTE.get())
                    .pattern(" G ").pattern(" R ").pattern(" P ");
            case "maxdoc_mk2" -> builder.define('G', Items.GLASS_BOTTLE).define('A', Items.GOLDEN_APPLE).define('P', ModItems.CONDUCTIVE_PASTE.get())
                    .pattern(" G ").pattern(" A ").pattern(" P ");
            case "maxdoc_mk3" -> builder.define('G', Items.GLASS_BOTTLE).define('H', Items.GHAST_TEAR).define('C', ModItems.BASIC_CIRCUIT_PLATE.get())
                    .pattern(" G ").pattern(" H ").pattern(" C ");
            case "bounce_back_mk1" -> builder.define('G', Items.GLASS_BOTTLE).define('S', Items.SUGAR).define('P', ModItems.CONDUCTIVE_PASTE.get())
                    .pattern(" G ").pattern(" S ").pattern(" P ");
            case "bounce_back_mk2" -> builder.define('G', Items.GLASS_BOTTLE).define('R', Items.REDSTONE).define('H', Items.GHAST_TEAR)
                    .pattern(" G ").pattern(" R ").pattern(" H ");
            case "bounce_back_mk3" -> builder.define('G', Items.GLASS_BOTTLE).define('A', Items.GOLDEN_APPLE).define('C', ModItems.BASIC_CIRCUIT_PLATE.get())
                    .pattern(" G ").pattern(" A ").pattern(" C ");
            case "health_booster" -> builder.define('A', Items.GOLDEN_APPLE).define('G', Items.GLASS_BOTTLE).define('P', ModItems.CONDUCTIVE_PASTE.get())
                    .pattern(" A ").pattern(" G ").pattern(" P ");
            case "stamina_booster" -> builder.define('S', Items.SUGAR).define('G', Items.GLASS_BOTTLE).define('R', Items.REDSTONE)
                    .pattern(" S ").pattern(" G ").pattern(" R ");
            case "oxy_booster" -> builder.define('H', Items.GHAST_TEAR).define('G', Items.GLASS_BOTTLE).define('S', Items.SUGAR)
                    .pattern(" H ").pattern(" G ").pattern(" S ");
            case "capacity_booster" -> builder.define('A', Items.GOLDEN_APPLE).define('M', ModItems.MICRO_BATTERY.get()).define('S', Items.SUGAR)
                    .pattern(" A ").pattern(" M ").pattern(" S ");
            case "ram_jolt" -> builder.define('R', Items.REDSTONE).define('M', ModItems.MICRO_BATTERY.get()).define('C', ModItems.BASIC_CIRCUIT_PLATE.get())
                    .pattern(" R ").pattern(" M ").pattern(" C ");
            case "immunoblockers" -> builder.define('F', Items.FERMENTED_SPIDER_EYE).define('H', Items.GHAST_TEAR).define('C', ModItems.BASIC_CIRCUIT_PLATE.get())
                    .pattern(" F ").pattern(" H ").pattern(" C ");
            case "chrome_suppressant" -> builder.define('H', Items.GHAST_TEAR).define('P', ModItems.CONDUCTIVE_PASTE.get()).define('C', ModItems.BASIC_CIRCUIT_PLATE.get())
                    .pattern(" H ").pattern(" P ").pattern(" C ");
            case "black_lace" -> builder.define('B', Items.BLAZE_POWDER).define('S', Items.SUGAR).define('F', Items.FERMENTED_SPIDER_EYE)
                    .pattern(" B ").pattern(" S ").pattern(" F ");
            case "asskick" -> builder.define('A', Items.GOLDEN_APPLE).define('S', Items.SUGAR).define('F', Items.FERMENTED_SPIDER_EYE)
                    .pattern(" A ").pattern(" S ").pattern(" F ");
            case "jellytricity" -> builder.define('S', Items.SUGAR).define('R', Items.REDSTONE).define('F', Items.FERMENTED_SPIDER_EYE)
                    .pattern(" S ").pattern(" R ").pattern(" F ");
            default -> builder.define('G', Items.GLASS_BOTTLE).define('P', ModItems.CONDUCTIVE_PASTE.get()).define('R', Items.REDSTONE)
                    .pattern(" G ").pattern(" P ").pattern(" R ");
        }

        builder.unlockedBy("has_conductive_paste", has(ModItems.CONDUCTIVE_PASTE.get()))
                .save(recipeOutput);
    }

    private void buildModuleRecipes(RecipeOutput recipeOutput) {
        for (CyberwareModuleDefinition definition : CyberwareModuleCatalog.definitions()) {
            buildModuleRecipe(recipeOutput, definition);
        }
    }

    private void buildChipwareRecipes(RecipeOutput recipeOutput) {
        for (ChipwareDefinition definition : ChipwareCatalog.definitions()) {
            buildChipwareRecipe(recipeOutput, definition);
        }
    }

    private void buildChipwareRecipe(RecipeOutput recipeOutput, ChipwareDefinition definition) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.chipware(definition.id()).get())
                .pattern(" T ")
                .pattern("CMC")
                .pattern(" R ")
                .define('T', tierIngredient(definition.tier()))
                .define('C', ModItems.BASIC_CIRCUIT_PLATE.get())
                .define('M', definition.motifItem())
                .define('R', Items.REDSTONE)
                .unlockedBy("has_basic_circuit_plate", has(ModItems.BASIC_CIRCUIT_PLATE.get()))
                .save(recipeOutput);
    }

    private void buildModuleRecipe(RecipeOutput recipeOutput, CyberwareModuleDefinition definition) {
        ShapedRecipeBuilder builder = ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.module(definition.id()).get())
                .define('T', tierIngredient(definition.tier()))
                .define('C', ModItems.BASIC_CIRCUIT_PLATE.get())
                .define('M', definition.motifItem())
                .define('R', ModItems.REPLACEMENT_JOINT.get());

        if (definition.category() == CyberwareModuleCategory.ARMS) {
            builder.define('W', ModItems.COPPER_WIRING.get())
                    .pattern("TWT")
                    .pattern("CMC")
                    .pattern("TRT");
        } else {
            builder.define('B', ModItems.MICRO_BATTERY.get())
                    .pattern("TRT")
                    .pattern("CMC")
                    .pattern("TBT");
        }

        builder.unlockedBy("has_basic_circuit_plate", has(ModItems.BASIC_CIRCUIT_PLATE.get()))
                .save(recipeOutput);
    }

    private ItemLike tierIngredient(CyberwareTier tier) {
        return switch (tier) {
            case TIER_1 -> Items.IRON_INGOT;
            case TIER_2 -> Items.GOLD_INGOT;
            case TIER_3 -> Items.DIAMOND;
            case TIER_4 -> Items.ECHO_SHARD;
            case TIER_5 -> Items.NETHER_STAR;
        };
    }

    private void buildStationRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.RIPPER_STATION.get())
                .pattern("ISI")
                .pattern("CBC")
                .pattern("IPI")
                .define('I', Items.IRON_INGOT)
                .define('S', ModItems.SENSOR_LENS.get())
                .define('C', ModItems.BASIC_CIRCUIT_PLATE.get())
                .define('B', Items.SMOOTH_STONE)
                .define('P', ModItems.CONDUCTIVE_PASTE.get())
                .unlockedBy("has_basic_circuit_plate", has(ModItems.BASIC_CIRCUIT_PLATE.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.TECHSTATION.get())
                .pattern("ICI")
                .pattern("RBR")
                .pattern("EPE")
                .define('I', Items.IRON_INGOT)
                .define('C', ModItems.BASIC_CIRCUIT_PLATE.get())
                .define('R', ModItems.REPLACEMENT_JOINT.get())
                .define('B', Items.SMOOTH_STONE)
                .define('E', ModItems.UNCOMMON_ITEM_COMPONENTS.get())
                .define('P', ModItems.CONDUCTIVE_PASTE.get())
                .unlockedBy("has_uncommon_item_components", has(ModItems.UNCOMMON_ITEM_COMPONENTS.get()))
                .save(recipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.RECYCLER_STATION.get())
                .pattern("ISI")
                .pattern("CBC")
                .pattern("RPR")
                .define('I', Items.IRON_INGOT)
                .define('S', ModItems.SERVO_SCREWS.get())
                .define('C', ModItems.BASIC_CIRCUIT_PLATE.get())
                .define('B', Items.SMOOTH_STONE)
                .define('R', ModItems.REPLACEMENT_JOINT.get())
                .define('P', ModItems.CONDUCTIVE_PASTE.get())
                .unlockedBy("has_basic_circuit_plate", has(ModItems.BASIC_CIRCUIT_PLATE.get()))
                .save(recipeOutput);
    }
}
