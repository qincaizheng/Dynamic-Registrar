package com.qdd.dynamicregistrar.gui;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.qdd.dynamicregistrar.DynamicRegistrar;
import com.qdd.dynamicregistrar.item.ArmorProperties;
import com.qdd.dynamicregistrar.item.CustomProperties;
import com.qdd.dynamicregistrar.item.TierProperties;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.storage.LevelResource;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;

/**
 * 数据包导出器 - 负责创建和导出数据包
 * 功能：
 * - 创建数据包目录结构
 * - 序列化物品属性为JSON
 * - 写入文件到datapacks目录
 */
public class DatapackExporter {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final String namespace;
    private final String datapackName;
    private final String description;
    private final Path datapackPath;

    /**
     * 创建数据包导出器
     * @param namespace 命名空间
     * @param datapackName 数据包名称
     * @param description 数据包描述
     */
    public DatapackExporter(String namespace, String datapackName, String description) {
        this.namespace = namespace;
        this.datapackName = datapackName;
        this.description = description;
        this.datapackPath = getDatapackPath(datapackName);
    }

    /**
     * 导出普通物品
     * @param properties 物品属性
     * @param statusCallback 状态回调
     */
    public void exportItem(CustomProperties properties, Consumer<String> statusCallback) {
        try {
            createDatapackStructure();

            // 使用Codec转换为JSON
            JsonElement jsonElement = CustomProperties.CODEC.encodeStart(JsonOps.INSTANCE, properties)
                    .getOrThrow();

            // 保存到文件
            Path itemsPath = datapackPath.resolve("data").resolve(namespace).resolve("dynamicregistrar").resolve("items").resolve("item");
            Files.createDirectories(itemsPath);

            String fileName = properties.identifier().getPath() + ".json";
            Path filePath = itemsPath.resolve(fileName);

            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                GSON.toJson(jsonElement, writer);
            }

            String message = "Created custom item: " + properties.identifier();
            statusCallback.accept(message);
            DynamicRegistrar.LOGGER.info(message);

        } catch (Exception e) {
            String message = "Failed to create custom item: " + e;
            statusCallback.accept(message);
            DynamicRegistrar.LOGGER.error("Failed to create custom item", e);
        }
    }

    /**
     * 导出盔甲
     * @param customProps 自定义物品属性
     * @param statusCallback 状态回调
     */
    public void exportArmor(CustomProperties customProps, Consumer<String> statusCallback) {
        try {
            createDatapackStructure();

            // 创建默认盔甲材质（简化版）
            net.minecraft.world.item.ArmorMaterial armorMaterial = new net.minecraft.world.item.ArmorMaterial(
                    java.util.Map.of(
                            net.minecraft.world.item.ArmorItem.Type.BOOTS, 2,
                            net.minecraft.world.item.ArmorItem.Type.LEGGINGS, 5,
                            net.minecraft.world.item.ArmorItem.Type.CHESTPLATE, 6,
                            net.minecraft.world.item.ArmorItem.Type.HELMET, 2
                    ),
                    15,
                    SoundEvents.ARMOR_EQUIP_GENERIC,
                    () -> net.minecraft.world.item.crafting.Ingredient.EMPTY,
                    java.util.List.of(new net.minecraft.world.item.ArmorMaterial.Layer(
                            ResourceLocation.fromNamespaceAndPath(namespace, customProps.identifier().getPath())
                    )),
                    0.0F,
                    0.0F
            );

            ArmorProperties armorProperties = new ArmorProperties(armorMaterial, customProps, true);

            // 使用Codec转换为JSON
            JsonElement jsonElement = ArmorProperties.CODEC.encodeStart(JsonOps.INSTANCE, armorProperties)
                    .getOrThrow();

            // 保存到文件
            Path armorPath = datapackPath.resolve("data").resolve(namespace).resolve("dynamicregistrar").resolve("items").resolve("armor");
            Files.createDirectories(armorPath);

            String fileName = customProps.identifier().getPath() + ".json";
            Path filePath = armorPath.resolve(fileName);

            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                GSON.toJson(jsonElement, writer);
            }

            String message = "Created custom armor: " + customProps.identifier().toString();
            statusCallback.accept(message);
            DynamicRegistrar.LOGGER.info(message);

        } catch (Exception e) {
            String message = "Failed to create custom armor: " + e.getMessage();
            statusCallback.accept(message);
            DynamicRegistrar.LOGGER.error("Failed to create custom armor", e);
        }
    }

    /**
     * 导出工具
     * @param customProps 自定义物品属性
     * @param statusCallback 状态回调
     */
    public void exportTool(CustomProperties customProps, Consumer<String> statusCallback) {
        try {
            createDatapackStructure();

            // 创建默认工具等级（简化版）
            net.neoforged.neoforge.common.SimpleTier tier = new net.neoforged.neoforge.common.SimpleTier(
                    net.minecraft.tags.TagKey.create(net.minecraft.core.registries.Registries.BLOCK,
                            ResourceLocation.fromNamespaceAndPath("minecraft", "needs_stone_tool")),
                    250,
                    6.0F,
                    2.0F,
                    14,
                    () -> net.minecraft.world.item.crafting.Ingredient.EMPTY
            );

            TierProperties tierProperties = new TierProperties(tier, customProps,
                    java.util.List.of("pickaxe", "axe", "shovel", "hoe", "sword"),
                    0.0F, 0.0F);

            // 使用Codec转换为JSON
            JsonElement jsonElement = TierProperties.CODEC.encodeStart(JsonOps.INSTANCE, tierProperties)
                    .getOrThrow();

            // 保存到文件
            Path toolsPath = datapackPath.resolve("data").resolve(namespace).resolve("dynamicregistrar").resolve("items").resolve("tier");
            Files.createDirectories(toolsPath);

            String fileName = customProps.identifier().getPath() + ".json";
            Path filePath = toolsPath.resolve(fileName);

            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                GSON.toJson(jsonElement, writer);
            }

            String message = "Created custom tool: " + customProps.identifier().toString();
            statusCallback.accept(message);
            DynamicRegistrar.LOGGER.info(message);

        } catch (Exception e) {
            String message = "Failed to create custom tool: " + e.getMessage();
            statusCallback.accept(message);
            DynamicRegistrar.LOGGER.error("Failed to create custom tool", e);
        }
    }

    /**
     * 创建数据包目录结构
     */
    private void createDatapackStructure() throws IOException {
        // 创建数据包目录结构
        Files.createDirectories(datapackPath);
        Files.createDirectories(datapackPath.resolve("data").resolve(namespace).resolve("dynamicregistrar").resolve("items").resolve("item"));
        Files.createDirectories(datapackPath.resolve("data").resolve(namespace).resolve("dynamicregistrar").resolve("items").resolve("armor"));
        Files.createDirectories(datapackPath.resolve("data").resolve(namespace).resolve("dynamicregistrar").resolve("items").resolve("tier"));

        // 创建 pack.mcmeta
        JsonObject packMeta = new JsonObject();
        JsonObject pack = new JsonObject();
        pack.addProperty("pack_format", 48);
        pack.addProperty("description", description);
        packMeta.add("pack", pack);

        Path packMetaPath = datapackPath.resolve("pack.mcmeta");
        try (FileWriter writer = new FileWriter(packMetaPath.toFile())) {
            GSON.toJson(packMeta, writer);
        }
    }

    /**
     * 获取数据包路径
     */
    private static Path getDatapackPath(String datapackName) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        Path datapacksPath = Paths.get(mc.gameDirectory.getAbsolutePath(), "datapacks", datapackName);
        if (mc.isSingleplayer()) {
            datapacksPath = Paths.get(mc.getSingleplayerServer().getWorldPath(LevelResource.ROOT).toFile().getAbsolutePath(), "datapacks", datapackName);
        }
        return datapacksPath;
    }

    /**
     * 创建自定义物品属性
     */
    public static CustomProperties createCustomProperties(
            String namespace,
            String itemId,
            String type,
            String curiosType,
            int maxStackSize,
            int maxDamage,
            String rarityStr,
            boolean canRepair,
            boolean fireResistant
    ) {
        ResourceLocation identifier = ResourceLocation.fromNamespaceAndPath(namespace, itemId);

        Rarity rarity;
        try {
            rarity = Rarity.valueOf(rarityStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            rarity = Rarity.COMMON;
        }

        return new CustomProperties(
                identifier,
                type,
                curiosType,
                canRepair,
                null, // food
                fireResistant,
                ItemAttributeModifiers.EMPTY, // attributeModifiers
                maxStackSize,
                maxDamage,
                rarity,
                DataComponentMap.EMPTY
        );
    }

    /**
     * 安全解析整数
     */
    public static int parseIntSafe(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
