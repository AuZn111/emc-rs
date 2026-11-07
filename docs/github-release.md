# EMC × Refined Storage 3.2.1

**AI-created mod** — code, feature design, documentation and cover art were created and implemented by AI.

Blockless **ProjectE × Refined Storage** bridge for **Minecraft 1.20.1 / Forge**.

## Highlights

- Automatic EMC conversion when items are inserted into the RS network.
- Automatic EMC spending when learned items are pulled from the terminal.
- Live EMC balance in the RS terminal.
- Virtual purchase quantities update every ~1 second with stable item IDs.
- `buy` / `sell` blacklists in `config/emc_rs-common.toml`.
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

- The sell blacklist is empty by default: any EMC-valued item inserted into the network becomes EMC. Protect machines, storage disks, tools, etc. with `sell`.
- Single-player focused; one network uses one owner's ProjectE knowledge/EMC.
- The owner must be online for automatic buying/selling.
- The EMC button is only added to block RS terminals.
- Items only; no fluid support.
- Source: https://github.com/AuZn111/emc-rs
- License: MIT.

## Changes

- 3.2.1: AI attribution in metadata/docs/cover.
- 3.2.0: virtual quantities sync incrementally via RS item-delta messages; stable UUIDs; accurate counts and reliable extraction.
- 3.1.0: live EMC balance label in RS terminals.
- 3.0.1: stopped rebuilding the cache on every EMC change.
- 3.0.0: blockless automatic EMC buy/sell network storage.
