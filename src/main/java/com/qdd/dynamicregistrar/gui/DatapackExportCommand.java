package com.qdd.dynamicregistrar.gui;

import com.qdd.dynamicregistrar.DynamicRegistrar;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * 数据包导出命令 - 允许在游戏内打开导出界面
 */
@EventBusSubscriber(modid = DynamicRegistrar.MODID)
public class DatapackExportCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
            Commands.literal("dynamicregistrar")
                .then(Commands.literal("export")
                    .requires(source -> source.hasPermission(2)) // 需要OP权限
                    .executes(DatapackExportCommand::openExportScreen)
                )
        );
    }

    private static int openExportScreen(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        try {
            // 发送数据包到客户端打开界面
            PacketDistributor.sendToPlayer(
                source.getPlayer(),
                new OpenExportScreenPacket()
            );

            source.sendSuccess(() -> Component.literal("§a正在打开数据包导出界面..."), true);
            return 1;
        } catch (Exception e) {
            DynamicRegistrar.LOGGER.error("打开导出界面时出错", e);
            source.sendFailure(Component.literal("§c打开导出界面失败: " + e.getMessage()));
            return 0;
        }
    }
}
