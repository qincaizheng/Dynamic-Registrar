package com.qdd.dynamicregistrar.manage;

import com.qdd.dynamicregistrar.DynamicRegistrar;
import com.qdd.dynamicregistrar.item.CustomProperties;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;
import java.util.Optional;

public class ExampleRegist {
    public static final ResourceKey<CustomProperties> EXAMPLE_CUSTOM_PROPERTIES = ResourceKey.create(
            RegisterManager.CUSTOM_PROPERTIES_REGISTRY_KEY,
            ResourceLocation.fromNamespaceAndPath(DynamicRegistrar.MODID, "example_custom_properties")
    );

    public static final CustomProperties EXAMPLE_CUSTOM_PROPERTIES_VALUE = new CustomProperties(
            EXAMPLE_CUSTOM_PROPERTIES.location(),
            true,
            new FoodProperties(0,0f,false,1F, Optional.empty(), List.of()),
            false,
            ItemAttributeModifiers.EMPTY,
            64,
            0,
            Rarity.COMMON,
            DataComponentMap.EMPTY
    );
}
