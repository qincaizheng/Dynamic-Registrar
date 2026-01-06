package com.qdd.dynamicregistrar.datagen;

import com.qdd.dynamicregistrar.DynamicRegistrar;
import com.qdd.dynamicregistrar.manage.ExampleRegist;
import com.qdd.dynamicregistrar.manage.RegisterManager;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataProvider;
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
                        new RegistrySetBuilder().add(RegisterManager.CUSTOM_PROPERTIES_REGISTRY_KEY,bootstrapContext ->
                                bootstrapContext.register(ExampleRegist.EXAMPLE_CUSTOM_PROPERTIES,
                                        ExampleRegist.EXAMPLE_CUSTOM_PROPERTIES_VALUE)),
                        Set.of(DynamicRegistrar.MODID)
                )
        );
    }
}
