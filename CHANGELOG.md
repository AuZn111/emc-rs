# Changelog

All notable changes to EMC × Refined Storage.

## 3.5.0

### Added

- **Learning bar:** a new side button in RS terminals opens a ProjectE-style learned-item panel with search, scrolling and item tooltips.
  - The panel has a **Record Cursor Item** button; if the cursor is empty it records the terminal's four filter-slot items.
  - Records are written to ProjectE knowledge without consuming the item or granting EMC by default.
- **Craft auto-learn:** taking a result from an RS crafting terminal (single or Shift crafting) records the crafted output into ProjectE knowledge once.
  - The output is not consumed again and no EMC is granted.
  - Automatic crafting tasks do not pass through a player, so they do not trigger this.

### Config

- `[learning] learnOnCraft` (default `true`).
- `[learning] consumeOnLearn` (default `false`; only affects the learning bar's cursor item).

### Verified

- Crafting oak planks from an oak log recorded the output (`craftLearned=true`).
- The learning action recorded a filter-slot diamond (`filterLearned=true`).
- `learnOnCraft=true`.

## 3.4.0

### Added

- One-time catch-up settlement: when the owner first unlocks the EMC shop with a Transmutation Table/Tablet, the network's **real stored stock** is scanned and eligible items are converted to EMC automatically.
- New `[progression] convertExistingStock` option (default `true`).

### Behavior

- Respects the `sell` blacklist, NBT protection and no-EMC items.
- Always protects the Transmutation Table/Tablet itself.
- Ignores virtual EMC-shop entries; only real RS storage is settled.
- Runs once per network/owner; repeating ticks do not duplicate the conversion.
- Sends the owner a chat message with the converted item count and EMC gained.

### Verified

- Test network had 2 iron ingots, 2 enchanted swords and 1 Transmutation Table before unlock.
- Catch-up converted both iron ingots (`512 EMC`), preserved the enchanted swords and the table, and a second catch-up converted `0`.
- No duplicate EMC on the second run.

## 3.3.0

### Added

- Transmutation Table / Transmutation Tablet progression gate: the virtual EMC shop and EMC extraction are locked until the owner has ever obtained either one.
- Permanent per-player unlock, detected from the player's inventory, a placed Transmutation Table, or the ProjectE advancement.
- New `[progression] requireTransmutationTable` and `convertWithoutTable` options.

### Changed

- Without a table, inserting EMC items now records knowledge and stores them normally by default; no EMC is produced (`convertWithoutTable = false`).
- Bulk recycle and the EMC status message respect the gate.
- Players who already obtained a table/tablet are unaffected and keep the normal automatic buy/sell behavior.

### Kept

- NBT / enchantment protection from 3.2.2.
- EMC-less items are never consumed.
- Live EMC balance and incremental quantity sync.
- Real-stock-first extraction, `buy` / `sell` blacklists, and the blockless design.

### Verified

- Without a table: locked, iron stored and learned, EMC unchanged, no virtual shop items.
- After adding a table: shop appears, diamond extracted, EMC spent.
- Real EMC-less/NBT items still need a real RS storage disk/block with free space.

## 3.2.2

- Items with NBT data (enchantments, damage, custom data, potions) are no longer auto-converted by default; they are stored normally and keep their NBT.
- New `[automation] sellNbtItems` option (default `false`) for users who explicitly want NBT items auto-sold.
- Auto-selling now also checks that the item has an EMC value, so truly EMC-less items are never consumed.
- EMC-less items are passed to normal RS storage; the network still needs a real storage disk/block with space.

## 3.2.1

- Added AI-created attribution to the mod metadata, README, release notes and cover.
- No gameplay changes.

## 3.2.0

- Virtual purchasable item counts now update every ~1 second from live EMC.
- Uses RS's native `GridItemDeltaMessage` deltas instead of rebuilding the storage cache.
- Item entry UUIDs remain stable, so accurate quantities and reliable extraction work together.
- Removed manual cache-refresh behavior from the EMC side button.
- Kept the in-terminal EMC balance display.

## 3.1.0

- Added the live EMC balance label to RS terminals.

## 3.0.1

- Stopped rebuilding the whole RS cache on every EMC change (prevented stale click UUIDs).
- Reduced forced cache invalidations while clicking items.

## 3.0.0

- Blockless rewrite.
- Automatic EMC buy/sell storage injected into every RS network.
- Owner = controller placer (captured via Mixin).
- `buy` / `sell` blacklists in `config/emc_rs-common.toml`.

## 2.0.0

- Removed the EMC Interface block.
- Network-level virtual storage, manual buy/sell mode button.

## 1.0.0

- Initial EMC Interface block release.

---

**AI creation notice:** this mod, including code, feature design, documentation and cover art, was created and implemented by AI.
