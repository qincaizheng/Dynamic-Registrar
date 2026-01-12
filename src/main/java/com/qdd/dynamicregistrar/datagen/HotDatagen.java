package com.qdd.dynamicregistrar.datagen;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.qdd.dynamicregistrar.DynamicRegistrar;
import com.qdd.dynamicregistrar.manage.RegisterManager;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * 热数据生成器 - 在游戏运行时为没有模型文件的物品生成默认模型
 */
public class HotDatagen {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * 运行热数据生成，为所有动态注册的物品生成默认模型文件
     */
    public static void run(ResourceManager resourceManager) throws IOException {
        DynamicRegistrar.LOGGER.info("开始运行热数据生成...");

        // 获取游戏运行目录
        Path runPath = Paths.get("").toAbsolutePath();
        Path modelsPath = runPath.resolve("resourcepacks/dynamicregistrar/assets");

        int generatedCount = 0;

        // 遍历所有动态注册的物品
        for (Map.Entry<ResourceLocation, Item> entry : RegisterManager.ITEMS.entrySet()) {
            ResourceLocation itemId = entry.getKey();
            Item item = entry.getValue();

            // 检查物品是否已有模型文件
            if (!hasItemModel(resourceManager, itemId)) {
                // 检查是否是盔甲物品
                if (item instanceof ArmorItem armorItem) {
                    generateArmorItemModel(modelsPath, itemId, armorItem);
                    generateArmorItemTexture(modelsPath, itemId, armorItem);
                } else {
                    generateDefaultItemModel(modelsPath, itemId);
                    generatePlaceholderTexture(modelsPath, itemId);
                }
                generatedCount++;
                DynamicRegistrar.LOGGER.info("为物品 {} 生成了默认模型和纹理", itemId);
            }
        }

        // 生成盔甲 layer 纹理
        generateArmorLayerTextures(resourceManager, modelsPath);

        // 生成 pack.mcmeta 文件
        generatePackMcmeta(runPath.resolve("resourcepacks/dynamicregistrar"));

        DynamicRegistrar.LOGGER.info("热数据生成完成！共生成了 {} 个模型文件", generatedCount);
        DynamicRegistrar.LOGGER.info("请在游戏中启用资源包: dynamicregistrar");
    }

    /**
     * 检查物品是否已有模型文件
     */
    private static boolean hasItemModel(ResourceManager resourceManager, ResourceLocation itemId) {
        ResourceLocation modelLocation = ResourceLocation.fromNamespaceAndPath(
                itemId.getNamespace(),
                "models/item/" + itemId.getPath() + ".json"
        );
        return resourceManager.getResource(modelLocation).isPresent();
    }

    /**
     * 为物品生成默认的模型文件
     */
    private static void generateDefaultItemModel(Path basePath, ResourceLocation itemId) throws IOException {
        // 创建模型 JSON
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");

        JsonObject textures = new JsonObject();
        // 使用物品自己的纹理路径
        textures.addProperty("layer0", itemId.getNamespace() + ":item/" + itemId.getPath());
        model.add("textures", textures);

        // 确定文件路径
        Path modelPath = basePath.resolve(itemId.getNamespace())
                .resolve("models/item")
                .resolve(itemId.getPath() + ".json");

        // 创建目录
        Files.createDirectories(modelPath.getParent());

        // 写入文件
        String json = GSON.toJson(model);
        Files.writeString(modelPath, json);
    }

    /**
     * 为盔甲物品生成模型文件
     */
    private static void generateArmorItemModel(Path basePath, ResourceLocation itemId, ArmorItem armorItem) throws IOException {
        // 创建模型 JSON
        JsonObject model = new JsonObject();
        model.addProperty("parent", "minecraft:item/generated");

        JsonObject textures = new JsonObject();
        // 使用物品自己的纹理路径
        textures.addProperty("layer0", itemId.getNamespace() + ":item/" + itemId.getPath());
        model.add("textures", textures);

        // 确定文件路径
        Path modelPath = basePath.resolve(itemId.getNamespace())
                .resolve("models/item")
                .resolve(itemId.getPath() + ".json");

        // 创建目录
        Files.createDirectories(modelPath.getParent());

        // 写入文件
        String json = GSON.toJson(model);
        Files.writeString(modelPath, json);
    }

