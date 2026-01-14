package com.qdd.dynamicregistrar.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.qdd.dynamicregistrar.item.CustomProperties;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据包创建界面 - 允许玩家在游戏内创建自定义物品数据包
 * 支持选项卡切换和滚动功能
 */
@OnlyIn(Dist.CLIENT)
public class DatapackExportScreen extends Screen {
    private static final Component TITLE = Component.translatable("gui.dynamicregistrar.datapack_export.title");

    private final Screen parent;

    // 选项卡相关
    private static final String[] TABS = {"Items", "Blocks"};
    private int currentTab = 0;
    private List<Button> tabButtons = new ArrayList<>();

    // 滚动面板
    private ScrollPanel scrollPanel;

    // 数据包设置（所有选项卡共享）
    private EditBox namespaceEditBox;
    private EditBox datapackNameEditBox;
    private EditBox descriptionEditBox;

    // 物品基本信息（Items选项卡）
    private EditBox itemIdEditBox;
    private EditBox itemTypeEditBox;
    private EditBox curiosTypeEditBox;
    private EditBox maxStackSizeEditBox;
    private EditBox maxDamageEditBox;
    private EditBox rarityEditBox;

    // 复选框（Items选项卡）
    private Button canRepairButton;
    private Button fireResistantButton;

    // 导出按钮（Items选项卡）
    private Button exportItemButton;
    private Button exportArmorButton;
    private Button exportToolButton;

    // 返回按钮
    private Button backButton;

    private String statusMessage = "";
    private int statusColor = 0xFFFFFF;

    private boolean canRepair = true;
    private boolean fireResistant = false;

    // 布局常量
    private static final int TAB_HEIGHT = 25;
    private static final int TAB_WIDTH = 100;
    private static final int CONTENT_TOP = 35;
    private static int CONTENT_HEIGHT = 200;
    private static int CONTENT_WIDTH = 420;

    public DatapackExportScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;

        // 创建选项卡按钮
        createTabButtons(centerX);
        CONTENT_HEIGHT = this.height - CONTENT_TOP - 35;
        CONTENT_WIDTH = this.width - 20;

        // 创建滚动面板
        scrollPanel = new ScrollPanel(this.minecraft, CONTENT_WIDTH, CONTENT_HEIGHT, CONTENT_TOP, centerX - CONTENT_WIDTH / 2);
        this.addRenderableWidget(scrollPanel);

        // 初始化滚动面板内容
        initScrollPanelContent();

