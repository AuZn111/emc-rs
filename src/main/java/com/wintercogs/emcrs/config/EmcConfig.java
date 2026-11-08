package com.wintercogs.emcrs.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

/**
 * Global EMC blacklists. File: config/emc_rs-common.toml
 *
 * <p>Each entry is either an item registry name ("minecraft:bedrock") or an
 * item tag ("#minecraft:logs").</p>
 */
public final class EmcConfig {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BUY_BLACKLIST;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> SELL_BLACKLIST;
    public static final ForgeConfigSpec.BooleanValue SELL_NBT_ITEMS;
    public static final ForgeConfigSpec.BooleanValue REQUIRE_TRANSMUTATION_TABLE;
    public static final ForgeConfigSpec.BooleanValue CONVERT_WITHOUT_TABLE;
    public static final ForgeConfigSpec.BooleanValue CONVERT_EXISTING_STOCK;
    public static final ForgeConfigSpec.BooleanValue LEARN_ON_CRAFT;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.comment(
                "Items listed under buy are not offered by the virtual EMC storage.",
                "Items listed under sell are not automatically converted to EMC when inserted into an RS network.",
                "Entries use item registry names or #tags, e.g. \"minecraft:bedrock\" or \"#minecraft:logs\"."
        ).push("blacklist");

        BUY_BLACKLIST = builder
                .comment("Items that cannot be bought with EMC (still available from real RS storage).")
                .defineListAllowEmpty("buy", List.<String>of(), o -> o instanceof String);

        SELL_BLACKLIST = builder
                .comment("Items that are never auto-converted to EMC. They are stored normally instead.")
                .defineListAllowEmpty("sell", List.<String>of(), o -> o instanceof String);

        builder.pop();

        builder.comment("Automation behavior.").push("automation");
        SELL_NBT_ITEMS = builder
                .comment(
                        "If true, items with NBT data (enchantments, damage, custom data, potions, etc.)",
                        "are also auto-converted to EMC when inserted.",
                        "Default false: such items are preserved and stored normally in RS."
                )
                .define("sellNbtItems", false);
        builder.pop();

        builder.comment("Progression gate for the virtual EMC shop.").push("progression");
        REQUIRE_TRANSMUTATION_TABLE = builder
                .comment(
                        "If true, the owner must have obtained a Transmutation Table or Transmutation Tablet",
                        "before virtual EMC items can be extracted and before EMC can be spent.",
                        "The unlock is remembered per player once obtained."
                )
                .define("requireTransmutationTable", true);
        CONVERT_WITHOUT_TABLE = builder
                .comment(
                        "Only used while the owner has no transmutation table/tablet.",
                        "false (default): inserting EMC items only records knowledge and stores them normally; no EMC is produced.",
                        "true: auto-sell still converts items and adds EMC, but EMC extraction remains locked until the table is obtained."
                )
                .define("convertWithoutTable", false);
        CONVERT_EXISTING_STOCK = builder
                .comment(
                        "If true, when the owner first unlocks the EMC shop with a table/tablet,",
                        "scan the network's real stored stock and convert eligible items once.",
                        "Respects sell-blacklist, NBT protection, no-EMC items and protects the table/tablet itself."
                )
                .define("convertExistingStock", true);
        builder.pop();

        builder.comment("Learning / knowledge recording.").push("learning");
        LEARN_ON_CRAFT = builder
                .comment(
                        "If true, taking a crafting result from an RS crafting terminal records the output",
                        "into the player's ProjectE knowledge once (no extra item is consumed and no EMC is given)."
                )
                .define("learnOnCraft", true);
        builder.pop();

        SPEC = builder.build();
    }

    private EmcConfig() {
    }
}
