# EMC × Refined Storage

**AI-CREATED MOD** — the code, feature design, documentation and cover art of this mod were created and implemented by AI.

**Project summary:** A blockless ProjectE × Refined Storage bridge with automatic EMC buy/sell, live EMC balance, stable quantity sync, NBT/enchantment protection, a Transmutation Table progression gate, one-time catch-up settlement and a learned-item learning panel. For Minecraft 1.20.1 / Forge.

- Mod ID: `emc_rs`
- Version: `3.5.1`
- License: MIT
- Minecraft: `1.20.1`
- Loader: Forge `47.x`
- Requires: ProjectE `1.20.1-PE1.0.1`, Refined Storage `1.12.4`
- Source: https://github.com/AuZn111/emc-rs

---

## Overview

Most ProjectE × Refined Storage bridges require an extra EMC interface block per network and a manual buy/sell mode switch.

This mod instead attaches the EMC shop to the **RS network itself**:

- No new block, no extra terrain.
- One RS network has one EMC owner.
- The terminal shows the owner's learned items that can be bought with EMC.
- Items inserted into the network can be converted to EMC automatically.
- Extraction consumes real RS stock first and only spends EMC for the remainder.

The result is a clean, blockless bridge that feels like a natural part of Refined Storage — with a progression gate so it does not skip ProjectE's early game.

---

## Features

### Blockless network-level EMC shop

- Virtual EMC stock is injected into the RS storage cache.
- The terminal shows learned items with an EMC value, minus the `buy` blacklist.
- No EMC interface block to craft or place.

### Automatic insert handling

- Items inserted into the RS network pass through an auto-recycle storage first.
- Ordinary items with an EMC value can be converted into the network owner's EMC.
- Items with no EMC value are never consumed; they are passed to normal RS storage.

### Automatic extraction with real stock first

- Real RS stock is consumed first.
- EMC is spent only for the remainder.
- Buy-blacklisted items never appear in the virtual shop (real stock is still extractable).

### Transmutation Table progression gate (3.3.0)

- Until the owner has **ever obtained** a ProjectE **Transmutation Table** or **Transmutation Tablet**, the virtual EMC shop and EMC extraction are locked.
- While locked:
  - the terminal does not show the virtual EMC item list;
  - no EMC can be spent;
  - by default, inserting EMC items only **records knowledge and stores them normally** — no EMC is produced;
  - Ctrl bulk recycle is blocked.
- Unlocking is permanent per player and is detected from:
  - a table/tablet in the player's inventory;
  - a placed Transmutation Table;
  - the ProjectE advancement for the table/tablet.
- Once unlocked, normal automatic EMC buy/sell behavior returns.
- **Catch-up settlement (3.4.0):** on first unlock, the network's real stored stock is scanned once and eligible items are converted to EMC. Sell-blacklisted, NBT and no-EMC items are skipped; the table/tablet itself is always protected.

### Learning panel and craft auto-learn (3.5.1)

- A new **Learning Bar** side button in RS terminals opens the **actual ProjectE Transmutation Table interface** — the left 8-slot input circle/learning area and the right learned-item outputs.
- This is the same table GUI as the block/tablet, not a custom list. The owner must have unlocked the table gate before it opens.
- Taking a result from an RS crafting terminal (single or Shift crafting) automatically records the crafted output into ProjectE knowledge. Automatic crafting tasks do not trigger this.
- Config: `[learning] learnOnCraft = true`.

### NBT / enchantment protection (3.2.2)

- Items with NBT data — enchantments, damage, custom names, potions, custom data — are **not auto-converted by default**.
- They enter normal RS storage with their NBT intact.
- Truly EMC-less items are never consumed.
- `sellNbtItems = true` can force NBT items to be auto-sold, but this discards enchantments/durability and is not recommended.

### Live EMC balance and stable quantities

- A gold **EMC balance** line is shown in the RS terminal above the player inventory.
- Virtual purchase quantities update about once per second from live EMC.
- Quantities are updated with RS's native item-delta messages, not by rebuilding the cache. Entry UUIDs stay stable, so accurate counts and reliable extraction work together.

### Blacklists and terminal button

- `buy` blacklist: items that cannot be bought with EMC.
- `sell` blacklist: items that are never auto-converted to EMC and are stored normally.
- EMC side button in block RS terminals:
  - Click: status; first click binds the owner.
  - Shift + Click: rebind owner (current owner or operator).
  - Ctrl + Click: recycle real network stock matching the terminal's four filter slots.

---

## Requirements

| Requirement | Version |
| --- | --- |
| Minecraft | 1.20.1 |
| Forge | 47.x |
| ProjectE | 1.20.1-PE1.0.1 |
| Refined Storage | 1.12.4 |
| Java | 17 |

---

## Installation

1. Install Minecraft 1.20.1 + Forge 47.x.
2. Put these jars in `mods/`:
   - `emc_rs-3.5.1.jar`
   - `refinedstorage-1.12.4.jar`
   - `ProjectE-1.20.1-PE1.0.1.jar`
3. Launch the game. No new block or item has to be crafted.

---

## How to use

1. Place an RS controller and connect an RS terminal plus **real storage** (storage disk/block).
2. You become the network EMC owner when you place the controller. If the network was placed by a command/structure, open a block RS terminal and click the EMC button once to bind.
3. **Before obtaining a Transmutation Table/Tablet:**
   - the terminal does not show EMC purchases;
   - inserting items only records knowledge and stores them normally;
   - EMC extraction is locked.
