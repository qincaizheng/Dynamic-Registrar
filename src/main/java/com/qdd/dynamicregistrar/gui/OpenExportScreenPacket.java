package com.qdd.dynamicregistrar.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import com.qdd.dynamicregistrar.DynamicRegistrar;
import io.netty.buffer.ByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * 打开数据包导出界面的网络包
 */
public record OpenExportScreenPacket() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenExportScreenPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DynamicRegistrar.MODID, "open_export_screen"));
    public static final StreamCodec<ByteBuf, OpenExportScreenPacket> STREAM_CODEC = StreamCodec.unit(new OpenExportScreenPacket());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(OpenExportScreenPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.setScreen(new DatapackExportScreen(minecraft.screen));
        }).exceptionally(e -> {
            context.disconnect(Component.translatable("dynamicregistrar.networking.failed", e.getMessage()));
            return null;
        });
    }
}
