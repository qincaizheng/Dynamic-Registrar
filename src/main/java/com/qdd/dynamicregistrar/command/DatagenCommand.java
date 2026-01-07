package com.qdd.dynamicregistrar.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.qdd.dynamicregistrar.DynamicRegistrar;
import com.qdd.dynamicregistrar.datagen.HotDatagen;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * 数据生成命令 - 允许在游戏内触发模型生成
 */
@EventBusSubscriber(modid = DynamicRegistrar.MODID)
public class DatagenCommand {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(
            Commands.literal("dynamicregistrar")
                .then(Commands.literal("datagen")
                    .requires(source -> source.hasPermission(4)) // 需要OP权限
                    .executes(DatagenCommand::executeDatagen)
                )
        );
    }

    private static int executeDatagen(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();

        try {
            source.sendSuccess(() -> Component.literal("§a开始生成物品模型..."), true);

            // 运行热数据生成
            HotDatagen.run(source.getServer().getResourceManager());

            source.sendSuccess(() -> Component.literal("§a模型生成完成！请使用 F3+T 重新加载资源包"), true);
            source.sendSuccess(() -> Component.literal("§e或者在选项 -> 资源包中启用 'dynamic_models' 资源包"), false);

            return 1;
        } catch (Exception e) {
            DynamicRegistrar.LOGGER.error("生成模型时出错", e);
            source.sendFailure(Component.literal("§c生成模型失败: " + e.getMessage()));
            return 0;
        }
    }
}