4. **After obtaining a table/tablet:**
   - the player is permanently unlocked;
   - the terminal shows EMC purchases;
   - extraction uses real stock first, then EMC;
   - ordinary non-NBT items can be auto-converted on insert.
5. Bulk recycle: put items into the terminal's four filter slots and Ctrl-click the EMC button.

> **Important:** the virtual EMC storage is not a replacement for real RS storage.
> Items without EMC, or NBT items, need a real storage disk/block with free space. Without one, they return to the player.

---

## Configuration

`config/emc_rs-common.toml` is generated on first launch:

```toml
[blacklist]
    buy = []
    sell = []

[automation]
    sellNbtItems = false

[progression]
    requireTransmutationTable = true
    convertWithoutTable = false
    convertExistingStock = true

[learning]
    learnOnCraft = true
```

| Option | Default | Purpose |
| --- | --- | --- |
| `blacklist.buy` | empty | Items that cannot be bought with EMC |
| `blacklist.sell` | empty | Items that are never auto-converted; stored normally |
| `automation.sellNbtItems` | false | If true, NBT items are auto-sold too (loses enchantments/durability) |
| `progression.requireTransmutationTable` | true | Locks EMC extraction until a table/tablet is obtained |
| `progression.convertWithoutTable` | false | While locked, inserts only learn + store (no EMC); true still converts but extraction stays locked |
| `progression.convertExistingStock` | true | On first unlock, convert eligible real stored stock once |
| `learning.learnOnCraft` | true | Record the output when taking a result from an RS crafting terminal |

Entries in blacklists can be item registry names or `#tags`:

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

Restart the game/server after editing.

---

## Important notes and limitations

- **AI-created:** code, feature design, documentation and cover art were created and implemented by AI.
- **Real RS storage is required for real items.** The virtual EMC storage only holds EMC-purchasable virtual entries.
- **Single-player focused:** one network uses one owner's ProjectE knowledge/EMC. The owner must be online.
- **The owner is the controller placer** (or the player who first clicks the EMC button).
- **Only the player's base EMC is used.** EMC inside transmutation-table input slots or Klein Stars is not read.
- **The EMC button is only added to block RS terminals.** Portable/wireless terminals can see and pull virtual items but have no button.
- **Items only; no fluid support.**
- **Version lock:** 1.20.1 + RS 1.12.x + ProjectE 1.0.x only.

---

## FAQ / troubleshooting

### Why can't I insert items without EMC or with NBT?

Check that the network has a real Refined Storage disk/block with free space. The virtual EMC storage does not store real items; items bounce back without a real storage target.

### Why is the EMC shop empty?

The owner has not yet obtained a Transmutation Table or Transmutation Tablet. This is the 3.3.0 progression gate. Obtain either item (or place the table) and the player unlocks permanently.

### Why did my enchanted item disappear?

On 3.2.1 and older, enchanted items could be auto-sold using their base item value. 3.2.2+ protects all NBT items by default. Check that you are on 3.5.0 and `sellNbtItems = false`.

### Why don't counts refresh immediately?

Virtual quantities update about once per second. Reopening the terminal sends a full item list if needed.

### Can I disable the progression gate?

Set `progression.requireTransmutationTable = false`. To keep the gate but still produce EMC before the table, set `convertWithoutTable = true`.

---

## Changelog

- 3.5.1: Learning Bar now opens the actual ProjectE Transmutation Table GUI (left input circle); removed the custom panel.
- 3.5.0: learned-item panel; record cursor/filter items; automatic learning of RS crafting-terminal output.
- 3.4.0: one-time catch-up settlement of real network stock on first unlock; `convertExistingStock` option.
- 3.3.0: Transmutation Table/Tablet progression gate; no-table inserts only record + store by default; new `[progression]` config; bulk recycle/status respect the gate.
- **3.2.2**: NBT/enchantment protection by default; EMC-value check prevents consuming EMC-less items; `sellNbtItems` option.
- **3.2.1**: AI attribution in metadata/docs/cover.
- **3.2.0**: virtual quantities sync incrementally via RS item-delta messages; stable UUIDs; accurate counts and reliable extraction.
- **3.1.0**: live EMC balance label in RS terminals.
- **3.0.1**: stopped rebuilding the cache on every EMC change.
- **3.0.0**: blockless automatic EMC buy/sell network storage.

---

## Links

- GitHub: https://github.com/AuZn111/emc-rs
- Releases: https://github.com/AuZn111/emc-rs/releases
- License: MIT
- This is a third-party addon; it is not affiliated with ProjectE or Refined Storage. Their binaries are not redistributed here.

---

## 中文简介（简）

**AI 创作**的 ProjectE × Refined Storage 联动，不需要额外的 EMC 接口方块。

- 存入可自动转 EMC，取出真实库存优先、不足扣 EMC。
- 未获得转化桌/便携转化桌时，EMC 取出锁定；默认存入只记录知识并正常存储，不产 EMC。
- 附魔/耐久/药水/自定义 NBT 默认保护，不会自动吞掉。
- 终端显示 EMC 余额，可购买数量每约 1 秒同步。
- **需要真实 RS 存储盘/存储块**；无 EMC/NBT 物品没有真实存储会退回背包。

适用于 Minecraft 1.20.1 / Forge，前置 ProjectE 与 Refined Storage。
