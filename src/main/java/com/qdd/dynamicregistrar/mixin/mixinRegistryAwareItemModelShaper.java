package com.qdd.dynamicregistrar.mixin;

import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import com.qdd.dynamicregistrar.manage.RegisterManager;
import net.neoforged.neoforge.client.model.RegistryAwareItemModelShaper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(RegistryAwareItemModelShaper.class)
public class mixinRegistryAwareItemModelShaper extends ItemModelShaper {
    public mixinRegistryAwareItemModelShaper(ModelManager modelManager) {
        super(modelManager);
    }
    @Final
    @Shadow
    private Map<Item, ModelResourceLocation> locations;

    @Shadow
    @Final
    private Map<Item, BakedModel> models;

    @Inject(method = "getItemModel", at = @At("HEAD"), cancellable = true)
    private void getItemModel(Item item, CallbackInfoReturnable<BakedModel> cir) {
        if (RegisterManager.ITEMS.containsValue(item) && !locations.containsKey(item)) {
            ModelResourceLocation mr = new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(item), "inventory");
            cir.setReturnValue(this.getModelManager().getModel(mr));
            locations.put(item, mr);
            models.put(item, this.getModelManager().getModel(mr));
        }
    }

    /**
     * 在 rebuildCache 之后，预加载所有动态注册的物品模型到缓存中
     * 这样可以确保盔甲等物品在第一次 F3+T 后就能正确显示
     */
    @Inject(method = "rebuildCache", at = @At("RETURN"))
    private void afterRebuildCache(CallbackInfo ci) {
        // 遍历所有动态注册的物品
        for (Map.Entry<ResourceLocation, Item> entry : RegisterManager.ITEMS.entrySet()) {
            Item item = entry.getValue();

            // 如果该物品还没有在 locations 缓存中，则预加载
            if (!locations.containsKey(item)) {
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
                ModelResourceLocation modelLocation = new ModelResourceLocation(itemId, "inventory");

                // 尝试从 ModelManager 获取模型
                BakedModel model = this.getModelManager().getModel(modelLocation);

                // 将模型位置添加到缓存中
                locations.put(item, modelLocation);
                models.put(item, model);
            }
        }
    }
}