package com.wintercogs.emcrs.logic;

import com.wintercogs.emcrs.config.EmcConfig;
import moze_intel.projecte.api.ItemInfo;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Parsed form of the buy/sell blacklists. The parsed matcher is cached until
 * the config list object changes, so hot paths (auto-sell on every inserted
 * stack) do not re-parse the config.
 */
public final class EmcBlacklist {
    private static List<? extends String> cachedBuyList;
    private static Matcher cachedBuyMatcher;
    private static List<? extends String> cachedSellList;
    private static Matcher cachedSellMatcher;

    private EmcBlacklist() {
    }

    public static Matcher buy() {
        List<? extends String> current = EmcConfig.BUY_BLACKLIST.get();
        if (cachedBuyMatcher == null || cachedBuyList != current) {
            cachedBuyList = current;
            cachedBuyMatcher = new Matcher(current);
        }
        return cachedBuyMatcher;
    }

    public static Matcher sell() {
        List<? extends String> current = EmcConfig.SELL_BLACKLIST.get();
        if (cachedSellMatcher == null || cachedSellList != current) {
            cachedSellList = current;
            cachedSellMatcher = new Matcher(current);
        }
        return cachedSellMatcher;
    }

    public static final class Matcher {
        private final Set<Item> items = new HashSet<>();
        private final List<TagKey<Item>> tags = new ArrayList<>();

        private Matcher(List<? extends String> entries) {
            if (entries == null) {
                return;
            }
            for (String rawEntry : entries) {
                if (rawEntry == null || rawEntry.isBlank()) {
                    continue;
                }
                String entry = rawEntry.trim();
                if (entry.startsWith("#")) {
                    ResourceLocation id = ResourceLocation.tryParse(entry.substring(1));
                    if (id != null) {
                        this.tags.add(TagKey.create(Registries.ITEM, id));
                    }
                } else {
                    ResourceLocation id = ResourceLocation.tryParse(entry);
                    if (id != null) {
                        Item item = ForgeRegistries.ITEMS.getValue(id);
                        if (item != null) {
                            this.items.add(item);
                        }
                    }
                }
            }
        }

        public boolean matches(ItemStack stack) {
            if (stack.isEmpty()) {
                return false;
            }
            if (this.items.contains(stack.getItem())) {
                return true;
            }
            for (TagKey<Item> tag : this.tags) {
                if (stack.is(tag)) {
                    return true;
                }
            }
            return false;
        }

        public boolean matches(ItemInfo info) {
            return matches(info.createStack());
        }

        public int size() {
            return this.items.size() + this.tags.size();
        }
    }
}
