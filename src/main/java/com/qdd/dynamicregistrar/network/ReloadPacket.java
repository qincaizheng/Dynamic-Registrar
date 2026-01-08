package com.qdd.dynamicregistrar.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import com.qdd.dynamicregistrar.Config;
import com.qdd.dynamicregistrar.DynamicRegistrar;
import com.qdd.dynamicregistrar.manage.RegisterManager;
import io.netty.buffer.ByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ReloadPacket(int version) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ReloadPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DynamicRegistrar.MODID, "reload"));
    public static final StreamCodec<ByteBuf, ReloadPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ReloadPacket::version,
            ReloadPacket::new
    );
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ReloadPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
                    RegisterManager.reload();
                    if (!Config.ENABLE_RELOAD.get()) return;
                    Minecraft.getInstance().reloadResourcePacks();
                })
                .exceptionally(e -> {
                    // Handle exception
                    context.disconnect(Component.translatable("dynamicregistrar.networking.failed", e.getMessage()));
                    return null;
                });
    }
}
