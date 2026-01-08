package com.qdd.dynamicregistrar.item;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import javax.annotation.Nullable;
import java.util.Optional;

public record CustomProperties(
    ResourceLocation identifier,
    String type,
    boolean canRepair,
    @Nullable FoodProperties food,
    boolean fireResistant,
    ItemAttributeModifiers attributeModifiers,
    int maxStackSize,
    int maxDamage,
    Rarity rarity,
    DataComponentMap components
) {
    public static final Codec<CustomProperties> CODEC = RecordCodecBuilder.create(p -> p.group(
            ResourceLocation.CODEC.fieldOf("identifier").forGetter(CustomProperties::identifier),
            Codec.STRING.optionalFieldOf("type","").forGetter(CustomProperties::type),
            Codec.BOOL.optionalFieldOf("can_repair", true).forGetter(CustomProperties::canRepair),
            FoodProperties.DIRECT_CODEC.optionalFieldOf("food").forGetter(cp -> Optional.ofNullable(cp.food())),
            Codec.BOOL.optionalFieldOf("fire_resistant", false).forGetter(CustomProperties::fireResistant),
            ItemAttributeModifiers.CODEC.optionalFieldOf("attribute_modifiers", ItemAttributeModifiers.EMPTY).forGetter(CustomProperties::attributeModifiers),
            Codec.INT.optionalFieldOf("max_stack_size", 64).forGetter(CustomProperties::maxStackSize),
            Codec.INT.optionalFieldOf("max_damage", 0).forGetter(CustomProperties::maxDamage),
            Rarity.CODEC.optionalFieldOf("rarity", Rarity.COMMON).forGetter(CustomProperties::rarity),
            DataComponentMap.CODEC.optionalFieldOf("components", DataComponentMap.EMPTY).forGetter(CustomProperties::components)
    ).apply(p, (identifier, type, canRepair, food, fireResistant, attributeModifiers, maxStackSize, maxDamage, rarity, components) ->
            new CustomProperties(identifier, type, canRepair, food.orElse(null), fireResistant, attributeModifiers, maxStackSize, maxDamage, rarity, components)
    ));

    private static <T> void applyComponent(Item.Properties properties, TypedDataComponent<T> component) {
        properties.component(component.type(), component.value());
    }

    public Item.Properties toItemProperties() {
        Item.Properties properties = new Item.Properties();
        if (!canRepair) properties.setNoRepair();
        if (fireResistant) properties.fireResistant();
        if (attributeModifiers != null) properties.attributes(attributeModifiers);
        if (maxStackSize != 64) properties.stacksTo(maxStackSize);
        if (maxDamage != 0) properties.durability(maxDamage);
        if (food != null) properties.food(food);
        if (rarity != Rarity.COMMON) properties.rarity(rarity);
        if (!components.isEmpty()){
            components.forEach(component ->
                applyComponent(properties, component)
            );
        }
        return properties;
    }
}


