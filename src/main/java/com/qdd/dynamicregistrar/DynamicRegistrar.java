package com.qdd.dynamicregistrar;

import com.mojang.logging.LogUtils;
import com.qdd.dynamicregistrar.data.DataComponents;
import com.qdd.dynamicregistrar.item.ModCreateTab;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(DynamicRegistrar.MODID)
public class DynamicRegistrar {
    public static final String MODID = "dynamicregistrar";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DynamicRegistrar(IEventBus modEventBus, ModContainer modContainer) {
        ModCreateTab.CREATIVE_MODE_TABS.register(modEventBus);
        DataComponents.REGISTRAR.register(modEventBus);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

}