        // 返回按钮
        this.backButton = Button.builder(
                Component.translatable("gui.done"),
                button -> this.onClose()
        ).bounds(centerX - 100, this.height - 25, 200, 20).build();
        this.addRenderableWidget(this.backButton);
    }

    private void createTabButtons(int centerX) {
        int totalWidth = TABS.length * TAB_WIDTH;
        int startX = centerX - totalWidth / 2;

        for (int i = 0; i < TABS.length; i++) {
            final int tabIndex = i;
            Button tabButton = Button.builder(
                    Component.literal(TABS[i]),
                    button -> switchTab(tabIndex)
            ).bounds(startX + i * TAB_WIDTH, 5, TAB_WIDTH, TAB_HEIGHT).build();
            tabButtons.add(tabButton);
            this.addRenderableWidget(tabButton);
        }
        updateTabButtonStyles();
    }

    private void switchTab(int tabIndex) {
        currentTab = tabIndex;
        updateTabButtonStyles();
        initScrollPanelContent();
    }

    private void updateTabButtonStyles() {
        for (int i = 0; i < tabButtons.size(); i++) {
            if (i == currentTab) {
                tabButtons.get(i).setAlpha(255);
            } else {
                tabButtons.get(i).setAlpha(150);
            }
        }
    }

    private void initScrollPanelContent() {
        scrollPanel.clearChildren();

        int centerX = CONTENT_WIDTH / 2;
        int startY = CONTENT_TOP + 10;
        int leftColumnX = centerX - 165;
        int rightColumnX = centerX + 15;

        // 数据包设置（所有选项卡共享）
        this.namespaceEditBox = new EditBox(this.font, leftColumnX, startY, 150, 20,
                Component.translatable("gui.dynamicregistrar.datapack_export.namespace"));
        this.namespaceEditBox.setValue("custom");
        scrollPanel.addRenderableWidget(this.namespaceEditBox);

        this.datapackNameEditBox = new EditBox(this.font, leftColumnX, startY + 30, 150, 20,
                Component.translatable("gui.dynamicregistrar.datapack_export.name"));
        this.datapackNameEditBox.setValue("custom_items");
        scrollPanel.addRenderableWidget(this.datapackNameEditBox);

        this.descriptionEditBox = new EditBox(this.font, leftColumnX, startY + 60, 150, 20,
                Component.translatable("gui.dynamicregistrar.datapack_export.description"));
        this.descriptionEditBox.setValue("Custom items datapack");
        scrollPanel.addRenderableWidget(this.descriptionEditBox);

        // 根据当前选项卡添加不同内容
        if (currentTab == 0) {
            initItemsTabContent(leftColumnX, rightColumnX, startY);
        } else if (currentTab == 1) {
            initBlocksTabContent(leftColumnX, rightColumnX, startY);
        }
    }

    private void initItemsTabContent(int leftColumnX, int rightColumnX, int startY) {
        // 物品设置 - 左列
        int itemStartY = startY + 100;
        this.itemIdEditBox = new EditBox(this.font, leftColumnX, itemStartY, 150, 20,
                Component.translatable("gui.dynamicregistrar.datapack_export.item_id"));
        this.itemIdEditBox.setValue("example_item");
        scrollPanel.addRenderableWidget(this.itemIdEditBox);

        this.itemTypeEditBox = new EditBox(this.font, leftColumnX, itemStartY + 30, 150, 20,
                Component.translatable("gui.dynamicregistrar.datapack_export.item_type"));
        this.itemTypeEditBox.setValue("");
        scrollPanel.addRenderableWidget(this.itemTypeEditBox);

        this.curiosTypeEditBox = new EditBox(this.font, leftColumnX, itemStartY + 60, 150, 20,
                Component.translatable("gui.dynamicregistrar.datapack_export.curios_type"));
        this.curiosTypeEditBox.setValue("");
        scrollPanel.addRenderableWidget(this.curiosTypeEditBox);

        // 物品设置 - 右列
        this.maxStackSizeEditBox = new EditBox(this.font, rightColumnX, itemStartY, 150, 20,
                Component.translatable("gui.dynamicregistrar.datapack_export.max_stack_size"));
        this.maxStackSizeEditBox.setValue("64");
        scrollPanel.addRenderableWidget(this.maxStackSizeEditBox);

        this.maxDamageEditBox = new EditBox(this.font, rightColumnX, itemStartY + 30, 150, 20,
                Component.translatable("gui.dynamicregistrar.datapack_export.max_damage"));
        this.maxDamageEditBox.setValue("0");
        scrollPanel.addRenderableWidget(this.maxDamageEditBox);

        this.rarityEditBox = new EditBox(this.font, rightColumnX, itemStartY + 60, 150, 20,
                Component.translatable("gui.dynamicregistrar.datapack_export.rarity"));
        this.rarityEditBox.setValue("COMMON");
        scrollPanel.addRenderableWidget(this.rarityEditBox);

        // 复选框按钮 - 右列
        int buttonY = itemStartY + 90;
        this.canRepairButton = Button.builder(
                Component.translatable("gui.dynamicregistrar.datapack_export.can_repair", canRepair ? Component.translatable("gui.dynamicregistrar.datapack_export.yes") : Component.translatable("gui.dynamicregistrar.datapack_export.no")),
                button -> {
                    canRepair = !canRepair;
                    canRepairButton.setMessage(Component.translatable("gui.dynamicregistrar.datapack_export.can_repair", canRepair ? Component.translatable("gui.dynamicregistrar.datapack_export.yes") : Component.translatable("gui.dynamicregistrar.datapack_export.no")));
                }
        ).bounds(rightColumnX, buttonY, 150, 20).build();
        scrollPanel.addRenderableWidget(this.canRepairButton);

        this.fireResistantButton = Button.builder(
                Component.translatable("gui.dynamicregistrar.datapack_export.fire_resistant", fireResistant ? Component.translatable("gui.dynamicregistrar.datapack_export.yes") : Component.translatable("gui.dynamicregistrar.datapack_export.no")),
                button -> {
                    fireResistant = !fireResistant;
                    fireResistantButton.setMessage(Component.translatable("gui.dynamicregistrar.datapack_export.fire_resistant", fireResistant ? Component.translatable("gui.dynamicregistrar.datapack_export.yes") : Component.translatable("gui.dynamicregistrar.datapack_export.no")));
                }
        ).bounds(rightColumnX, buttonY + 25, 150, 20).build();
        scrollPanel.addRenderableWidget(this.fireResistantButton);

        // 导出按钮 - 左列
        int exportButtonY = itemStartY + 90;
        this.exportItemButton = Button.builder(
                Component.translatable("gui.dynamicregistrar.datapack_export.export_item"),
                button -> exportCustomItem()
        ).bounds(leftColumnX, exportButtonY, 150, 20).build();
        scrollPanel.addRenderableWidget(this.exportItemButton);

        this.exportArmorButton = Button.builder(
                Component.translatable("gui.dynamicregistrar.datapack_export.export_armor"),
                button -> exportCustomArmor()
        ).bounds(leftColumnX, exportButtonY + 25, 150, 20).build();
        scrollPanel.addRenderableWidget(this.exportArmorButton);

        this.exportToolButton = Button.builder(
                Component.translatable("gui.dynamicregistrar.datapack_export.export_tool"),
                button -> exportCustomTool()
        ).bounds(leftColumnX, exportButtonY + 50, 150, 20).build();
        scrollPanel.addRenderableWidget(this.exportToolButton);
    }

    private void initBlocksTabContent(int leftColumnX, int rightColumnX, int startY) {
        // 方块选项卡内容（待实现）
        // 这里可以添加方块相关的输入框和按钮
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTitle(guiGraphics);
        this.renderStatus(guiGraphics);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderTitle(GuiGraphics guiGraphics) {
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 5, 0xFFFFFF);
    }

    private void renderStatus(GuiGraphics guiGraphics) {
        if (!statusMessage.isEmpty()) {
            guiGraphics.drawCenteredString(this.font,
                    Component.literal(statusMessage),
                    this.width / 2, this.height - 70, statusColor);
        }
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }

    private void exportCustomItem() {
        String namespace = namespaceEditBox.getValue();
        String datapackName = datapackNameEditBox.getValue();
        String description = descriptionEditBox.getValue();

        CustomProperties properties = DatapackExporter.createCustomProperties(
                namespace,
                itemIdEditBox.getValue(),
                itemTypeEditBox.getValue(),
                curiosTypeEditBox.getValue(),
                DatapackExporter.parseIntSafe(maxStackSizeEditBox.getValue(), 64),
                DatapackExporter.parseIntSafe(maxDamageEditBox.getValue(), 0),
                rarityEditBox.getValue(),
                canRepair,
                fireResistant
        );

        DatapackExporter exporter = new DatapackExporter(namespace, datapackName, description);
        exporter.exportItem(properties, message -> {
            setStatus(message, 0x55FF55);
        });
    }

    private void exportCustomArmor() {
        String namespace = namespaceEditBox.getValue();
        String datapackName = datapackNameEditBox.getValue();
        String description = descriptionEditBox.getValue();

        CustomProperties customProps = DatapackExporter.createCustomProperties(
                namespace,
                itemIdEditBox.getValue(),
                itemTypeEditBox.getValue(),
                curiosTypeEditBox.getValue(),
                DatapackExporter.parseIntSafe(maxStackSizeEditBox.getValue(), 64),
                DatapackExporter.parseIntSafe(maxDamageEditBox.getValue(), 0),
                rarityEditBox.getValue(),
                canRepair,
                fireResistant
        );

        DatapackExporter exporter = new DatapackExporter(namespace, datapackName, description);
        exporter.exportArmor(customProps, message -> {
            setStatus(message, 0x55FF55);
        });
    }

    private void exportCustomTool() {
        String namespace = namespaceEditBox.getValue();
        String datapackName = datapackNameEditBox.getValue();
        String description = descriptionEditBox.getValue();

        CustomProperties customProps = DatapackExporter.createCustomProperties(
                namespace,
                itemIdEditBox.getValue(),
                itemTypeEditBox.getValue(),
                curiosTypeEditBox.getValue(),
                DatapackExporter.parseIntSafe(maxStackSizeEditBox.getValue(), 64),
                DatapackExporter.parseIntSafe(maxDamageEditBox.getValue(), 0),
                rarityEditBox.getValue(),
                canRepair,
                fireResistant
        );

        DatapackExporter exporter = new DatapackExporter(namespace, datapackName, description);
        exporter.exportTool(customProps, message -> {
            setStatus(message, 0x55FF55);
        });
    }

    private void setStatus(String message, int color) {
        this.statusMessage = message;
        this.statusColor = color;
    }
}
