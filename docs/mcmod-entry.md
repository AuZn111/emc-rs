# 等价交换 × 精致存储（EMC × Refined Storage）

> **AI 创作声明**：本模组的代码、功能设计、文档与封面均由 **AI 创作并实现**。

## 基本信息

| 项目 | 内容 |
| --- | --- |
| 中文名称 | 等价交换 × 精致存储 |
| 英文名称 | EMC × Refined Storage |
| Mod ID | `emc_rs` |
| 当前版本 | 3.3.0 |
| 作者 | EMC-RS Team（AI 创作） |
| 支持版本 | Minecraft 1.20.1 |
| 运行环境 | Forge 47.x |
| 前置模组 | ProjectE 1.20.1-PE1.0.1、Refined Storage 1.12.4 |
| 许可证 | MIT |
| 开源地址 | https://github.com/AuZn111/emc-rs |
| 下载地址 | https://github.com/AuZn111/emc-rs/releases |
| 封面 | `docs/cover.png` |

## 模组简介

一个面向 **Minecraft 1.20.1 / Forge** 的 **等价交换（ProjectE）× 精致存储（Refined Storage）** 联动模组。

不需要额外的 EMC 接口方块：虚拟 EMC 库存直接挂到 RS 网络上。

- **存入自动转 EMC**：插入 RS 网络、有 ProjectE 回收价值且不带 NBT 的物品，会自动转化为网络主人的 EMC。
- **附魔/NBT 保护**：附魔、耐久、药水、自定义 NBT 的物品默认不会被自动回收，会正常进入 RS 存储并保留 NBT。
- **无 EMC 物品**：真正没有 EMC 价值的物品不会被消耗，会交给普通 RS 存储；网络里需要有存储盘/存储块和空间。
- **取出自动扣 EMC**：RS 终端显示已学习物品；真实库存优先，不足部分按 EMC 价值扣主人 EMC。
- **进度门槛**：未获得转化桌或便携转化桌时，终端不显示 EMC 商品、不能用 EMC 取出；默认存入只记录知识并正常存储，不产 EMC。
- **获得后永久解锁**：背包里有转化桌/便携转化桌、放置过转化桌、或完成对应进度，即按玩家永久解锁。
- **RS 终端显示 EMC 余额**：物品栏上方右侧显示金色 EMC 余额。
- **数量实时同步**：虚拟可购买数量每约 1 秒按实时 EMC 增量更新，不重建缓存、不改变物品 ID，数量准确且取物不会因为刷新失败。
- **buy / sell 黑名单**：`config/emc_rs-common.toml` 可配置不想买入、不想被自动回收的物品。
- **EMC 侧边按钮**：状态/首次绑定、Shift 重绑主人、Ctrl 按过滤槽批量回收真实库存。

## 使用流程（单人）

1. 安装 Forge 1.20.1 与两个前置模组，把 `emc_rs-3.3.0.jar` 放入 `mods/`。
2. 正常放置 RS 控制器，接上 RS 终端和存储。控制器由你放置时会自动成为 EMC 主人；否则打开方块终端点一次 EMC 按钮绑定。
3. 打开 RS 终端：已学习物品直接显示，取出时真实库存优先，不足扣 EMC。
4. 往网络里放物品：有 EMC 价值且不在 `sell` 黑名单里的普通物品会自动变成 EMC；带 NBT 的物品默认保留。
5. 需要批量回收已有库存时，把物品放入终端右侧 4 个过滤槽，Ctrl 点击 EMC 按钮。
6. 未获得转化桌/便携转化桌时 EMC 取出锁定；获得后自动恢复。

## 黑名单配置

文件 `config/emc_rs-common.toml` 首次启动生成：

```toml
[blacklist]
buy = []
sell = []

[automation]
sellNbtItems = false

[progression]
requireTransmutationTable = true
convertWithoutTable = false
```

支持物品 ID 和 `#tag`：

```toml
[blacklist]
buy = [
    "minecraft:bedrock",
    "#minecraft:logs"
]
sell = [
    "refinedstorage:1k_storage_disk",
    "refinedstorage:64k_storage_disk",
    "minecraft:netherite_pickaxe"
]
```

- `buy`：不进入虚拟 EMC 商店，不能用 EMC 买（真实库存仍可取出）。
- `sell`：插入网络时不会被自动转成 EMC，正常存储。
- `automation.sellNbtItems`：默认 `false`；带附魔/耐久/自定义 NBT 的物品不会被自动回收。设为 `true` 会强制自动回收，附魔和 NBT 会丢失。
- `progression.requireTransmutationTable`：默认 `true`；未获得转化桌/便携转化桌时锁定 EMC 取出。
- `progression.convertWithoutTable`：默认 `false`；未获得桌子时存入只学习+存储、不产 EMC。设为 `true` 则仍自动转 EMC，但仍不能用 EMC 取出。
- 修改后需重启游戏/服务器。

## 重要说明

- **默认 `sell` 黑名单为空**：普通有 EMC 价值的物品一进网络就会变成 EMC。带 NBT 的物品默认受保护；机器、存储盘、工具等普通物品可加入 `sell`。
- **进度门槛**：未获得转化桌/便携转化桌时不能用 EMC 取出；默认存入只记录知识并正常存储，不产 EMC。
- **无 EMC 物品需要真实存储**：网络里必须有可用的 RS 存储盘/存储块且有空间，否则物品会弹回背包。
- 仅面向单人使用优化；一个网络使用一个 EMC 主人的知识/EMC，主人需要在线。
- 只读取玩家面板上的基础 EMC，不读取转化桌输入槽或 Klein Star 内的 EMC。
- EMC 按钮只加在方块 RS 终端上；便携/无线终端可以看物品、按 EMC 取出，但没有按钮。
- 只处理物品，不处理流体。
- 仅适用于 1.20.1 + RS 1.12.x + ProjectE 1.0.x。

## 安装

1. Minecraft 1.20.1 + Forge 47.x。
2. `mods/` 放入：
   - `emc_rs-3.2.1.jar`
   - `refinedstorage-1.12.4.jar`
   - `ProjectE-1.20.1-PE1.0.1.jar`
3. 启动游戏，不需要合成新方块。

## 声明

- 本模组为 **AI 创作**。
- 本项目是 ProjectE 与 Refined Storage 的第三方联动，不隶属于这两个项目；前置模组归各自作者所有。
- 许可证：MIT。
