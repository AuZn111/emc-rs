# EMC × Refined Storage

**AI-CREATED MOD** — the code, feature design, documentation and cover art of this mod were created and implemented by AI.

A **blockless ProjectE × Refined Storage bridge** for **Minecraft 1.20.1 / Forge**.

- Mod ID: `emc_rs`
- Version: `3.3.0`
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

> **Important:** with an empty sell blacklist, normal EMC-valued items with no NBT are converted to EMC. Items with NBT (enchantments, damage, custom data) are protected by default. Add machines, storage disks, tools, etc. to `sell` if you want to keep ordinary items too.

### NBT / enchantment protection and EMC-less items

- Items with NBT data — enchantments, damage, custom names, potions, etc. — are **not auto-converted by default**; they enter normal RS storage with their NBT intact.
- Truly EMC-less items are never consumed by the auto-sell storage. They are passed to normal RS storage and require a storage disk/block with free space; the virtual EMC storage does not hold real items.
- If you explicitly want NBT items auto-converted, set `[automation] sellNbtItems = true`. This will discard enchantments/durability, so it is not recommended.

### Progression gate: Transmutation Table / Tablet

- Until the owner has **ever obtained** a ProjectE Transmutation Table or Transmutation Tablet, the virtual EMC shop and EMC extraction are locked.
- By default, inserting EMC items while locked only records their knowledge and stores them normally; **no EMC is produced**.
- The unlock is permanent per player and is detected from the player's inventory, a placed transmutation table, or the ProjectE advancement.
- Once unlocked, the normal automatic EMC buy/sell behavior returns.

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
   - `emc_rs-3.3.0.jar`
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

[automation]
    sellNbtItems = false

[progression]
    requireTransmutationTable = true
    convertWithoutTable = false
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
- `automation.sellNbtItems`: default `false`; if `true`, NBT items (enchantments, damage, etc.) are auto-converted too, losing their NBT.
- `progression.requireTransmutationTable`: default `true`; locks EMC extraction until the owner has obtained a table/tablet.
- `progression.convertWithoutTable`: default `false`; while locked, inserts only record knowledge and store normally (no EMC). Set `true` to auto-sell before the table, but extraction remains locked.
- NBT-specific matching is not supported.
- Restart the game/server after editing.

---

## Notes and limitations

- **Single-player focused**: the controller placer (or the first player to click the EMC button) is the network EMC owner. One network uses one owner's knowledge/EMC.
- NBT items (enchantments, damage, custom data) are preserved in normal RS storage by default.
- The EMC shop is locked until the owner has obtained a Transmutation Table or Tablet.
- EMC-less items need a real RS storage disk/block with space; otherwise they return to the player.
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
