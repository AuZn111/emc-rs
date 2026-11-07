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
        SPEC = builder.build();
    }

    private EmcConfig() {
    }
}
