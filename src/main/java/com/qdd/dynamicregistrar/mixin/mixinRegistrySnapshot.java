package com.qdd.dynamicregistrar.mixin;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import com.qdd.dynamicregistrar.manage.RegisterManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectSortedMap;
import net.neoforged.neoforge.registries.RegistrySnapshot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(RegistrySnapshot.class)
public class mixinRegistrySnapshot {
    @Final
    @Shadow
    @Mutable
    private Int2ObjectSortedMap<ResourceLocation> ids;

    @Final
    @Shadow
    @Mutable
    private Map<ResourceLocation, ResourceLocation> aliases;
    @Inject(method = "<init>(Lnet/minecraft/core/Registry;Z)V", at = @At("TAIL"))
    private void init(Registry<?> registry, boolean full, CallbackInfo ci) {
        if (registry.equals(BuiltInRegistries.ITEM)) {
            for(ResourceLocation key : RegisterManager.ITEMS.keySet()) {
                if (BuiltInRegistries.ITEM.containsKey(ResourceKey.create(BuiltInRegistries.ITEM.key(), key))) {
                    ids.remove(BuiltInRegistries.ITEM.getId(ResourceKey.create(BuiltInRegistries.ITEM.key(), key)));
                    aliases.remove(key);
                }
            }
        }
        if (registry.equals(BuiltInRegistries.BLOCK)) {
            for(ResourceLocation key : RegisterManager.BLOCKS.keySet()) {
                if (BuiltInRegistries.BLOCK.containsKey(ResourceKey.create(BuiltInRegistries.BLOCK.key(), key))) {
                    ids.remove(BuiltInRegistries.BLOCK.getId(ResourceKey.create(BuiltInRegistries.BLOCK.key(), key)));
                    aliases.remove(key);
                }
            }
        }
        if (registry.equals(BuiltInRegistries.ENTITY_TYPE)) {
            for(ResourceLocation key : RegisterManager.ENTITY_TYPES.keySet()) {
                if (BuiltInRegistries.ENTITY_TYPE.containsKey(ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), key))) {
                    ids.remove(BuiltInRegistries.ENTITY_TYPE.getId(ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), key)));
                    aliases.remove(key);
                }
            }
        }
        if (registry.equals(BuiltInRegistries.BLOCK_ENTITY_TYPE)) {
            for(ResourceLocation key : RegisterManager.ENTITY_BLOCKS.keySet()) {
                if (BuiltInRegistries.BLOCK_ENTITY_TYPE.containsKey(ResourceKey.create(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), key))) {
                    ids.remove(BuiltInRegistries.BLOCK_ENTITY_TYPE.getId(ResourceKey.create(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), key)));
                    aliases.remove(key);
                }
            }
        }
    }
}
