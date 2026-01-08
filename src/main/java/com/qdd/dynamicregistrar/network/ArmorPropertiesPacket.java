package com.qdd.dynamicregistrar.network;

import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import com.qdd.dynamicregistrar.DynamicRegistrar;
import com.qdd.dynamicregistrar.item.ArmorProperties;
import com.qdd.dynamicregistrar.manage.RegisterManager;
import io.netty.buffer.ByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record ArmorPropertiesPacket(List<ArmorProperties> syncedDataList) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ArmorPropertiesPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DynamicRegistrar.MODID, "armor_properties"));

    public static final StreamCodec<ByteBuf, ArmorPropertiesPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(ArmorProperties.CODEC).apply(ByteBufCodecs.list()),
            ArmorPropertiesPacket::syncedDataList,
            ArmorPropertiesPacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static void handle(ArmorPropertiesPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
                    RegisterManager.SYNC_ARMOR_PROPERTIES.clear();
                    RegisterManager.SYNC_ARMOR_PROPERTIES.addAll(packet.syncedDataList());
                })
                .exceptionally(e -> {
                    // Handle exception
                    context.disconnect(Component.translatable("dynamicregistrar.networking.failed", e.getMessage()));
                    return null;
                });
    }
}
