package com.qdd.dynamicregistrar.data;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import com.mojang.serialization.Codec;
import com.qdd.dynamicregistrar.DynamicRegistrar;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class DataComponents {
    public static final DeferredRegister.DataComponents REGISTRAR = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, DynamicRegistrar.MODID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> curios = REGISTRAR.registerComponentType(
            "curios",
            builder -> builder
                    // The codec to read/write the data to disk
                    .persistent(Codec.STRING)
                    // The codec to read/write the data across the network
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                    .cacheEncoding()
    );

}
