package com.qdd.dynamicregistrar.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.crafting.Ingredient;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.neoforge.common.SimpleTier;

import java.util.List;

public record TierProperties(SimpleTier tier, CustomProperties customProperties, List<String> types, float extendDamage, float extendSpeed) {
    public static final Codec<SimpleTier> SIMPLE_TIER_CODEC = RecordCodecBuilder.create(p -> p.group(
            TagKey.codec(Registries.BLOCK).fieldOf("incorrectBlocksForDrops").forGetter(SimpleTier::getIncorrectBlocksForDrops),
            Codec.INT.fieldOf("uses").forGetter(SimpleTier::getUses),
            Codec.FLOAT.fieldOf("speed").forGetter(SimpleTier::getSpeed),
            Codec.FLOAT.fieldOf("attackDamageBonus").forGetter(SimpleTier::getAttackDamageBonus),
            Codec.INT.fieldOf("enchantmentValue").forGetter(SimpleTier::getEnchantmentValue),
            Ingredient.CODEC.fieldOf("repairIngredient").forGetter(SimpleTier::getRepairIngredient)
    ).apply(p, (incorrectBlocksForDrops, uses, speed, attackDamageBonus, enchantmentValue, repairIngredient) -> new SimpleTier(incorrectBlocksForDrops, uses, speed, attackDamageBonus, enchantmentValue, () -> repairIngredient)));

    public static final Codec<TierProperties> CODEC = RecordCodecBuilder.create(p -> p.group(
            SIMPLE_TIER_CODEC.fieldOf("tier").forGetter(TierProperties::tier),
            CustomProperties.CODEC.fieldOf("custom_properties").forGetter(TierProperties::customProperties),
            Codec.list(Codec.STRING).fieldOf("types").forGetter(TierProperties::types),
            Codec.FLOAT.optionalFieldOf("extend_damage", 0.0F).forGetter(TierProperties::extendDamage),
            Codec.FLOAT.optionalFieldOf("extend_speed", 0.0F).forGetter(TierProperties::extendSpeed)
    ).apply(p, TierProperties::new));
}
