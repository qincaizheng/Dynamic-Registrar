package com.qdd.dynamicregistrar.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * 在主菜单添加数据包导出按钮
 */
@EventBusSubscriber(modid = "dynamicregistrar", value = Dist.CLIENT)
public class DatapackExportButton {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (event.getScreen() instanceof TitleScreen) {
            int buttonWidth = 60;
            int buttonHeight = 20;
            int x = event.getScreen().width / 2 +100 ;
            int y = event.getScreen().height / 4 + 104; // 在其他按钮下方

            Button exportButton = Button.builder(
                    Component.translatable("gui.dynamicregistrar.datapack_export.open"),
                    button -> {
                        Minecraft minecraft = Minecraft.getInstance();
                        minecraft.setScreen(new DatapackExportScreen(event.getScreen()));
                    }
            ).bounds(x, y, buttonWidth, buttonHeight).build();

            event.addListener(exportButton);
        }
    }
}
