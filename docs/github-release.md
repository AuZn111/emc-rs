# EMC × Refined Storage 3.2.2

**AI-created mod** — code, feature design, documentation and cover art were created and implemented by AI.

Blockless **ProjectE × Refined Storage** bridge for **Minecraft 1.20.1 / Forge**.

## Fixes in 3.2.2

- **Enchantments / NBT are no longer lost.** Items with NBT data (enchantments, damage, custom names, potions, custom data) are not auto-converted by default. They enter normal RS storage with their NBT intact.
- **Truly EMC-less items are never consumed.** Auto-selling now checks the item's EMC value before converting; EMC-less items are passed to normal RS storage.
- New config option `[automation] sellNbtItems` (default `false`). Set it to `true` only if you explicitly want NBT items auto-converted (their enchantments/durability will be discarded).

> EMC-less items still need a real Refined Storage disk/block with free space.
> The virtual EMC storage only holds EMC-purchasable virtual items, not arbitrary real items.

## Highlights

- Automatic EMC conversion when ordinary (non-NBT) EMC-valued items are inserted.
- Automatic EMC spending when learned items are pulled from the terminal.
- Live EMC balance in the RS terminal.
- Virtual purchase quantities update every ~1 second with stable item IDs.
- `buy` / `sell` blacklists plus the new NBT protection option.
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

- The sell blacklist is empty by default, but NBT items are protected automatically. Ordinary EMC-valued items without NBT still become EMC when inserted.
- Add machines, storage disks, tools, etc. to `sell` if you want to keep ordinary non-NBT items.
- Single-player focused; one network uses one owner's ProjectE knowledge/EMC.
- The owner must be online for automatic buying/selling.
- The EMC button is only added to block RS terminals.
- Items only; no fluid support.
- Source: https://github.com/AuZn111/emc-rs
- License: MIT.

## Changes

- 3.2.2: NBT/enchantment protection by default; EMC-value check prevents consuming EMC-less items; new `sellNbtItems` option.
- 3.2.1: AI attribution in metadata/docs/cover.
- 3.2.0: virtual quantities sync incrementally via RS item-delta messages; stable UUIDs; accurate counts and reliable extraction.
- 3.1.0: live EMC balance label in RS terminals.
- 3.0.1: stopped rebuilding the cache on every EMC change.
- 3.0.0: blockless automatic EMC buy/sell network storage.
