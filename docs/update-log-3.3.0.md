# EMC × Refined Storage 3.3.0 更新日志

**AI 创作模组 / AI-created mod.**

适用于 MC百科的“更新日志”栏目，以及 CurseForge 上传文件时的 Changelog 字段。

---

## 中文（MC百科）

### 新增

- **转化桌/便携转化桌进度门槛**：未获得转化桌（Transmutation Table）或便携转化桌（Transmutation Tablet）时，虚拟 EMC 商店和 EMC 取出都会锁定。
- **按玩家永久解锁**：背包里有转化桌/平板、放置过转化桌、或完成 ProjectE 对应进度后解锁；之后不会重新锁定。
- 新增 `[progression]` 配置段：
  - `requireTransmutationTable = true`
  - `convertWithoutTable = false`

### 调整

- 未获得桌子时，默认**存入只记录知识并正常存储，不产生 EMC**。
- EMC 状态提示和 Ctrl 批量回收也会遵守进度门槛。
- 已经获得过转化桌/平板的玩家不受影响，自动买卖恢复正常。

### 保留

- 附魔/耐久/药水/自定义 NBT 默认保护，不会被自动吞掉。
- 真正没有 EMC 价值的物品绝不会被消耗。
- RS 终端实时 EMC 余额、虚拟可购买数量约 1 秒增量同步。
- 真实库存优先、`buy` / `sell` 黑名单、无额外方块。

### 修复与验证

- 阻断“未获得转化桌就能用 EMC 取出物品”的进度漏洞。
- 无桌测试：锁定、铁锭正常存入并学习、EMC 不变、无虚拟商品。
- 放桌测试：商品出现、可扣 EMC 取出钻石。
- 注意：真实物品仍需要真实 RS 存储盘/存储块；虚拟 EMC 存储不是真实存储。

---

## English (CurseForge)

### Added

- **Transmutation Table / Transmutation Tablet progression gate**: until the owner has ever obtained either item, the virtual EMC shop and EMC extraction are locked.
- **Permanent per-player unlock**, detected from the player's inventory, a placed Transmutation Table, or the ProjectE advancement.
- New `[progression]` options:
  - `requireTransmutationTable = true`
  - `convertWithoutTable = false`

### Changed

- Without a table, inserting EMC items now only records knowledge and stores them normally by default; **no EMC is produced**.
- The EMC status message and Ctrl bulk recycle respect the gate.
- Players who already obtained a table/tablet are unaffected.

### Kept

- NBT / enchantment protection by default.
- Truly EMC-less items are never consumed.
- Live EMC terminal balance and ~1-second incremental quantity sync.
- Real-stock-first extraction, `buy` / `sell` blacklists, and the blockless design.

### Fixed / verified

- Closed the "use EMC before obtaining a Transmutation Table" progression loophole.
- Locked test: iron stored and learned, EMC unchanged, no virtual shop items.
- Unlocked test: shop appears, diamond extracted, EMC spent.
- Real EMC-less/NBT items still require a real RS storage disk/block with free space.
