package com.wintercogs.emcrs.tick;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.storage.IStorage;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache;
import com.wintercogs.emcrs.data.EmcNetworkData;
import com.wintercogs.emcrs.logic.EmcTransactions;
import com.wintercogs.emcrs.logic.TransmutationGate;
import com.wintercogs.emcrs.storage.NetworkEmcBuyStorage;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Keeps the virtual EMC purchase counts in sync with the owner's live EMC.
 *
 * <p>Instead of invalidating the RS cache (which would regenerate entry UUIDs
 * and break pending terminal clicks), this updates the cache list in place with
 * batched deltas. RS sends those deltas to open terminals as
 * {@code GridItemDeltaMessage}, so quantities stay accurate and existing item
 * IDs remain stable.</p>
 */
public final class EmcNetworkTicker {
    private static final int POLL_INTERVAL = 20;
    private static final Map<INetwork, Integer> TICKS = new WeakHashMap<>();

    private EmcNetworkTicker() {
    }

    public static void tick(INetwork network) {
        if (!(network.getLevel() instanceof ServerLevel level)) {
            return;
        }
        int ticks = TICKS.getOrDefault(network, 0) + 1;
        if (ticks < POLL_INTERVAL) {
            TICKS.put(network, ticks);
            return;
        }
        TICKS.put(network, 0);
        sync(network, level);
    }

    public static void sync(INetwork network, ServerLevel level) {
        IStorageCache<ItemStack> cache = network.getItemStorageCache();
        NetworkEmcBuyStorage buyStorage = null;
        for (IStorage<ItemStack> storage : cache.getStorages()) {
            if (storage instanceof NetworkEmcBuyStorage buy) {
                buyStorage = buy;
                break;
            }
        }
        if (buyStorage == null) {
            return;
        }

        UUID owner = EmcNetworkData.get(level).getOwner(network.getPosition());
        ServerPlayer player = EmcTransactions.getOwnerPlayer(level, owner);
        IKnowledgeProvider provider = player == null ? null : EmcTransactions.getProvider(player);
        Map<ItemInfo, Integer> current = new HashMap<>();
        if (player != null && provider != null && TransmutationGate.canUseEmc(player)) {
            current = EmcTransactions.getBuyCounts(provider);
        }

        Map<ItemInfo, Integer> baseline = buyStorage.getLastCounts();
        if (baseline.isEmpty() && current.isEmpty()) {
            buyStorage.setLastCounts(current);
            return;
        }

        boolean changed = false;
        Set<ItemInfo> allItems = new HashSet<>(baseline.keySet());
        allItems.addAll(current.keySet());
        for (ItemInfo info : allItems) {
            int oldCount = baseline.getOrDefault(info, 0);
            int newCount = current.getOrDefault(info, 0);
            if (oldCount == newCount) {
                continue;
            }
            ItemStack stack = info.createStack();
            if (newCount > oldCount) {
                cache.add(stack, newCount - oldCount, false, true);
            } else {
                cache.remove(stack, oldCount - newCount, true);
            }
            changed = true;
        }

        if (changed) {
            cache.flush();
        }
        buyStorage.setLastCounts(current);
    }
}