    /**
     * 为盔甲物品生成纹理（16x16）
     */
    private static void generateArmorItemTexture(Path basePath, ResourceLocation itemId, ArmorItem armorItem) throws IOException {
        Path texturePath = basePath.resolve(itemId.getNamespace())
                .resolve("textures/item")
                .resolve(itemId.getPath() + ".png");

        // 如果纹理已存在，跳过
        if (Files.exists(texturePath)) {
            return;
        }

        // 创建目录
        Files.createDirectories(texturePath.getParent());

        // 创建 16x16 的图片
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 根据盔甲类型使用不同的颜色
        Color baseColor = getArmorTypeColor(armorItem.getType());
        g2d.setColor(baseColor);
        g2d.fillRect(0, 0, 16, 16);

        // 添加一个黑色边框
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, 15, 15);

        // 绘制简单的盔甲图案
        g2d.setColor(Color.WHITE);
        drawArmorPattern(g2d, armorItem.getType());

        g2d.dispose();

        // 保存为 PNG 文件
        ImageIO.write(image, "PNG", texturePath.toFile());

        DynamicRegistrar.LOGGER.info("为盔甲物品 {} 生成了占位纹理: {}", itemId, texturePath);
    }

    /**
     * 生成盔甲 layer 纹理（用于穿戴时显示）
     */
    private static void generateArmorLayerTextures(ResourceManager resourceManager, Path basePath) throws IOException {
        // 从注册表中获取所有盔甲物品，并提取唯一的盔甲材质
        var processedMaterials = new java.util.HashSet<ResourceLocation>();

        for (Map.Entry<ResourceLocation, Item> entry : RegisterManager.ITEMS.entrySet()) {
            Item item = entry.getValue();

            // 只处理盔甲物品
            if (item instanceof ArmorItem armorItem) {
                // 获取盔甲材质
                ArmorMaterial material = armorItem.getMaterial().value();

                // 遍历所有 layers
                for (int i = 0; i < material.layers().size(); i++) {
                    ArmorMaterial.Layer layer = material.layers().get(i);
                    ResourceLocation layerAssetName = layer.assetName;

                    // 避免重复生成同一个 layer
                    String layerKey = layerAssetName.toString() + "_" + i;
                    if (processedMaterials.contains(ResourceLocation.parse(layerKey))) {
                        continue;
                    }
                    processedMaterials.add(ResourceLocation.parse(layerKey));

                    // 生成 layer 1 和 layer 2 纹理
                    generateArmorLayerTexture(basePath, layerAssetName, 1);
                    generateArmorLayerTexture(basePath, layerAssetName, 2);

                    DynamicRegistrar.LOGGER.info("为盔甲材质 {} 生成了 layer 纹理", layerAssetName);
                }
            }
        }

        DynamicRegistrar.LOGGER.info("盔甲 layer 纹理生成完成");
    }

    /**
     * 为特定的盔甲材质生成 layer 纹理
     */
    private static void generateArmorLayerTexture(Path basePath, ResourceLocation armorMaterialId, int layerIndex) throws IOException {
        // Layer 1: 用于 helmet, chestplate, boots (64x32)
        // Layer 2: 用于 leggings (64x32)

        String layerSuffix = "_layer_" + layerIndex;
        Path texturePath = basePath.resolve(armorMaterialId.getNamespace())
                .resolve("textures/models/armor")
                .resolve(armorMaterialId.getPath() + layerSuffix + ".png");

        // 如果纹理已存在，跳过
        if (Files.exists(texturePath)) {
            return;
        }

        // 创建目录
        Files.createDirectories(texturePath.getParent());

        // 创建 64x32 的盔甲 layer 纹理
        BufferedImage image = new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 使用半透明的颜色作为占位
        Color layerColor = layerIndex == 1 ? new Color(200, 200, 255, 200) : new Color(255, 200, 200, 200);
        g2d.setColor(layerColor);
        g2d.fillRect(0, 0, 64, 32);

        // 添加网格线以便识别
        g2d.setColor(new Color(0, 0, 0, 100));
        for (int x = 0; x < 64; x += 8) {
            g2d.drawLine(x, 0, x, 32);
        }
        for (int y = 0; y < 32; y += 8) {
            g2d.drawLine(0, y, 64, y);
        }

        g2d.dispose();

        // 保存为 PNG 文件
        ImageIO.write(image, "PNG", texturePath.toFile());

        DynamicRegistrar.LOGGER.info("为盔甲材质 {} 生成了 layer {} 纹理: {}", armorMaterialId, layerIndex, texturePath);
    }

    /**
     * 根据盔甲类型返回不同的颜色
     */
    private static Color getArmorTypeColor(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> new Color(150, 150, 255);      // 浅蓝色
            case CHESTPLATE -> new Color(255, 150, 150);  // 浅红色
            case LEGGINGS -> new Color(150, 255, 150);    // 浅绿色
            case BOOTS -> new Color(255, 255, 150);       // 浅黄色
            case BODY -> new Color(255, 150, 255);        // 浅紫色
        };
    }

    /**
     * 绘制简单的盔甲图案
     */
    private static void drawArmorPattern(Graphics2D g2d, ArmorItem.Type type) {
        switch (type) {
            case HELMET -> {
                // 绘制头盔图案
                g2d.fillRect(4, 4, 8, 4);   // 顶部
                g2d.fillRect(3, 8, 10, 4);  // 中部
            }
            case CHESTPLATE -> {
                // 绘制胸甲图案
                g2d.fillRect(5, 4, 6, 8);   // 主体
                g2d.fillRect(3, 6, 10, 4);  // 横条
            }
            case LEGGINGS -> {
                // 绘制护腿图案
                g2d.fillRect(4, 4, 3, 8);   // 左腿
                g2d.fillRect(9, 4, 3, 8);   // 右腿
            }
            case BOOTS -> {
                // 绘制靴子图案
                g2d.fillRect(4, 8, 3, 4);   // 左靴
                g2d.fillRect(9, 8, 3, 4);   // 右靴
            }
            case BODY -> {
                // 绘制身体图案
                g2d.fillRect(4, 4, 8, 8);
            }
        }
    }

    /**
     * 生成占位纹理图片（16x16 紫红色方块）
     */
    private static void generatePlaceholderTexture(Path basePath, ResourceLocation itemId) throws IOException {
        Path texturePath = basePath.resolve(itemId.getNamespace())
                .resolve("textures/item")
                .resolve(itemId.getPath() + ".png");

        // 如果纹理已存在，跳过
        if (Files.exists(texturePath)) {
            return;
        }

        // 创建目录
        Files.createDirectories(texturePath.getParent());

        // 创建 16x16 的图片
        BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 填充紫红色背景
        g2d.setColor(new Color(255, 0, 255)); // 紫红色 (Magenta)
        g2d.fillRect(0, 0, 16, 16);

        // 添加一个黑色边框，使其更明显
        g2d.setColor(Color.BLACK);
        g2d.drawRect(0, 0, 15, 15);

        // 在中间画一个问号图案（可选）
        g2d.setColor(Color.WHITE);
        g2d.fillRect(6, 4, 4, 2);   // 问号上部
        g2d.fillRect(8, 6, 2, 2);   // 问号中部
        g2d.fillRect(6, 8, 2, 2);   // 问号下部
        g2d.fillRect(6, 11, 4, 2);  // 问号的点

        g2d.dispose();

        // 保存为 PNG 文件
        ImageIO.write(image, "PNG", texturePath.toFile());

        DynamicRegistrar.LOGGER.info("为物品 {} 生成了占位纹理: {}", itemId, texturePath);
    }

    /**
     * 生成资源包的 pack.mcmeta 文件
     */
    private static void generatePackMcmeta(Path packPath) throws IOException {
        JsonObject root = new JsonObject();
        JsonObject pack = new JsonObject();
        pack.addProperty("pack_format", 34); // NeoForge 1.21.x 使用 pack_format 34
        pack.addProperty("description", "动态生成的物品模型");
        root.add("pack", pack);

        Path mcmetaPath = packPath.resolve("pack.mcmeta");
        Files.createDirectories(packPath);

        String json = GSON.toJson(root);
        Files.writeString(mcmetaPath, json);
    }
}