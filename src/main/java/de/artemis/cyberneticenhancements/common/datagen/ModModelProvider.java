package de.artemis.cyberneticenhancements.common.datagen;

import com.google.gson.JsonObject;
import de.artemis.cyberneticenhancements.CyberneticEnhancements;
import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableCatalog;
import de.artemis.cyberneticenhancements.common.consumable.CyberConsumableDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.ChipwareCatalog;
import de.artemis.cyberneticenhancements.common.cyberware.ChipwareDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareCatalog;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareDefinition;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareModuleCatalog;
import de.artemis.cyberneticenhancements.common.cyberware.CyberwareModuleDefinition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class ModModelProvider implements DataProvider {
    private final PackOutput.PathProvider blockstatesPathProvider;
    private final PackOutput.PathProvider blockModelPathProvider;
    private final PackOutput.PathProvider itemModelPathProvider;

    public ModModelProvider(PackOutput output) {
        this.blockstatesPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.blockModelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/block");
        this.itemModelPathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/item");
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        futures.add(saveCubeBlockModel(output, "ripper_station", "minecraft:block/iron_block"));
        futures.add(saveSimpleBlockstate(output, "ripper_station"));
        futures.add(saveBlockItemDefinition(output, "ripper_station"));

        futures.add(saveCubeBlockModel(output, "tech_station", "minecraft:block/gray_concrete"));
        futures.add(saveSimpleBlockstate(output, "tech_station"));
        futures.add(saveBlockItemDefinition(output, "tech_station"));

        futures.add(saveCubeBlockModel(output, "recycler_station", "minecraft:block/deepslate_tiles"));
        futures.add(saveSimpleBlockstate(output, "recycler_station"));
        futures.add(saveBlockItemDefinition(output, "recycler_station"));

        Map<String, String> materials = Map.ofEntries(
                Map.entry("copper_wiring", "minecraft:item/copper_ingot"),
                Map.entry("conductive_paste", "minecraft:item/redstone"),
                Map.entry("servo_screws", "minecraft:item/iron_nugget"),
                Map.entry("sensor_lens", "minecraft:item/amethyst_shard"),
                Map.entry("micro_battery", "minecraft:item/fire_charge"),
                Map.entry("replacement_joint", "minecraft:item/iron_ingot"),
                Map.entry("basic_circuit_plate", "minecraft:item/comparator"),
                Map.entry("common_item_components", "minecraft:item/iron_nugget"),
                Map.entry("uncommon_item_components", "minecraft:item/gold_nugget"),
                Map.entry("rare_item_components", "minecraft:item/diamond"),
                Map.entry("epic_item_components", "minecraft:item/echo_shard"),
                Map.entry("legendary_item_components", "minecraft:item/nether_star")
        );
        for (Map.Entry<String, String> material : materials.entrySet()) {
            futures.add(saveAliasedItemModel(output, material.getKey(), material.getValue()));
        }
        futures.add(saveAliasedFlatItemModel(
                output,
                "whos_ready_for_tomorrow_music_disc",
                "minecraft:item/music_disc_pigstep",
                "minecraft:item/template_music_disc"
        ));

        for (CyberConsumableDefinition definition : CyberConsumableCatalog.definitions()) {
            futures.add(saveAliasedItemModel(
                    output,
                    definition.id(),
                    modelPath(definition.motifItem())
            ));
        }

        for (ChipwareDefinition definition : ChipwareCatalog.definitions()) {
            futures.add(saveAliasedItemModel(
                    output,
                    definition.id(),
                    modelPath(definition.motifItem().asItem())
            ));
        }

        for (CyberwareModuleDefinition definition : CyberwareModuleCatalog.definitions()) {
            futures.add(saveAliasedItemModel(
                    output,
                    definition.id(),
                    modelPath(definition.motifItem().asItem())
            ));
        }

        for (CyberwareDefinition definition : CyberwareCatalog.definitions()) {
            CyberwareDefinition rootDefinition = resolveTextureRoot(definition);
            futures.add(saveAliasedItemModel(
                    output,
                    definition.id(),
                    modelPath(rootDefinition.motifItem().asItem())
            ));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return "Model Definitions: " + CyberneticEnhancements.MOD_ID;
    }

    private CompletableFuture<?> saveCubeBlockModel(CachedOutput output, String modelName, String texturePath) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:block/cube_all");

        JsonObject textures = new JsonObject();
        textures.addProperty("all", texturePath);
        json.add("textures", textures);

        return DataProvider.saveStable(output, json, blockModelPathProvider.json(id(modelName)));
    }

    private CompletableFuture<?> saveSimpleBlockstate(CachedOutput output, String blockName) {
        JsonObject json = new JsonObject();
        JsonObject variants = new JsonObject();
        JsonObject variant = new JsonObject();
        variant.addProperty("model", modPath("block/" + blockName));
        variants.add("", variant);
        json.add("variants", variants);
        return DataProvider.saveStable(output, json, blockstatesPathProvider.json(id(blockName)));
    }

    private CompletableFuture<?> saveBlockItemDefinition(CachedOutput output, String itemName) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", modPath("block/" + itemName));
        return DataProvider.saveStable(output, json, itemModelPathProvider.json(id(itemName)));
    }

    private CompletableFuture<?> saveAliasedItemModel(CachedOutput output, String itemName, String parent) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", parent);
        return DataProvider.saveStable(output, json, itemModelPathProvider.json(id(itemName)));
    }

    private CompletableFuture<?> saveAliasedFlatItemModel(CachedOutput output, String itemName, String texturePath, String parent) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", parent);

        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", texturePath);
        json.add("textures", textures);

        return DataProvider.saveStable(output, json, itemModelPathProvider.json(id(itemName)));
    }

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(CyberneticEnhancements.MOD_ID, path);
    }

    private static CyberwareDefinition resolveTextureRoot(CyberwareDefinition definition) {
        CyberwareDefinition current = definition;
        while (current.upgradeFromId() != null) {
            current = CyberwareCatalog.get(current.upgradeFromId());
        }
        return current;
    }

    private static String texturePath(net.minecraft.world.item.Item item) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        return key.getNamespace() + ":item/" + key.getPath();
    }

    private static String modelPath(net.minecraft.world.item.Item item) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        return key.getNamespace() + ":item/" + key.getPath();
    }

    private static String modPath(String path) {
        return CyberneticEnhancements.MOD_ID + ":" + path;
    }
}
