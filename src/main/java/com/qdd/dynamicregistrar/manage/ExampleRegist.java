package com.qdd.dynamicregistrar.manage;

import com.qdd.dynamicregistrar.DynamicRegistrar;
import com.qdd.dynamicregistrar.item.ArmorProperties;
import com.qdd.dynamicregistrar.item.CustomProperties;
import com.qdd.dynamicregistrar.item.TierProperties;
import net.neoforged.neoforge.common.SimpleTier;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ExampleRegist {
    public static final ResourceKey<CustomProperties> EXAMPLE_CUSTOM_PROPERTIES = ResourceKey.create(
            RegisterManager.CUSTOM_PROPERTIES_REGISTRY_KEY,
            ResourceLocation.fromNamespaceAndPath(DynamicRegistrar.MODID, "example_custom_properties")
    );

    public static final CustomProperties EXAMPLE_CUSTOM_PROPERTIES_VALUE = new CustomProperties(
            ResourceLocation.fromNamespaceAndPath(DynamicRegistrar.MODID, "example_custom_properties"),
            "",
            "head",
            true,
            null,
            false,
            ItemAttributeModifiers.EMPTY,
            64,
            0,
            Rarity.COMMON,
            DataComponentMap.EMPTY
    );
    // 注册一个复杂的物品作为例子
    public static final ResourceKey<CustomProperties> EXAMPLE_COMPLEX_CUSTOM_PROPERTIES_KEY = ResourceKey.create(
            RegisterManager.CUSTOM_PROPERTIES_REGISTRY_KEY,
            ResourceLocation.fromNamespaceAndPath(DynamicRegistrar.MODID, "example_complex_custom_properties")
    );
    public static final CustomProperties EXAMPLE_COMPLEX_CUSTOM_PROPERTIES = new CustomProperties(
            ResourceLocation.fromNamespaceAndPath(DynamicRegistrar.MODID, "example_complex_custom_properties"),
            "FishingRodItem",
            "",
            false,
            new FoodProperties(1,1f,false,1F, Optional.of(new ItemStack(Items.BOWL)), List.of(new FoodProperties.PossibleEffect(() -> new MobEffectInstance(MobEffects.HEALTH_BOOST, 100, 0), 1.0F) )),
            true,
            ItemAttributeModifiers.builder().add(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, 1.0, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND).build(),
            1,
            300,
            Rarity.RARE,
            DataComponentMap.builder().set(
                    DataComponents.LORE,
                    new ItemLore(List.of(Component.translatable("item.dynamicregistrar.example_complex_custom_properties.lore")))
            ).build()
    );


    public static final ResourceKey<ArmorProperties> EXAMPLE_ARMOR_PROPERTIES = ResourceKey.create(
            RegisterManager.ARMOR_PROPERTIES_REGISTRY_KEY,
            ResourceLocation.fromNamespaceAndPath(DynamicRegistrar.MODID, "example_armor_properties")
    );
    public static final ArmorProperties EXAMPLE_ARMOR_PROPERTIES_VALUE = new ArmorProperties(
            new ArmorMaterial(
                    Map.of(ArmorItem.Type.HELMET, 1, ArmorItem.Type.CHESTPLATE, 2, ArmorItem.Type.LEGGINGS, 1, ArmorItem.Type.BOOTS, 1),
                    0,
                    SoundEvents.ARMOR_EQUIP_GENERIC,
                    () -> Ingredient.EMPTY,
                    List.of(new ArmorMaterial.Layer(EXAMPLE_CUSTOM_PROPERTIES.location())),
                    0f,
                    0f
            ),
            EXAMPLE_CUSTOM_PROPERTIES_VALUE,
            false
    );
    public static final ResourceKey<TierProperties> EXAMPLE_TIER_PROPERTIES = ResourceKey.create(
            RegisterManager.TIER_PROPERTIES_REGISTRY_KEY,
            ResourceLocation.fromNamespaceAndPath(DynamicRegistrar.MODID, "example_tier_properties")
    );
    public static final TierProperties EXAMPLE_TIER_PROPERTIES_VALUE = new TierProperties(
            new SimpleTier(
                    TagKey.create(Registries.BLOCK, EXAMPLE_CUSTOM_PROPERTIES.location()),
                    1,
                    1f,
                    1f,
                    1,
                    () -> Ingredient.EMPTY
            ),
            EXAMPLE_CUSTOM_PROPERTIES_VALUE,
            List.of("sword", "axe", "pickaxe", "shovel", "hoe"),
            0.5f,
            0.5f
    );
}