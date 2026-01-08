package com.qdd.dynamicregistrar.network;

import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import com.qdd.dynamicregistrar.DynamicRegistrar;
import com.qdd.dynamicregistrar.item.TierProperties;
import com.qdd.dynamicregistrar.manage.RegisterManager;
import io.netty.buffer.ByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record TierPropertiesPacket(List<TierProperties> syncedDataList) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TierPropertiesPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DynamicRegistrar.MODID, "tier_properties"));

    public static final StreamCodec<ByteBuf, TierPropertiesPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.fromCodec(TierProperties.CODEC).apply(ByteBufCodecs.list()),
            TierPropertiesPacket::syncedDataList,
            TierPropertiesPacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(TierPropertiesPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
                    RegisterManager.SYNC_TIER_PROPERTIES.clear();
                    RegisterManager.SYNC_TIER_PROPERTIES.addAll(packet.syncedDataList());
                })
                .exceptionally(e -> {
                    // Handle exception
                    context.disconnect(Component.translatable("dynamicregistrar.networking.failed", e.getMessage()));
                    return null;
        });
    }
}

