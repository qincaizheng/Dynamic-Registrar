package com.qdd.dynamicregistrar.mixin;

import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import com.qdd.dynamicregistrar.manage.RegisterManager;
import net.neoforged.neoforge.client.model.RegistryAwareItemModelShaper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(RegistryAwareItemModelShaper.class)
public class mixinRegistryAwareItemModelShaper extends ItemModelShaper {
    public mixinRegistryAwareItemModelShaper(ModelManager modelManager) {
        super(modelManager);
    }
    @Final
    @Shadow
    private  Map<Item, ModelResourceLocation> locations;

    @Inject(method = "getItemModel", at = @At("HEAD"), cancellable = true)
    private void getItemModel(Item item, CallbackInfoReturnable<BakedModel> cir) {
        if (RegisterManager.ITEMS.containsValue(item)&&!locations.containsKey(item)) {
            ModelResourceLocation mr=new ModelResourceLocation(BuiltInRegistries.ITEM.getKey(item), "inventory");
            cir.setReturnValue(this.getModelManager().getModel(mr));
            locations.put(item, mr);
        }
    }
}
