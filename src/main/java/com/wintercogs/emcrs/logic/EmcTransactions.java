package com.wintercogs.emcrs.logic;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import com.wintercogs.emcrs.config.EmcConfig;
import com.wintercogs.emcrs.storage.NetworkEmcBuyStorage;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.api.event.PlayerAttemptLearnEvent;
import moze_intel.projecte.api.proxy.IEMCProxy;
import moze_intel.projecte.utils.Constants;
import moze_intel.projecte.utils.PlayerHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Shared ProjectE transaction logic for the blockless RS network storage.
 */
public final class EmcTransactions {
    private static final BigInteger MAX_DISPLAYED_COUNT = BigInteger.valueOf(1_000_000_000L);

    private EmcTransactions() {
    }

    @Nullable
    public static ServerPlayer getOwnerPlayer(ServerLevel level, @Nullable UUID owner) {
        if (owner == null) {
            return null;
        }
        MinecraftServer server = level.getServer();
        return server == null ? null : server.getPlayerList().getPlayer(owner);
    }

    @Nullable
    public static IKnowledgeProvider getProvider(@Nullable ServerPlayer player) {
        if (player == null) {
            return null;
        }
        return player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY).resolve().orElse(null);
    }

    @NotNull
    public static Collection<ItemStack> getBuyStacks(@NotNull IKnowledgeProvider provider) {
        Map<ItemInfo, Integer> counts = getBuyCounts(provider);
        List<ItemStack> result = new ArrayList<>(counts.size());
        counts.forEach((info, count) -> {
            ItemStack stack = info.createStack();
            stack.setCount(count);
            result.add(stack);
        });
        return result;
    }

    /**
     * Current virtual purchase counts for a provider. Used both when the RS
     * cache is (re)built and by the incremental quantity updater.
     */
    @NotNull
    public static Map<ItemInfo, Integer> getBuyCounts(@NotNull IKnowledgeProvider provider) {
        Map<ItemInfo, Integer> result = new HashMap<>();
        BigInteger emc = provider.getEmc();
        if (emc.signum() <= 0) {
            return result;
        }

        EmcBlacklist.Matcher buyBlacklist = EmcBlacklist.buy();
        for (ItemInfo info : provider.getKnowledge()) {
            if (buyBlacklist.matches(info)) {
                continue;
            }
            long value = IEMCProxy.INSTANCE.getValue(info);
            if (value <= 0L) {
                continue;
            }
            BigInteger affordable = emc.divide(BigInteger.valueOf(value));
            if (affordable.signum() <= 0) {
                continue;
            }
            int count = affordable.min(MAX_DISPLAYED_COUNT).intValue();
            if (count > 0) {
                result.put(info, count);
            }
        }
        return result;
    }

    @NotNull
    public static ItemStack extract(@NotNull ServerLevel level, @Nullable UUID owner,
                                    @NotNull ItemStack stack, int size, int flags, @NotNull Action action) {
        ServerPlayer player = getOwnerPlayer(level, owner);
        IKnowledgeProvider provider = getProvider(player);
        if (player == null || provider == null || stack.isEmpty() || size <= 0) {
            return ItemStack.EMPTY;
        }
        if (!TransmutationGate.canUseEmc(player)) {
            return ItemStack.EMPTY;
        }

        ItemInfo info = ItemInfo.fromStack(stack);
        if (!provider.hasKnowledge(info) || EmcBlacklist.buy().matches(info)) {
            return ItemStack.EMPTY;
        }
        long value = IEMCProxy.INSTANCE.getValue(info);
        if (value <= 0L) {
            return ItemStack.EMPTY;
        }

        BigInteger emc = provider.getEmc();
        BigInteger affordable = emc.divide(BigInteger.valueOf(value));
        int take = affordable.min(BigInteger.valueOf(size)).intValue();
        if (take <= 0) {
            return ItemStack.EMPTY;
        }

        if (action == Action.PERFORM) {
            BigInteger cost = BigInteger.valueOf(value).multiply(BigInteger.valueOf(take));
            setEmc(provider, player, emc.subtract(cost));
        }

        ItemStack result = info.createStack();
        result.setCount(take);
        return result;
    }

    @NotNull
    public static ItemStack insert(@NotNull ServerLevel level, @Nullable UUID owner,
                                   @NotNull ItemStack stack, int size, @NotNull Action action) {
        ServerPlayer player = getOwnerPlayer(level, owner);
        IKnowledgeProvider provider = getProvider(player);
        if (player == null || provider == null || stack.isEmpty() || size <= 0) {
            return copyWithSize(stack, size);
        }

        ItemInfo info = ItemInfo.fromStack(stack);
        boolean canUseEmc = TransmutationGate.canUseEmc(player);
        if (!canUseEmc) {
            // Before obtaining a table: record knowledge, but never spend EMC.
            if (action == Action.PERFORM && IEMCProxy.INSTANCE.getValue(info) > 0L) {
                learn(provider, player, info);
            }
            if (!EmcConfig.CONVERT_WITHOUT_TABLE.get()) {
                // Default: store normally and only learn the item; no EMC is produced.
                return copyWithSize(stack, size);
            }
        }
        if (IEMCProxy.INSTANCE.getValue(info) <= 0L) {
            // Truly no EMC value: never consume it; let normal RS storage take it.
            return copyWithSize(stack, size);
        }
        if (!EmcConfig.SELL_NBT_ITEMS.get() && stack.hasTag()) {
            // Preserve enchanted/damaged/custom-NBT items by default.
            return copyWithSize(stack, size);
        }
        if (EmcBlacklist.sell().matches(stack)) {
            return copyWithSize(stack, size);
        }
        long unitValue = IEMCProxy.INSTANCE.getSellValue(info);
        if (unitValue <= 0L) {
            return copyWithSize(stack, size);
        }

        int amount = Math.min(size, stack.getCount());
        if (amount <= 0) {
            return copyWithSize(stack, size);
        }

        if (action == Action.PERFORM) {
            BigInteger gain = BigInteger.valueOf(unitValue).multiply(BigInteger.valueOf(amount));
            learn(provider, player, info);
            addEmc(provider, player, gain);
        }
        return ItemStack.EMPTY;
    }

    /**
     * One-time conversion of real stock already stored in the network after the
     * owner unlocks the EMC shop with a transmutation table/tablet.
     *
     * <p>Virtual buy-storage entries are ignored by the extraction filter. The
     * method respects the sell blacklist, NBT protection and no-EMC items, and
     * always protects the transmutation table/tablet itself.</p>
     */
    public static int catchUpExistingStock(@NotNull INetwork network, @NotNull ServerPlayer player,
                                           @NotNull IKnowledgeProvider provider) {
        if (!TransmutationGate.isCatchUpEnabled() || !TransmutationGate.canUseEmc(player)) {
            return 0;
        }
        IStorageCache<ItemStack> cache = network.getItemStorageCache();
        int sold = 0;
        BigInteger gained = BigInteger.ZERO;

        for (StackListEntry<ItemStack> entry : cache.getList().copy().getStacks()) {
            ItemStack cached = entry.getStack();
            if (cached.isEmpty()) {
                continue;
            }
            ItemStack key = cached.copy();
            key.setCount(1);
            if (TransmutationGate.isTransmutationItem(key)) {
                continue;
            }
            ItemInfo info = ItemInfo.fromStack(key);
            if (IEMCProxy.INSTANCE.getValue(info) <= 0L) {
                continue;
            }
            if (!EmcConfig.SELL_NBT_ITEMS.get() && key.hasTag()) {
                continue;
            }
            if (EmcBlacklist.sell().matches(key)) {
                continue;
            }
            long unitValue = IEMCProxy.INSTANCE.getSellValue(info);
            if (unitValue <= 0L) {
                continue;
            }

            for (int guard = 0; guard < 4096; guard++) {
                ItemStack inNetwork = cache.getList().get(key, IComparer.COMPARE_NBT);
                if (inNetwork == null) {
                    break;
                }
                ItemStack extracted = network.extractItem(
                        inNetwork, inNetwork.getCount(), IComparer.COMPARE_NBT, Action.PERFORM,
                        storage -> !(storage instanceof NetworkEmcBuyStorage));
                if (extracted.isEmpty()) {
                    break;
                }
                int amount = extracted.getCount();
                sold += amount;
                gained = gained.add(BigInteger.valueOf(unitValue)
                        .multiply(BigInteger.valueOf(amount)));
                learn(provider, player, ItemInfo.fromStack(extracted));
            }
        }

        if (gained.signum() > 0) {
            addEmc(provider, player, gained);
            player.displayClientMessage(Component.translatable(
                    "message.emc_rs.network.catch_up", sold, formatEmc(gained)), false);
        }
        return sold;
    }

    public static void learn(@NotNull IKnowledgeProvider provider, @NotNull ServerPlayer owner, @NotNull ItemInfo info) {
        ItemInfo persistent = IEMCProxy.INSTANCE.getPersistentInfo(info);
        if (!provider.hasKnowledge(persistent)
                && !MinecraftForge.EVENT_BUS.post(new PlayerAttemptLearnEvent(owner, info, persistent))
                && provider.addKnowledge(persistent)) {
            provider.syncKnowledgeChange(owner, persistent, true);
        }
    }

    public static void addEmc(@NotNull IKnowledgeProvider provider, @NotNull ServerPlayer owner, @NotNull BigInteger gain) {
        if (gain.signum() <= 0) {
            return;
        }
        setEmc(provider, owner, provider.getEmc().add(gain));
    }

    public static void setEmc(@NotNull IKnowledgeProvider provider, @NotNull ServerPlayer owner, @NotNull BigInteger emc) {
        BigInteger value = emc.signum() < 0 ? BigInteger.ZERO : emc;
        provider.setEmc(value);
        provider.syncEmc(owner);
        PlayerHelper.updateScore(owner, PlayerHelper.SCOREBOARD_EMC, value);
    }

    @NotNull
    public static String formatEmc(@NotNull BigInteger emc) {
        return Constants.EMC_FORMATTER.format(emc);
    }

    @NotNull
    public static ItemStack copyWithSize(@NotNull ItemStack stack, int size) {
        ItemStack copy = stack.copy();
        copy.setCount(Math.max(0, size));
        return copy;
    }
}
