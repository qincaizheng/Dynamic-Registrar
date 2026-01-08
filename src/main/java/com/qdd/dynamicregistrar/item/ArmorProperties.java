package com.qdd.dynamicregistrar.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ArmorProperties(ArmorMaterial armorMaterial, CustomProperties customProperties, boolean ifBody){
    public static final Codec<ArmorMaterial> ARMOR_MATERIAL_CODEC = RecordCodecBuilder.create(p -> p.group(
            Codec.unboundedMap(ArmorItem.Type.CODEC, Codec.INT).fieldOf("defense").forGetter(ArmorMaterial::defense),
            Codec.INT.fieldOf("enchantmentValue").forGetter(ArmorMaterial::enchantmentValue),
            SoundEvent.CODEC.fieldOf("equipSound").forGetter(ArmorMaterial::equipSound),
            Ingredient.CODEC.fieldOf("repairIngredient").forGetter(am -> am.repairIngredient().get()),
            Codec.list(RecordCodecBuilder.<ArmorMaterial.Layer>create(t -> t.group(
                    ResourceLocation.CODEC.fieldOf("asset_name").forGetter(l -> l.assetName)
            ).apply(t, ArmorMaterial.Layer::new))).fieldOf("layers").forGetter(ArmorMaterial::layers),
            Codec.FLOAT.fieldOf("toughness").forGetter(ArmorMaterial::toughness),
            Codec.FLOAT.fieldOf("knockback_resistance").forGetter(ArmorMaterial::knockbackResistance)
    ).apply(p, (defense, enchantmentValue, equipSound, repairIngredient, layers, toughness, knockbackResistance) -> new ArmorMaterial(defense, enchantmentValue, equipSound,()-> repairIngredient, layers, toughness, knockbackResistance)));

   public static final Codec<ArmorProperties> CODEC = RecordCodecBuilder.create(p -> p.group(
            ARMOR_MATERIAL_CODEC.fieldOf("armor_material").forGetter(ArmorProperties::armorMaterial),
            CustomProperties.CODEC.fieldOf("custom_properties").forGetter(ArmorProperties::customProperties),
            Codec.BOOL.fieldOf("if_body").forGetter(ArmorProperties::ifBody)
    ).apply(p, ArmorProperties::new));
}
