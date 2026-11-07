package com.wintercogs.emcrs.logic;

import com.refinedmods.refinedstorage.api.util.Action;
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
