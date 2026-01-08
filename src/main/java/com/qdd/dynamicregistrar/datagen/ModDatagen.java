package com.qdd.dynamicregistrar.datagen;

import com.qdd.dynamicregistrar.DynamicRegistrar;
import com.qdd.dynamicregistrar.item.ArmorProperties;
import com.qdd.dynamicregistrar.manage.ExampleRegist;
import com.qdd.dynamicregistrar.manage.RegisterManager;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Set;


@EventBusSubscriber(modid = DynamicRegistrar.MODID)
public class ModDatagen {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        event.getGenerator().addProvider(
                event.includeServer(),
                (DataProvider.Factory<DatapackBuiltinEntriesProvider>) output -> new DatapackBuiltinEntriesProvider(
                        output,
                        event.getLookupProvider(),
                        new RegistrySetBuilder().add(RegisterManager.CUSTOM_PROPERTIES_REGISTRY_KEY,bootstrapContext -> {
                                    bootstrapContext.register(ExampleRegist.EXAMPLE_CUSTOM_PROPERTIES,
                                            ExampleRegist.EXAMPLE_CUSTOM_PROPERTIES_VALUE);
                                    bootstrapContext.register(ExampleRegist.EXAMPLE_COMPLEX_CUSTOM_PROPERTIES_KEY,
                                            ExampleRegist.EXAMPLE_COMPLEX_CUSTOM_PROPERTIES);
                                })
                        .add(RegisterManager.ARMOR_PROPERTIES_REGISTRY_KEY,bootstrapContext ->
                                bootstrapContext.register(ExampleRegist.EXAMPLE_ARMOR_PROPERTIES,
                                        ExampleRegist.EXAMPLE_ARMOR_PROPERTIES_VALUE)
                        ).add(RegisterManager.TIER_PROPERTIES_REGISTRY_KEY,bootstrapContext ->
                                bootstrapContext.register(ExampleRegist.EXAMPLE_TIER_PROPERTIES,
                                        ExampleRegist.EXAMPLE_TIER_PROPERTIES_VALUE)
                        ),
                        Set.of(DynamicRegistrar.MODID)
                )
        );
    }
}
