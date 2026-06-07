package de.artemis.cyberneticenhancements.common.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareConditionHelper;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareDefinition;
import de.artemis.cyberneticenhancements.common.item.CyberwareItem;
import de.artemis.cyberneticenhancements.common.registry.ModBlocks;
import de.artemis.cyberneticenhancements.common.registry.ModRecipeSerializers;
import de.artemis.cyberneticenhancements.common.registry.ModRecipeTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public final class RecyclerRecipe extends SingleItemRecipe {
    private final boolean damageSensitive;

    public RecyclerRecipe(String group, Ingredient ingredient, ItemStack result, boolean damageSensitive) {
        super(ModRecipeTypes.RECYCLING.get(), ModRecipeSerializers.RECYCLING.get(), group, ingredient, result);
        this.damageSensitive = damageSensitive;
    }

    @Override
    public boolean matches(SingleRecipeInput input, Level level) {
        return this.ingredient.test(input.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        ItemStack assembled = this.result.copy();
        if (!damageSensitive || !(input.item().getItem() instanceof CyberwareItem cyberwareItem)) {
            return assembled;
        }

        assembled.setCount(calculateCyberwareOutputCount(input.item(), cyberwareItem.getDefinition(), this.result.getCount()));
        return assembled;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.RECYCLER_STATION.get());
    }

    public Ingredient getInputIngredient() {
        return this.ingredient;
    }

    public boolean isDamageSensitive() {
        return this.damageSensitive;
    }

    public List<ItemStack> getDisplayResults() {
        if (!damageSensitive) {
            return List.of(this.result.copy());
        }

        int minimumCount = Math.max(1, this.result.getCount() - 2);
        List<ItemStack> displayResults = new ArrayList<>(this.result.getCount() - minimumCount + 1);
        for (int count = minimumCount; count <= this.result.getCount(); count++) {
            ItemStack stack = this.result.copy();
            stack.setCount(count);
            displayResults.add(stack);
        }
        return displayResults;
    }

    private static int calculateCyberwareOutputCount(ItemStack inputStack, CyberwareDefinition definition, int baseCount) {
        double integrityRatio = CyberwareConditionHelper.getIntegrityRatio(inputStack, definition);
        int penalty = integrityRatio < 0.45D ? 2 : integrityRatio < 0.75D ? 1 : 0;
        return Math.max(1, baseCount - penalty);
    }

    public static final class Serializer implements RecipeSerializer<RecyclerRecipe> {
        private final MapCodec<RecyclerRecipe> codec = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(recipe -> recipe.group),
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(RecyclerRecipe::getInputIngredient),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(recipe -> recipe.result),
                Codec.BOOL.optionalFieldOf("damage_sensitive", false).forGetter(RecyclerRecipe::isDamageSensitive)
        ).apply(instance, RecyclerRecipe::new));
        private final StreamCodec<RegistryFriendlyByteBuf, RecyclerRecipe> streamCodec = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                recipe -> recipe.group,
                Ingredient.CONTENTS_STREAM_CODEC,
                RecyclerRecipe::getInputIngredient,
                ItemStack.STREAM_CODEC,
                recipe -> recipe.result,
                ByteBufCodecs.BOOL,
                RecyclerRecipe::isDamageSensitive,
                RecyclerRecipe::new
        );

        @Override
        public MapCodec<RecyclerRecipe> codec() {
            return this.codec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, RecyclerRecipe> streamCodec() {
            return this.streamCodec;
        }
    }
}
