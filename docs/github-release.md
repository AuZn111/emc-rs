# EMC × Refined Storage 3.4.0

**AI-created mod** — code, feature design, documentation and cover art were created and implemented by AI.

Blockless **ProjectE × Refined Storage** bridge for **Minecraft 1.20.1 / Forge**.

## New in 3.4.0: unlock catch-up settlement

When the owner first obtains a **Transmutation Table** or **Transmutation Tablet** and unlocks the EMC shop, the network's **real stored stock** is scanned once and eligible items are automatically converted into EMC.

- Default `[progression] convertExistingStock = true`.
- Only real RS storage is settled; virtual EMC-shop entries are ignored.
- Skips:
  - `sell` blacklist items;
  - items with NBT (enchantments, damage, potions, custom data);
  - items with no EMC value;
  - the Transmutation Table / Tablet itself.
- Runs once per network/owner; repeating ticks do not duplicate the conversion.
- Sends the owner a chat message with the converted item count and EMC gained.

### Test result

```
before[iron=2, sword=2, table=1, emc=10000]
sold=2
after[iron=0, sword=2, table=1, emc=10512]
soldAgain=0, emcAgain=10512
```

The two iron ingots were converted, the enchanted swords and the table were preserved, and a second catch-up converted nothing.

## Existing behavior retained

- **Transmutation Table/Tablet progression gate (3.3.0):** no EMC shop or EMC extraction before the table/tablet is obtained.
- **No-table insert behavior:** by default records knowledge and stores normally; no EMC is produced.
- **NBT/enchantment protection (3.2.2):** NBT items are not auto-sold by default.
- **EMC-less items:** never consumed; passed to normal RS storage.
- **Live EMC balance + incremental quantity sync (3.2.0/3.1.0):** stable item IDs and reliable extraction.
- **Real-stock-first extraction**, `buy` / `sell` blacklists, and the blockless design.

## Requirements

- Minecraft 1.20.1
- Forge 47.x
- ProjectE 1.20.1-PE1.0.1
- Refined Storage 1.12.4
- Java 17

## Installation

Put the mod jar and the two required mods in `mods/`. No new block or item has to be crafted.

## Notes

- Catch-up settlement respects the sell blacklist and NBT protection. Protect anything you want to keep with `sell`.
- Real EMC-less/NBT items still need a real RS storage disk/block with free space.
- Single-player focused; one network uses one owner's ProjectE knowledge/EMC.
- The owner must be online for catch-up, buying and selling.
- The EMC button is only added to block RS terminals.
- Items only; no fluid support.
- Source: https://github.com/AuZn111/emc-rs
- License: MIT.

## Changes

- 3.4.0: one-time catch-up settlement of real network stock on first unlock; new `convertExistingStock` option.
- 3.3.0: Transmutation Table/Tablet progression gate; no-table inserts only record + store by default.
- 3.2.2: NBT/enchantment protection by default; EMC-value check prevents consuming EMC-less items.
- 3.2.1: AI attribution in metadata/docs/cover.
- 3.2.0: virtual quantities sync incrementally via RS item-delta messages; stable UUIDs.
- 3.1.0: live EMC balance label in RS terminals.
- 3.0.1: stopped rebuilding the cache on every EMC change.
- 3.0.0: blockless automatic EMC buy/sell network storage.
