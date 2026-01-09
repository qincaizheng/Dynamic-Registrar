package com.qdd.dynamicregistrar.manage;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import com.mojang.datafixers.util.Pair;
import com.qdd.dynamicregistrar.DynamicRegistrar;
import com.qdd.dynamicregistrar.curio.BackRender;
import com.qdd.dynamicregistrar.curio.CurioItem;
import com.qdd.dynamicregistrar.curio.HeadRender;
import com.qdd.dynamicregistrar.data.DataComponents;
import com.qdd.dynamicregistrar.item.ArmorProperties;
import com.qdd.dynamicregistrar.item.CustomProperties;
import com.qdd.dynamicregistrar.item.TierProperties;
import com.qdd.dynamicregistrar.network.ArmorPropertiesPacket;
import com.qdd.dynamicregistrar.network.CustomPropertiesPacket;
import com.qdd.dynamicregistrar.network.ReloadPacket;
import com.qdd.dynamicregistrar.network.TierPropertiesPacket;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

import java.util.*;

@EventBusSubscriber(modid = DynamicRegistrar.MODID)
public class RegisterManager {
    public static final ResourceKey<Registry<CustomProperties>> CUSTOM_PROPERTIES_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(DynamicRegistrar.MODID, "items/item"));
    // 盔甲属性注册表
    public static final ResourceKey<Registry<ArmorProperties>> ARMOR_PROPERTIES_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(DynamicRegistrar.MODID, "items/armor"));

    // 工具属性注册表
    public static final ResourceKey<Registry<TierProperties>> TIER_PROPERTIES_REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(DynamicRegistrar.MODID, "items/tier"));

    // 需同步的CustomProperties
    public static List<CustomProperties> SYNC_CUSTOM_PROPERTIES = new ArrayList<>();

    // 需同步的ArmorProperties
    public static List<ArmorProperties> SYNC_ARMOR_PROPERTIES = new ArrayList<>();

    // 需同步的TierProperties
    public static List<TierProperties> SYNC_TIER_PROPERTIES = new ArrayList<>();

    // 物品注册表
    public static Map<ResourceLocation, Item> ITEMS = new HashMap<>();

    // 盔甲注册表
    public static Map<ResourceLocation, ArmorMaterial> ARMOR_MATERIALS = new HashMap<>();

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
        event.dataPackRegistry(
                TIER_PROPERTIES_REGISTRY_KEY,
                TierProperties.CODEC,
                TierProperties.CODEC
        );
    }

    @SubscribeEvent
    public static void onDatapackReload(AddReloadListenerEvent event) {
        SYNC_CUSTOM_PROPERTIES.clear();
        SYNC_ARMOR_PROPERTIES.clear();
        SYNC_TIER_PROPERTIES.clear();
        SYNC_CUSTOM_PROPERTIES.addAll(event.getRegistryAccess().registry(CUSTOM_PROPERTIES_REGISTRY_KEY).orElseThrow().stream().toList());
        SYNC_ARMOR_PROPERTIES.addAll(event.getRegistryAccess().registry(ARMOR_PROPERTIES_REGISTRY_KEY).orElseThrow().stream().toList());
        SYNC_TIER_PROPERTIES.addAll(event.getRegistryAccess().registry(TIER_PROPERTIES_REGISTRY_KEY).orElseThrow().stream().toList());
        reload();
    }

    public static void reload() {
        ITEMS.clear();
        BLOCKS.clear();
        ENTITY_TYPES.clear();
        ENTITY_BLOCKS.clear();
        reloadItems();
        reloadBlocks();
        reloadEntityTypes();
        reloadEntityBlocks();
    }


    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null && !event.getPlayer().server.isSingleplayer()) {
            PacketDistributor.sendToPlayer(event.getPlayer(),new CustomPropertiesPacket(SYNC_CUSTOM_PROPERTIES));
            PacketDistributor.sendToPlayer(event.getPlayer(),new ArmorPropertiesPacket(SYNC_ARMOR_PROPERTIES));
            PacketDistributor.sendToPlayer(event.getPlayer(),new TierPropertiesPacket(SYNC_TIER_PROPERTIES));
            PacketDistributor.sendToPlayer(event.getPlayer(),new ReloadPacket(0));
        }
    }


    private static void reloadItems() {
        DynamicRegistrar.LOGGER.info("Reloading items");
        // 从数据包中加载物品
        MappedRegistry<?> ITEM = (MappedRegistry<?>) BuiltInRegistries.ITEM;
        MappedRegistry<?> ARMOR_MATERIAL = (MappedRegistry<?>) BuiltInRegistries.ARMOR_MATERIAL;
        ITEM.unfreeze();
        // 盔甲注册
        ARMOR_MATERIAL.unfreeze();
        for (ArmorProperties properties : SYNC_ARMOR_PROPERTIES) {
            ResourceLocation key = properties.customProperties().identifier();
            if (BuiltInRegistries.ARMOR_MATERIAL.containsKey(ResourceKey.create(BuiltInRegistries.ARMOR_MATERIAL.key(), key))) {
                ((MappedRegistry<?>) BuiltInRegistries.ARMOR_MATERIAL).byValue.remove(properties.armorMaterial());
                ((MappedRegistry<?>) BuiltInRegistries.ARMOR_MATERIAL).byLocation.remove(key);
            }
            Holder<ArmorMaterial> registeredMaterial = Registry.registerForHolder(BuiltInRegistries.ARMOR_MATERIAL, key,properties.armorMaterial());
            ARMOR_MATERIALS.put(key, registeredMaterial.value());
            for (ArmorItem.Type type : ArmorItem.Type.values()) {
                if (!properties.ifBody() && type == ArmorItem.Type.BODY) continue;
                var itemKey = ResourceLocation.fromNamespaceAndPath(key.getNamespace(), key.getPath() + "_" + type.getName());
                if (BuiltInRegistries.ITEM.containsKey(ResourceKey.create(BuiltInRegistries.ITEM.key(), itemKey))) {
                    ((MappedRegistry<?>) BuiltInRegistries.ITEM).byLocation.remove(itemKey);
                }
                Item registeredItem=Registry.register(BuiltInRegistries.ITEM,itemKey,new ArmorItem( registeredMaterial,type, properties.customProperties().toItemProperties()));
                ITEMS.put(itemKey, registeredItem);
            }
        }
        ARMOR_MATERIAL.freeze();
        // 普通物品注册
        for (CustomProperties properties : SYNC_CUSTOM_PROPERTIES) {
            ResourceLocation key = properties.identifier();
            if (BuiltInRegistries.ITEM.containsKey(ResourceKey.create(BuiltInRegistries.ITEM.key(), key))) {
                ((MappedRegistry<?>) BuiltInRegistries.ITEM).byLocation.remove(key);
            }
            try {
                if(!properties.type().isEmpty()) {
                    Class<?> itemClass;
                    if(properties.type().contains(".")){
                        itemClass = Class.forName(properties.type());
                    }else{
                        itemClass = Class.forName("net.minecraft.world.item." + properties.type());
                    }
                    Item registeredItem = Registry.register(BuiltInRegistries.ITEM, key, (Item) itemClass.getDeclaredConstructor(Item.Properties.class).newInstance(properties.toItemProperties()));
                    ITEMS.put(key, registeredItem);
                } else {
                    Item registeredItem = Registry.register(BuiltInRegistries.ITEM, key, new Item(properties.toItemProperties()));
                    ITEMS.put(key, registeredItem);
                }
            } catch (Exception e) {
                DynamicRegistrar.LOGGER.error("Failed to create item for type {}", properties.type());
                Item registeredItem = Registry.register(BuiltInRegistries.ITEM, key, new Item(properties.toItemProperties()));
                ITEMS.put(key, registeredItem);
            }
        }
        // 工具注册
        for (TierProperties properties : SYNC_TIER_PROPERTIES) {
            ResourceLocation key = properties.customProperties().identifier();
            for (String type : properties.types()) {
                var itemKey = ResourceLocation.fromNamespaceAndPath(key.getNamespace(), key.getPath() + "_" + type);
                if (BuiltInRegistries.ITEM.containsKey(ResourceKey.create(BuiltInRegistries.ITEM.key(), itemKey))) {
                    ((MappedRegistry<?>) BuiltInRegistries.ITEM).byLocation.remove(itemKey);
                }
                switch (type) {
                    case "sword":
                        Item registeredItem = Registry.register(BuiltInRegistries.ITEM, itemKey, new SwordItem(properties.tier(), properties.customProperties().toItemProperties().attributes(SwordItem.createAttributes(properties.tier(), properties.extendDamage(), properties.extendSpeed()))));
                        ITEMS.put(itemKey, registeredItem);
                        break;
                    case "axe":
                        Item registeredItem2 = Registry.register(BuiltInRegistries.ITEM, itemKey, new AxeItem(properties.tier(), properties.customProperties().toItemProperties().attributes(AxeItem.createAttributes(properties.tier(), properties.extendDamage(), properties.extendSpeed()))));
                        ITEMS.put(itemKey, registeredItem2);
                        break;
                    case "pickaxe":
                        Item registeredItem3 = Registry.register(BuiltInRegistries.ITEM, itemKey, new PickaxeItem(properties.tier(), properties.customProperties().toItemProperties().attributes(PickaxeItem.createAttributes(properties.tier(), properties.extendDamage(), properties.extendSpeed()))));
                        ITEMS.put(itemKey, registeredItem3);
                        break;
                    case "shovel":
                        Item registeredItem4 = Registry.register(BuiltInRegistries.ITEM, itemKey, new ShovelItem(properties.tier(), properties.customProperties().toItemProperties().attributes(ShovelItem.createAttributes(properties.tier(), properties.extendDamage(), properties.extendSpeed()))));
                        ITEMS.put(itemKey, registeredItem4);
                        break;
                    case "hoe":
                        Item registeredItem5 = Registry.register(BuiltInRegistries.ITEM, itemKey, new HoeItem(properties.tier(),properties.customProperties().toItemProperties().attributes(HoeItem.createAttributes(properties.tier(), properties.extendDamage(), properties.extendSpeed()))));
                        ITEMS.put(itemKey, registeredItem5);
                        break;
                    default:
                        break;
                }
            }
        }
        ITEM.freeze();
        DynamicRegistrar.LOGGER.info("Finished reloading items , {} items", ITEMS.size());
        if(ModList.get().isLoaded("curios")){
            if(FMLLoader.getDist() == Dist.CLIENT){
                reloadClientCurios();
            }
            reloadCurios();
        }
    }

    private static void reloadCurios() {
        ITEMS.forEach((key, item) -> {
            if (!item.components().get(DataComponents.curios.value()).isEmpty()) {
                CuriosApi.registerCurio(item, new CurioItem());
                Optional<Pair<TagKey<Item>, HolderSet.Named<Item>>> tagKey = BuiltInRegistries.ITEM.getTags().filter(tag ->
                                tag.getFirst().location().equals(ResourceLocation.fromNamespaceAndPath("curios", item.components().get(DataComponents.curios.value()))))
                        .findFirst();
                List<TagKey<Item>> tags = new ArrayList<>(item.builtInRegistryHolder().tags);
                if (tagKey.isPresent()) {
                    tags.add(tagKey.get().getFirst());
                } else {
                    tags.add(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("curios", item.components().get(DataComponents.curios.value()))));
                }
                item.builtInRegistryHolder().tags = Set.copyOf(tags);
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    private static void reloadClientCurios() {
        ITEMS.forEach((key, item) -> {
            if (item.components().get(DataComponents.curios.value()).isEmpty()) return;
            switch (item.components().get(DataComponents.curios.value())) {
                case "head":
                    CuriosRendererRegistry.register(item,()-> HeadRender.INSTANCE);
                    break;
                case "back":
                    CuriosRendererRegistry.register(item,()-> BackRender.INSTANCE);
                    break;
                default:
                    break;
            }
        });
    }

    private static void reloadBlocks() {
        // TODO: Implement block reloading
    }

    private static void reloadEntityTypes() {
        // TODO: Implement entity type reloading
    }

    private static void reloadEntityBlocks() {
        // TODO: Implement entity block reloading
    }
}
