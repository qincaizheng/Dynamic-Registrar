# Datapack Creator GUI

这个GUI系统允许玩家在游戏内创建自定义物品数据包，而不是导出已有的物品。

## 功能

1. **创建普通物品** - 根据玩家输入的参数创建新的普通物品
2. **创建盔甲** - 根据玩家输入的参数创建新的盔甲套装
3. **创建工具** - 根据玩家输入的参数创建新的工具套装
4. **选项卡切换** - 支持在物品、方块等不同类型之间切换
5. **滚动支持** - 当内容过多时支持滚动查看

所有创建的物品都使用Codec自动转换为JSON格式。

## 界面架构

### 选项卡系统
- **Items Tab (物品选项卡)**: 创建物品相关内容
- **Blocks Tab (方块选项卡)**: 创建方块相关内容（待实现）
- 可扩展更多选项卡类型

### 滚动容器
- 使用 `ScrollPanel` 或自定义滚动容器包裹内容
- 支持鼠标滚轮滚动
- 自动计算内容高度并显示滚动条

## 使用方法

### 方法1: 通过主菜单按钮
在游戏主菜单中，点击"Create Datapack"按钮打开创建界面。

### 方法2: 通过暂停菜单按钮
在游戏中按ESC打开暂停菜单，点击"Create Datapack"按钮打开创建界面。

### 方法3: 通过命令
在游戏中输入命令：
```
/dynamicregistrar export
```
需要OP权限（权限等级2）。

## 创建界面说明

### 选项卡区域
- **Items**: 物品创建选项卡
- **Blocks**: 方块创建选项卡（待实现）

### 数据包设置（所有选项卡共享）
- **Namespace (命名空间)**: 数据包的命名空间，默认为"custom"
- **Datapack Name (数据包名称)**: 数据包的文件夹名称，默认为"custom_items"
- **Description (描述)**: 数据包的描述信息

### 物品设置（Items选项卡）
- **Item ID (物品ID)**: 物品的ID，例如"example_item"
- **Item Type (物品类型)**: 物品类型（可选）
- **Curios Type (饰品类型)**: Curios饰品类型（可选）
- **Max Stack Size (最大堆叠数)**: 最大堆叠数量，默认为64
- **Max Damage (最大耐久度)**: 最大耐久度，默认为0（无耐久）
- **Rarity (稀有度)**: 稀有度，可选值：COMMON、UNCOMMON、RARE、EPIC
- **可修复**: 是否可以修复，默认为是
- **防火**: 是否防火，默认为否

### 创建按钮
- **Create Item (创建物品)**: 创建普通物品
- **Create Armor (创建盔甲)**: 创建盔甲套装（使用默认盔甲材质）
- **Create Tool (创建工具)**: 创建工具套装（使用默认工具等级）

## 创建后的数据包结构

```
datapacks/
└── custom_items/
    ├── pack.mcmeta
    └── data/
        └── custom/
            └── dynamicregistrar/
                └── items/
                    ├── item/      # 普通物品
                    ├── armor/     # 盔甲
                    └── tier/      # 工具
```

## Codec转换

所有创建的物品都使用对应的Codec自动转换为JSON：

- `CustomProperties.CODEC` - 普通物品的Codec
- `ArmorProperties.CODEC` - 盔甲的Codec
- `TierProperties.CODEC` - 工具的Codec

转换示例：
```java
JsonElement jsonElement = CustomProperties.CODEC.encodeStart(JsonOps.INSTANCE, properties)
        .getOrThrow();
```

## 注意事项

1. 创建的数据包可以直接放入服务器的datapacks文件夹中使用
2. 创建后需要在游戏中重新加载数据包才能生效
3. 如果数据包已存在，会被覆盖
4. 确保有足够的权限写入datapacks文件夹
5. 盔甲和工具使用默认的材质和等级，后续可以扩展为可自定义

## 文件说明

- `DatapackExportScreen.java` - 主创建界面（包含选项卡和滚动功能）
- `ScrollPanel.java` - 自定义滚动面板组件
- `DatapackExporter.java` - 数据包导出逻辑（包含物品、盔甲、工具的创建和导出）
- `DatapackExportButton.java` - 主菜单按钮
- `DatapackExportPauseButton.java` - 暂停菜单按钮
- `DatapackExportCommand.java` - 命令处理器
- `OpenExportScreenPacket.java` - 网络包

## 代码架构

### 职责分离
- **DatapackExportScreen**: 负责UI渲染、用户交互、选项卡切换
- **ScrollPanel**: 负责滚动容器、内容裁剪、渐变遮罩
- **DatapackExporter**: 负责数据包创建、JSON序列化、文件写入

### 类关系
```
DatapackExportScreen
    ├── 使用 ScrollPanel 作为滚动容器
    └── 使用 DatapackExporter 处理导出逻辑
```

## 实现细节

### 选项卡实现
- 使用 `TabButton` 类表示单个选项卡按钮
- 维护当前选中的选项卡索引
- 切换选项卡时重新渲染对应的内容区域

### 滚动实现
- 使用 `AbstractScrollWidget` 或自定义滚动容器
- 内容区域高度根据选项卡内容动态计算
- 支持鼠标滚轮和拖拽滚动条
- 上下边缘渐变遮罩，防止内容溢出显示

### 布局结构
```
┌─────────────────────────────────────┐
│           标题栏                     │
├─────────────────────────────────────┤
│  [Items] [Blocks] [Other...]       │  ← 选项卡按钮
├─────────────────────────────────────┤
│  ╔═══════════════════════════════╗  │  ← 上边缘渐变遮罩
│  ║                               ║  │
│  ║      滚动内容区域             ║  │  ← 可滚动区域（带裁剪）
│  ║                               ║  │
│  ║                               ║  │
│  ╚═══════════════════════════════╝  │  ← 下边缘渐变遮罩
├─────────────────────────────────────┤
│           状态信息                   │
├─────────────────────────────────────┤
│          [返回按钮]                  │
└─────────────────────────────────────┘
```

### 遮罩实现
- 使用 `enableScissor()` 裁剪超出边界的内容
- 在滚动区域顶部和底部绘制渐变遮罩
- 遮罩高度约为 10-15 像素
- 使用半透明黑色渐变（从透明到半透明）
