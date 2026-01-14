package com.qdd.dynamicregistrar.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义滚动面板 - 支持内容滚动显示
 * 功能：
 * - 支持鼠标滚轮滚动
 * - 支持拖拽滚动条
 * - 自动计算内容高度
 * - 内容裁剪防止溢出
 * - 上下边缘渐变遮罩
 */
public class ScrollPanel extends AbstractContainerWidget {
    private final Minecraft minecraft;
    private final List<Renderable> children = new ArrayList<>();
    private int scrollY = 0;
    private int contentHeight = 0;
    private boolean isScrolling = false;
    private int scrollBarX;
    private int scrollBarWidth = 8;
    private int scrollBarHeight;
    private boolean isDragging = false;

    public ScrollPanel(Minecraft minecraft, int width, int height, int top, int left) {
        super(left, top, width, height, Component.empty());
        this.minecraft = minecraft;
        this.scrollBarX = left + width - scrollBarWidth - 2;
        this.scrollBarHeight = height;
    }

    public void addRenderableWidget(Renderable renderable) {
        children.add(renderable);
        // Track the bottom-most widget to determine content height
        if (renderable instanceof AbstractWidget widget) {
            int widgetBottom = widget.getY() + widget.getHeight();
            if (widgetBottom > contentHeight) {
                contentHeight = widgetBottom + 10;
            }
        }
    }

    public void clearChildren() {
        children.clear();
        scrollY = 0;
        contentHeight = 0;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // Narration not implemented for this widget
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 绘制背景
        guiGraphics.fill(getX(), getY(), getX() + getWidth(), getY() + getHeight(), 0x80000000);

        // 启用裁剪，防止内容溢出
        guiGraphics.enableScissor(getX(), getY(), getX() + getWidth(), getY() + getHeight());

        // 应用滚动偏移
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0, -scrollY, 0);

        // 渲染子组件
        for (Renderable renderable : children) {
            renderable.render(guiGraphics, mouseX, mouseY + scrollY, partialTick);
        }

        guiGraphics.pose().popPose();

        // 禁用裁剪
        guiGraphics.disableScissor();

        // 绘制渐变遮罩
        renderGradientMasks(guiGraphics);

        // 绘制滚动条
        renderScrollBar(guiGraphics);
    }

    private void renderGradientMasks(GuiGraphics guiGraphics) {
        int maxScroll = Math.max(0, contentHeight - getHeight());
        int maskHeight = 15;

        // 上边缘渐变遮罩（仅当可以向上滚动时显示）
        if (scrollY > 0) {
            for (int i = 0; i < maskHeight; i++) {
                int alpha = (int) (255 * (1.0f - (float) i / maskHeight) * 0.6f);
                int color = (alpha << 24);
                guiGraphics.fill(getX(), getY() + i, getX() + getWidth() - scrollBarWidth - 2, getY() + i + 1, color);
            }
        }

        // 下边缘渐变遮罩（仅当可以向下滚动时显示）
        if (scrollY < maxScroll) {
            for (int i = 0; i < maskHeight; i++) {
                int alpha = (int) (255 * ((float) i / maskHeight) * 0.6f);
                int color = (alpha << 24);
                guiGraphics.fill(getX(), getY() + getHeight() - maskHeight + i, getX() + getWidth() - scrollBarWidth - 2, getY() + getHeight() - maskHeight + i + 1, color);
            }
        }
    }

    private void renderScrollBar(GuiGraphics guiGraphics) {
        int maxScroll = Math.max(0, contentHeight - getHeight());
        if (maxScroll > 0) {
            float scrollRatio = (float) scrollY / maxScroll;
            int barHeight = Math.max(20, (int) ((float) getHeight() * getHeight() / contentHeight));
            int barY = getY() + (int) ((getHeight() - barHeight) * scrollRatio);

            // 滚动条背景
            guiGraphics.fill(scrollBarX, getY(), scrollBarX + scrollBarWidth, getY() + getHeight(), 0x40000000);

            // 滚动条
            guiGraphics.fill(scrollBarX, barY, scrollBarX + scrollBarWidth, barY + barHeight, 0xFF808080);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 检查是否点击滚动条
        int maxScroll = Math.max(0, contentHeight - getHeight());
        if (maxScroll > 0 && mouseX >= scrollBarX && mouseX <= scrollBarX + scrollBarWidth) {
            isDragging = true;
            return true;
        }

        // 转换鼠标坐标并传递给子组件
        for (Renderable renderable : children) {
            if (renderable instanceof net.minecraft.client.gui.components.events.GuiEventListener listener) {
                if (listener.mouseClicked(mouseX, mouseY + scrollY, button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDragging) {
            int maxScroll = Math.max(0, contentHeight - getHeight());
            if (maxScroll > 0) {
                float scrollRatio = (float) (mouseY - getY()) / getHeight();
                scrollY = (int) (scrollRatio * maxScroll);
                scrollY = Math.max(0, Math.min(scrollY, maxScroll));
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        int maxScroll = Math.max(0, contentHeight - getHeight());
        if (maxScroll > 0) {
            scrollY -= (int) (verticalAmount * 20);
            scrollY = Math.max(0, Math.min(scrollY, maxScroll));
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Renderable renderable : children) {
            if (renderable instanceof net.minecraft.client.gui.components.events.GuiEventListener listener) {
                if (listener.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (Renderable renderable : children) {
            if (renderable instanceof net.minecraft.client.gui.components.events.GuiEventListener listener) {
                if (listener.charTyped(codePoint, modifiers)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<net.minecraft.client.gui.components.events.GuiEventListener> children() {
        List<net.minecraft.client.gui.components.events.GuiEventListener> listeners = new ArrayList<>();
        for (Renderable renderable : children) {
            if (renderable instanceof net.minecraft.client.gui.components.events.GuiEventListener listener) {
                listeners.add(listener);
            }
        }
        return listeners;
    }
}
