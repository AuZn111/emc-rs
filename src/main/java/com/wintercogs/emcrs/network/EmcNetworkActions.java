package com.wintercogs.emcrs.network;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import com.wintercogs.emcrs.data.EmcNetworkData;
import com.wintercogs.emcrs.logic.EmcBlacklist;
import com.wintercogs.emcrs.logic.EmcTransactions;
import com.wintercogs.emcrs.storage.NetworkEmcBuyStorage;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class EmcNetworkActions {
    private EmcNetworkActions() {
    }

    public static void handle(ServerPlayer player, BlockPos gridPos, int action) {
        ServerLevel level = player.serverLevel();
        net.minecraft.world.level.block.entity.BlockEntity blockEntity = level.getBlockEntity(gridPos);
        INetworkNode node = blockEntity == null ? null : NetworkUtils.getNodeFromBlockEntity(blockEntity);
        if (!(node instanceof GridNetworkNode grid) || grid.getNetwork() == null) {
            message(player, "message.emc_rs.network.no_network");
            return;
        }

        INetwork network = grid.getNetwork();
        if (!network.getSecurityManager().hasPermission(
                com.refinedmods.refinedstorage.api.network.security.Permission.BUILD, player)) {
            message(player, "message.emc_rs.network.no_permission");
            return;
        }

        BlockPos controllerPos = network.getPosition();
        EmcNetworkData data = EmcNetworkData.get(level);
        UUID owner = data.getOwner(controllerPos);

        if (action == EmcActionPacket.ACTION_REBIND) {
            if (owner != null && !owner.equals(player.getUUID()) && !player.hasPermissions(2)) {
                message(player, "message.emc_rs.network.rebind_denied");
                return;
            }
            bind(player, data, controllerPos);
            return;
        }

        if (action == EmcActionPacket.ACTION_BULK_SELL) {
            if (owner == null) {
                message(player, "message.emc_rs.network.bind_first");
                return;
            }
            bulkSell(player, level, network, grid, owner);
            return;
        }

        // Normal click: show status. If the network has no owner yet, bind it first.
        if (owner == null) {
            bind(player, data, controllerPos);
        }
        sendStatus(player, level, network, data);
    }

    private static void bind(ServerPlayer player, EmcNetworkData data, BlockPos controllerPos) {
        data.setOwner(controllerPos, player.getUUID());
        player.displayClientMessage(Component.translatable(
                "message.emc_rs.network.bound", player.getGameProfile().getName()), false);
    }

    private static void sendStatus(ServerPlayer player, ServerLevel level, INetwork network, EmcNetworkData data) {
        UUID owner = data.getOwner(network.getPosition());
        if (owner == null) {
            message(player, "message.emc_rs.network.bind_first");
            return;
        }
        ServerPlayer ownerPlayer = EmcTransactions.getOwnerPlayer(level, owner);
        if (ownerPlayer == null) {
            message(player, "message.emc_rs.network.owner_offline");
            return;
        }
        IKnowledgeProvider provider = EmcTransactions.getProvider(ownerPlayer);
        if (provider == null) {
            message(player, "message.emc_rs.network.no_provider");
            return;
        }

        player.displayClientMessage(Component.translatable(
                "message.emc_rs.network.status",
                ownerPlayer.getGameProfile().getName(),
                EmcTransactions.formatEmc(provider.getEmc()),
                EmcBlacklist.buy().size(),
                EmcBlacklist.sell().size()), false);
    }

    private static void bulkSell(ServerPlayer player, ServerLevel level, INetwork network,
                                 GridNetworkNode grid, UUID owner) {
        if (!network.getSecurityManager().hasPermission(
                com.refinedmods.refinedstorage.api.network.security.Permission.EXTRACT, player)) {
            message(player, "message.emc_rs.network.no_extract");
            return;
        }
        if (!network.canRun()) {
            message(player, "message.emc_rs.network.no_energy");
            return;
        }

        ServerPlayer ownerPlayer = EmcTransactions.getOwnerPlayer(level, owner);
        if (ownerPlayer == null) {
            message(player, "message.emc_rs.network.owner_offline");
            return;
        }
        IKnowledgeProvider provider = EmcTransactions.getProvider(ownerPlayer);
        if (provider == null) {
            message(player, "message.emc_rs.network.no_provider");
            return;
        }

        IItemHandler filter = grid.getFilter();
        EmcBlacklist.Matcher sellBlacklist = EmcBlacklist.sell();
        List<ItemStack> filters = new ArrayList<>();
        for (int i = 0; i < filter.getSlots(); i++) {
            ItemStack stack = filter.getStackInSlot(i);
            if (stack.isEmpty()) {
                continue;
            }
            ItemStack key = stack.copy();
            key.setCount(1);
            if (sellBlacklist.matches(key)) {
                continue;
            }
            if (IEMCProxy.INSTANCE.getSellValue(key) > 0L) {
                filters.add(key);
            }
        }

        if (filters.isEmpty()) {
            message(player, "message.emc_rs.network.no_filter");
            return;
        }

        int sold = 0;
        BigInteger gained = BigInteger.ZERO;
        for (ItemStack key : filters) {
            long unitValue = IEMCProxy.INSTANCE.getSellValue(key);
            if (unitValue <= 0L) {
                continue;
            }
            for (int guard = 0; guard < 4096; guard++) {
                ItemStack inNetwork = network.getItemStorageCache().getList()
                        .get(key, IComparer.COMPARE_NBT);
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
                EmcTransactions.learn(provider, ownerPlayer, ItemInfo.fromStack(extracted));
            }
        }

        if (sold <= 0) {
            message(player, "message.emc_rs.network.nothing_to_sell");
            return;
        }

        EmcTransactions.addEmc(provider, ownerPlayer, gained);
        player.displayClientMessage(Component.translatable(
                "message.emc_rs.network.bulk_sold", sold, EmcTransactions.formatEmc(gained)), false);
    }

    private static void message(ServerPlayer player, String key) {
        player.displayClientMessage(Component.translatable(key), false);
    }
}
