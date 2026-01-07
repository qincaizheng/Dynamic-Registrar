package com.qdd.dynamicregistrar.network;

import com.qdd.dynamicregistrar.DynamicRegistrar;
import com.qdd.dynamicregistrar.item.CustomProperties;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.HandlerThread;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = DynamicRegistrar.MODID)
public class ModNetwork {
    @SubscribeEvent // on the mod event bus
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.executesOn(HandlerThread.NETWORK);


    }



}
