# EMC × Refined Storage 3.3.0

**AI-created mod** — code, feature design, documentation and cover art were created and implemented by AI.

Blockless **ProjectE × Refined Storage** bridge for **Minecraft 1.20.1 / Forge**.

## New in 3.3.0: transmutation-table progression gate

- Until the owner has **ever obtained** a ProjectE **Transmutation Table** or **Transmutation Tablet**, the virtual EMC shop and EMC extraction are locked.
- Without a table, the terminal does not show the virtual EMC item list and cannot spend EMC.
- By default (`convertWithoutTable = false`), inserting EMC items while locked **only records knowledge and stores them normally**; no EMC is produced.
- Unlocking is permanent per player and is detected from:
  - a table/tablet in the player's inventory,
  - a placed Transmutation Table,
  - the ProjectE advancement for the table/tablet.
- Bulk recycle and the EMC status message also respect the gate.
- Config:
  ```toml
  [progression]
      requireTransmutationTable = true
      convertWithoutTable = false
  ```
  Set `convertWithoutTable = true` if you want auto-sell to keep producing EMC before the table (extraction stays locked).

### Test result

```
lockedWithoutTable=true  ironCount=1  learned=true  emcAfterInsert=10000
buyStacksBefore=0  diamondBefore=0
unlockedWithTable=true  buyStacksAfter=2  diamondAfter=1  emcAfterBuy=1808
convertWithoutTable=false
```

## Existing fixes retained

- **Enchantments / NBT are preserved.** Items with NBT are not auto-converted by default.
- **Truly EMC-less items are never consumed.** They are passed to normal RS storage.
- `[automation] sellNbtItems` (default `false`) remains available if you explicitly want NBT items auto-sold.
- EMC-less items still need a real RS storage disk/block with free space.

## Highlights

- Automatic EMC conversion when ordinary (non-NBT) EMC-valued items are inserted.
- Automatic EMC spending when learned items are pulled from the terminal (after the table gate unlocks).
- Live EMC balance in the RS terminal.
- Virtual purchase quantities update every ~1 second with stable item IDs.
- `buy` / `sell` blacklists, NBT protection, and the progression gate.
- EMC side button: status/bind, Shift rebind, Ctrl filtered bulk recycle.
- No extra block or item.

## Requirements

- Minecraft 1.20.1
- Forge 47.x
- ProjectE 1.20.1-PE1.0.1
- Refined Storage 1.12.4
- Java 17

## Installation

Put the mod jar and the two required mods in `mods/`. No new block or item has to be crafted.

## Notes

- The sell blacklist is empty by default, but NBT items are protected automatically. Ordinary EMC-valued items without NBT still become EMC when inserted after the table gate is unlocked.
- Add machines, storage disks, tools, etc. to `sell` if you want to keep ordinary non-NBT items.
- Single-player focused; one network uses one owner's ProjectE knowledge/EMC.
- The owner must be online for automatic buying/selling.
- The EMC button is only added to block RS terminals.
- Items only; no fluid support.
- Source: https://github.com/AuZn111/emc-rs
- License: MIT.

## Changes

- 3.3.0: transmutation-table progression gate; no-table inserts only record + store by default; new `[progression]` config.
- 3.2.2: NBT/enchantment protection by default; EMC-value check prevents consuming EMC-less items; `sellNbtItems` option.
- 3.2.1: AI attribution in metadata/docs/cover.
- 3.2.0: virtual quantities sync incrementally via RS item-delta messages; stable UUIDs.
- 3.1.0: live EMC balance label in RS terminals.
- 3.0.1: stopped rebuilding the cache on every EMC change.
- 3.0.0: blockless automatic EMC buy/sell network storage.
