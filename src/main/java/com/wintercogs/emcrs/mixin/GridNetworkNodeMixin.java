package com.wintercogs.emcrs.mixin;

import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.wintercogs.emcrs.config.EmcConfig;
import com.wintercogs.emcrs.logic.EmcTransactions;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.proxy.IEMCProxy;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Records the output of a manual RS crafting-terminal craft into the player's
 * ProjectE knowledge. This intentionally does not consume the output or grant
 * EMC; it is only a knowledge record. Covers single and shift crafting.
 */
@Mixin(GridNetworkNode.class)
public class GridNetworkNodeMixin {
    @Inject(method = "onCrafted", at = @At("HEAD"))
    private void emcrs$learnCrafted(Player player,
                                    IStackList<ItemStack> availableItems,
                                    IStackList<ItemStack> usedItems,
                                    CallbackInfo ci) {
        if (!EmcConfig.LEARN_ON_CRAFT.get() || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        ItemStack result = ((GridNetworkNode) (Object) this).getCraftingResult().getItem(0);
        if (result.isEmpty()) {
            return;
        }
        IKnowledgeProvider provider = EmcTransactions.getProvider(serverPlayer);
        if (provider == null) {
            return;
        }
        ItemInfo info = ItemInfo.fromStack(result);
        if (IEMCProxy.INSTANCE.getValue(info) <= 0L) {
            return;
        }
        EmcTransactions.learn(provider, serverPlayer, info);
    }
}
