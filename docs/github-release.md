# EMC × Refined Storage 3.5.0

**AI-created mod** — code, feature design, documentation and cover art were created and implemented by AI.

Blockless **ProjectE × Refined Storage** bridge for **Minecraft 1.20.1 / Forge**.

## New in 3.5.0: Learning panel + craft auto-learn

### Learning Bar

- A new **Learning Bar** side button appears in the left function bar of RS terminals.
- Clicking it expands a ProjectE-style learned-item panel:
  - learned item grid;
  - search box;
  - mouse-wheel scrolling;
  - item tooltips.
- **Record Cursor Item** button:
  - with an item on the cursor: records it into ProjectE knowledge;
  - with an empty cursor: records the terminal's four filter-slot items.
- Default: **no item consumption and no EMC granted** — only knowledge is recorded.
- `[learning] consumeOnLearn = true` makes the cursor-record consume one item.

### Craft auto-learn

- Taking a result from an **RS crafting terminal** (single or Shift crafting) records the crafted output into ProjectE knowledge once.
- The output is not consumed again and no EMC is granted.
- **Automatic crafting tasks** do not pass through a player, so they do not trigger this.

### Verified

```
result=oak_planks craftLearned=true filterLearned=true learnOnCraft=true
```

- Crafting oak planks from an oak log recorded the output.
- The learning action recorded a filter-slot diamond.
- `learnOnCraft=true`.

## Existing behavior retained

- **Transmutation Table/Tablet progression gate (3.3.0):** no EMC shop or EMC extraction before the table/tablet is obtained.
- **Catch-up settlement (3.4.0):** first unlock converts eligible real network stock once.
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

- The learning panel is a viewer/record panel for learned items; it does not replace the Transmutation Table's actual transmutation slots.
- Records respect ProjectE knowledge; items without an EMC value are ignored.
- Real EMC-less/NBT items still need a real RS storage disk/block with free space.
- Single-player focused; one network uses one owner's ProjectE knowledge/EMC.
- Items only; no fluid support.
- Source: https://github.com/AuZn111/emc-rs
- License: MIT.

## Changes

- 3.5.0: learned-item learning panel; record cursor/filter items; automatic learning of RS crafting-terminal output.
- 3.4.0: one-time catch-up settlement of real network stock on first unlock.
- 3.3.0: Transmutation Table/Tablet progression gate; no-table inserts only record + store by default.
- 3.2.2: NBT/enchantment protection; EMC-value check prevents consuming EMC-less items.
- 3.2.1: AI attribution in metadata/docs/cover.
- 3.2.0: virtual quantities sync incrementally; stable UUIDs.
- 3.1.0: live EMC balance label.
- 3.0.1: stopped rebuilding the cache on every EMC change.
- 3.0.0: blockless automatic EMC buy/sell network storage.
