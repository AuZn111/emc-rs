package com.wintercogs.emcrs.storage;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.api.storage.IStorage;
import com.refinedmods.refinedstorage.api.util.Action;
import com.wintercogs.emcrs.data.EmcNetworkData;
import com.wintercogs.emcrs.logic.EmcTransactions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * Auto-sell half of the EMC network storage: anything inserted into the RS
 * network with a positive ProjectE sell value is consumed and converted to the
 * owner's EMC, unless it is in the sell blacklist or has no EMC value. It is
 * INSERT-only and has the highest priority.
 */
public class NetworkEmcSellStorage implements IStorage<ItemStack> {
    private final INetwork network;

    public NetworkEmcSellStorage(INetwork network) {
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
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public ItemStack insert(@NotNull ItemStack stack, int size, @NotNull Action action) {
        ServerLevel level = level();
        if (level == null || stack.isEmpty() || size <= 0) {
            return EmcTransactions.copyWithSize(stack, size);
        }
        return EmcTransactions.insert(level, owner(), stack, size, action);
    }

    @NotNull
    @Override
    public ItemStack extract(@NotNull ItemStack stack, int size, int flags, @NotNull Action action) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getStored() {
        return 0;
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public AccessType getAccessType() {
        return AccessType.INSERT;
    }

    @Override
    public int getCacheDelta(int storedPreInsertion, int size, @Nullable ItemStack remainder) {
        // Auto-sold items never remain in the storage.
        return 0;
    }
}
