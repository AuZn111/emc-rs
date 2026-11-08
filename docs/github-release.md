# EMC × Refined Storage 3.5.1

**AI-created mod** — code, feature design, documentation and cover art were created and implemented by AI.

Blockless **ProjectE × Refined Storage** bridge for **Minecraft 1.20.1 / Forge**.

## Fixed in 3.5.1

- The **Learning Bar** side button now opens the **actual ProjectE Transmutation Table GUI** — the left 8-slot input circle/learning area and the right learned-item outputs, matching the real table.
- The 3.5.0 custom learned-item list and its "Record Cursor Item" action were removed. They were not the table's left learning interface.
- The table interface is gated by the table/tablet unlock; without it, the usual lock message appears.
- `[learning] consumeOnLearn` was removed because the actual table handles item consumption normally.
- **Kept:** RS crafting-terminal manual/Shift crafting auto-learns the crafted output once (`learnOnCraft = true`). Automatic crafting tasks do not trigger it.

## Verified

```
result=oak_planks craftLearned=true learnOnCraft=true
```

Crafting oak planks from an oak log recorded the output in ProjectE knowledge.

## Requirements

- Minecraft 1.20.1
- Forge 47.x
- ProjectE 1.20.1-PE1.0.1
- Refined Storage 1.12.4
- Java 17

## Installation

Put the mod jar and the two required mods in `mods/`. No new block or item has to be crafted.

## Notes

- The Learning Bar opens the real table UI; it does not add a separate custom list.
- The table UI requires the owner to have unlocked the Transmutation Table progression gate.
- RS crafting auto-learn only covers manual crafting in the RS crafting terminal, not automatic crafting tasks.
- Real EMC-less/NBT items still need a real RS storage disk/block with free space.
- Source: https://github.com/AuZn111/emc-rs
- License: MIT.

## Changes

- 3.5.1: Learning Bar opens the actual ProjectE Transmutation Table GUI; removed the 3.5.0 custom panel/record action.
- 3.5.0: learned-item panel (superseded), record cursor/filter items, RS crafting-terminal auto-learn.
- 3.4.0: one-time catch-up settlement of real network stock on first unlock.
- 3.3.0: Transmutation Table/Tablet progression gate.
- 3.2.2: NBT/enchantment protection; EMC-value check prevents consuming EMC-less items.
- 3.2.1: AI attribution in metadata/docs/cover.
- 3.2.0: virtual quantities sync incrementally; stable UUIDs.
- 3.1.0: live EMC balance label.
- 3.0.1: stopped rebuilding the cache on every EMC change.
- 3.0.0: blockless automatic EMC buy/sell network storage.
