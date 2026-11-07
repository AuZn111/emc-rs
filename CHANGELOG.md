# Changelog

All notable changes to EMC × Refined Storage.

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
