package com.qdd.dynamicregistrar.manage;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import com.qdd.dynamicregistrar.DynamicRegistrar;
import com.qdd.dynamicregistrar.item.CustomProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

import java.util.HashMap;
import java.util.Map;

@EventBusSubscriber(modid = DynamicRegistrar.MODID)
public class RegisterManager {
    public static final ResourceKey<Registry<CustomProperties>> CUSTOM_PROPERTIES_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(DynamicRegistrar.MODID, "custom_properties"));

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
    }

    @SubscribeEvent
    public static void onDatapackReload(AddReloadListenerEvent event) {
        event.addListener(new CustomReloadListener());
    }
}
