package com.wintercogs.emcrs.mixin;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.storage.AccessType;
import com.refinedmods.refinedstorage.api.storage.IStorage;
import com.refinedmods.refinedstorage.api.storage.cache.InvalidateCause;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.apiimpl.storage.cache.ItemStorageCache;
import com.wintercogs.emcrs.storage.NetworkEmcBuyStorage;
import com.wintercogs.emcrs.storage.NetworkEmcSellStorage;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Injects the automatic EMC buy/sell storages into every RS item storage cache.
 *
 * <p>The sell storage is INSERT-only at the highest priority, so items inserted
 * into the network become EMC unless blacklisted. The buy storage is
 * EXTRACT-only at the lowest priority, so real RS stock is consumed first and
 * EMC is only spent for the remainder.</p>
 *
 * <p>Injection happens just before the cache notifies its listeners, so the
 * final list contains the virtual stacks and listeners are notified only once.</p>
 */
@Mixin(ItemStorageCache.class)
public abstract class ItemStorageCacheMixin {
    private static final Logger LOGGER = LogManager.getLogger("emc_rs");

    @Shadow
    @Final
    private INetwork network;

    @Shadow
    @Final
    private CopyOnWriteArrayList<IStorage<ItemStack>> storages;

    @Shadow
    @Final
    private IStackList<ItemStack> list;

    @Inject(
            method = "invalidate",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
                    shift = At.Shift.BEFORE
            )
    )
    private void emcrs$injectNetworkStorages(InvalidateCause cause, CallbackInfo ci) {
        if (!(this.network.getLevel() instanceof ServerLevel)) {
            return;
        }

        try {
            NetworkEmcSellStorage sellStorage = new NetworkEmcSellStorage(this.network);
            NetworkEmcBuyStorage buyStorage = new NetworkEmcBuyStorage(this.network);

            this.storages.add(sellStorage);
            this.storages.add(buyStorage);
            this.storages.sort(IStorage.COMPARATOR);

            for (IStorage<ItemStack> storage : List.of(sellStorage, buyStorage)) {
                if (storage.getAccessType() == AccessType.INSERT) {
                    continue;
                }
                for (ItemStack stack : storage.getStacks()) {
                    if (!stack.isEmpty() && stack.getCount() > 0) {
                        this.list.add(stack, stack.getCount());
                    }
                }
            }

            LOGGER.debug("Injected EMC buy/sell storages at {}", this.network.getPosition());
        } catch (Exception exception) {
            LOGGER.error("Failed to inject EMC storages at {}", this.network.getPosition(), exception);
        }
    }
}
