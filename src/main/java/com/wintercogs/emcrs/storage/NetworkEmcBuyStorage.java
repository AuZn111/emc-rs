package com.wintercogs.emcrs.storage;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.api.storage.IStorage;
import com.refinedmods.refinedstorage.api.util.Action;
import com.wintercogs.emcrs.data.EmcNetworkData;
import com.wintercogs.emcrs.logic.EmcTransactions;
import moze_intel.projecte.api.ItemInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Auto-buy half of the EMC network storage: exposes learned ProjectE items
 * (minus the buy blacklist) and spends the owner's EMC when extracted.
 * It is EXTRACT-only and has the lowest priority, so real RS stock is used
 * before EMC.
 */
public class NetworkEmcBuyStorage implements IStorage<ItemStack> {
    private final INetwork network;

    /**
     * The virtual counts currently added to the RS cache list. The ticker
     * updates the cache in place by diffing against this map, preserving the
     * cache entry UUIDs instead of rebuilding them.
     */
    private Map<ItemInfo, Integer> lastCounts = new HashMap<>();

    public NetworkEmcBuyStorage(INetwork network) {
        this.network = network;
    }

    @Nullable
    private ServerLevel level() {
        return this.network.getLevel() instanceof ServerLevel serverLevel ? serverLevel : null;
    }

    @Nullable
    private UUID owner() {
        ServerLevel level = level();
        return level == null ? null : EmcNetworkData.get(level).getOwner(this.network.getPosition());
    }

    @Override
    public Collection<ItemStack> getStacks() {
        ServerLevel level = level();
        if (level == null) {
            this.lastCounts = new HashMap<>();
            return Collections.emptyList();
        }
        ServerPlayer player = EmcTransactions.getOwnerPlayer(level, owner());
        if (player == null) {
            this.lastCounts = new HashMap<>();
            return Collections.emptyList();
        }
        var provider = EmcTransactions.getProvider(player);
        if (provider == null) {
            this.lastCounts = new HashMap<>();
            return Collections.emptyList();
        }
        Map<ItemInfo, Integer> counts = EmcTransactions.getBuyCounts(provider);
        this.lastCounts = counts;
        java.util.List<ItemStack> result = new java.util.ArrayList<>(counts.size());
        counts.forEach((info, count) -> {
            ItemStack stack = info.createStack();
            stack.setCount(count);
            result.add(stack);
        });
        return result;
    }

    @NotNull
    @Override
    public ItemStack insert(@NotNull ItemStack stack, int size, @NotNull Action action) {
        // EXTRACT-only: not called by Network#insertItem.
        return EmcTransactions.copyWithSize(stack, size);
    }

    @NotNull
    @Override
    public ItemStack extract(@NotNull ItemStack stack, int size, int flags, @NotNull Action action) {
        ServerLevel level = level();
        if (level == null || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack result = EmcTransactions.extract(level, owner(), stack, size, flags, action);
        if (action == Action.PERFORM && !result.isEmpty()) {
            recordExtraction(ItemInfo.fromStack(result), result.getCount());
        }
        return result;
    }

    public Map<ItemInfo, Integer> getLastCounts() {
        return this.lastCounts;
    }

    public void setLastCounts(Map<ItemInfo, Integer> counts) {
        this.lastCounts = new HashMap<>(counts);
    }

    private void recordExtraction(ItemInfo info, int amount) {
        int old = this.lastCounts.getOrDefault(info, 0);
        int now = Math.max(0, old - amount);
        if (now == 0) {
            this.lastCounts.remove(info);
        } else {
            this.lastCounts.put(info, now);
        }
    }

    @Override
    public int getStored() {
        return 0;
    }

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE;
    }

    @Override
    public AccessType getAccessType() {
        return AccessType.EXTRACT;
    }

    @Override
    public int getCacheDelta(int storedPreInsertion, int size, @Nullable ItemStack remainder) {
        return 0;
    }
}
