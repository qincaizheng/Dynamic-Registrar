package com.qdd.dynamicregistrar.manage;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import com.qdd.dynamicregistrar.DynamicRegistrar;
import com.qdd.dynamicregistrar.item.ArmorProperties;
import com.qdd.dynamicregistrar.item.CustomProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = DynamicRegistrar.MODID)
public class RegisterManager {
    public static final ResourceKey<Registry<CustomProperties>> CUSTOM_PROPERTIES_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(DynamicRegistrar.MODID, "items"));
    // 盔甲属性注册表
    public static final ResourceKey<Registry<ArmorProperties>> ARMOR_PROPERTIES_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(DynamicRegistrar.MODID, "armor"));

    // 物品注册表
    public static Map<ResourceLocation, Item> ITEMS = new HashMap<>();

    // 方块注册表
    public static Map<ResourceLocation, Block> BLOCKS = new HashMap<>();

    // 实体类型注册表
    public static Map<ResourceLocation, EntityType<?>> ENTITY_TYPES = new HashMap<>();

    // 实体方块注册表
    public static Map<ResourceLocation, BlockEntityType<?>> ENTITY_BLOCKS = new HashMap<>();

    @SubscribeEvent // on the mod event bus
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                CUSTOM_PROPERTIES_REGISTRY_KEY,
                CustomProperties.CODEC,
                CustomProperties.CODEC
        );
        event.dataPackRegistry(
                ARMOR_PROPERTIES_REGISTRY_KEY,
                ArmorProperties.CODEC,
                ArmorProperties.CODEC
        );
    }

    @SubscribeEvent
    public static void onDatapackReload(AddReloadListenerEvent event) {
        ITEMS.clear();
        BLOCKS.clear();
        ENTITY_TYPES.clear();
        ENTITY_BLOCKS.clear();
        reloadItems(event);
        reloadBlocks(event);
        reloadEntityTypes(event);
        reloadEntityBlocks(event);
    }


    private static void reloadItems(AddReloadListenerEvent event) {
        // 从数据包中加载物品
        MappedRegistry<?> D = (MappedRegistry<?>) BuiltInRegistries.ITEM;
        D.unfreeze();
        for (CustomProperties properties : event.getRegistryAccess().registry(CUSTOM_PROPERTIES_REGISTRY_KEY).orElseThrow().stream().toList()) {
            ResourceLocation key = properties.identifier();
            if (BuiltInRegistries.ITEM.containsKey(ResourceKey.create(BuiltInRegistries.ITEM.key(), key))) {
                ((MappedRegistry<?>) BuiltInRegistries.ITEM).byLocation.remove(key);
            }
            Item registeredItem = Registry.register(BuiltInRegistries.ITEM, key, new Item(properties.toItemProperties()));
            ITEMS.put(key, registeredItem);
        }
        D.freeze();
    }

    private static void reloadBlocks(AddReloadListenerEvent event) {
        // TODO: Implement block reloading
    }

    private static void reloadEntityTypes(AddReloadListenerEvent event) {
        // TODO: Implement entity type reloading
    }

    private static void reloadEntityBlocks(AddReloadListenerEvent event) {
        // TODO: Implement entity block reloading
    }
}
