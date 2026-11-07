# EMC × Refined Storage

**AI-CREATED MOD** — the code, feature design, documentation and cover art of this mod were created and implemented by AI.

A **blockless ProjectE × Refined Storage bridge** for **Minecraft 1.20.1 / Forge**.

- Mod ID: `emc_rs`
- Version: `3.2.1`
- License: MIT
- Requires: ProjectE `1.20.1-PE1.0.1`, Refined Storage `1.12.4`, Forge `47.x`
- Source: https://github.com/AuZn111/emc-rs

---

## What it does

### Automatic EMC conversion on insert

- Any item inserted into the RS network (terminal shift-click, importer, interface, etc.) passes through an auto-recycle storage first.
- Items with a ProjectE sell value are consumed and converted into the network owner's EMC.
- Items with no EMC value, zero sell value, or in the **sell blacklist** are stored normally in RS.
- Selling an item for the first time learns it, matching ProjectE's transmutation table behavior.

> **Important:** with an empty sell blacklist, every EMC-valued item inserted into the network becomes EMC. Add machines, storage disks, tools, etc. to `sell` if you want to keep them.

### Automatic EMC purchase on extraction

- The RS terminal shows the owner's learned ProjectE items that have EMC value and are not in the **buy blacklist**.
- Real RS stock is consumed first; EMC is only spent for the remainder.
- Buy-blacklisted items never appear in the virtual EMC shop (real stock is still extractable).

### Live EMC balance and quantity sync

- A gold **EMC balance** line is shown in the RS terminal above the player inventory.
- Virtual purchasable quantities update about once per second from the live EMC.
- Quantities are updated with RS's native item-delta messages, not by rebuilding the cache. Entry UUIDs stay stable, so accurate counts and reliable extraction work together.

### EMC control button in the RS terminal

| Action | Result |
| --- | --- |
| Click | Show status; first click binds the current player as the network EMC owner |
| Shift + Click | Rebind the EMC owner (current owner or operator) |
| Ctrl + Click | Recycle real network stock matching the terminal's four filter slots (skips sell blacklist) |

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
   - `emc_rs-3.2.1.jar`
   - `refinedstorage-1.12.4.jar`
   - `ProjectE-1.20.1-PE1.0.1.jar`
3. Launch the game. No new block or item has to be crafted.

---

## Configuration

`config/emc_rs-common.toml` is generated on first launch:

```toml
[blacklist]
    buy = []
    sell = []
```

Entries can be item registry names or `#tags`:

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

- `buy`: items that cannot be purchased with EMC.
- `sell`: items that are never auto-converted to EMC and are stored normally.
- NBT-specific matching is not supported.
- Restart the game/server after editing.

---

## Notes and limitations

- **Single-player focused**: the controller placer (or the first player to click the EMC button) is the network EMC owner. One network uses one owner's knowledge/EMC.
- The owner must be online for EMC purchases and automatic selling.
- Only the player's base EMC is used; EMC inside transmutation-table input slots or Klein Stars is not read.
- Inserting EMC-valued items into the network converts them automatically unless the sell blacklist protects them.
- The EMC button is only added to block RS terminals; portable/wireless terminals can still see and pull virtual items but have no button.
- Items only; no fluid support.
- For 1.20.1 + RS 1.12.x + ProjectE 1.0.x only; not compatible with RS 2.x / 1.21.

---

## Links

- GitHub: https://github.com/AuZn111/emc-rs
- Releases: https://github.com/AuZn111/emc-rs/releases
- MC百科 / CurseForge pages: pending

## License

MIT. This is a third-party addon; it is not affiliated with ProjectE or Refined Storage. Their binaries are not redistributed here.
