package com.wintercogs.emcrs.client;

import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.apiimpl.network.node.GridNetworkNode;
import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.grid.GridScreen;
import com.wintercogs.emcrs.EMCRS;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import moze_intel.projecte.utils.TransmutationEMCFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.math.BigInteger;

/**
 * Adds the EMC network control button and the live EMC balance label to RS
 * terminals. The balance comes from the local player's synced ProjectE
 * knowledge capability, so single-player needs no extra packets.
 */
@Mod.EventBusSubscriber(modid = EMCRS.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ClientEvents {
    private ClientEvents() {
    }

    @SubscribeEvent
    public static void onScreenInit(ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof GridScreen)) {
            return;
        }
        // GridScreen#init calls super.init() (which fires this event) before its
        // own onPostInit(), so defer adding the button until BaseScreen's
        // runActions() after init. That places it below the normal side buttons.
        BaseScreen.executeLater(GridScreen.class, screen -> {
            if (screen.getSideButtons().stream().anyMatch(button ->
                    button instanceof EmcControlSideButton || button instanceof EmcLearnSideButton)) {
                return;
            }
            IGrid grid = screen.getGrid();
            if (grid instanceof GridNetworkNode gridNode) {
                screen.addSideButton(new EmcControlSideButton(screen, gridNode.getPos()));
                screen.addSideButton(new EmcLearnSideButton(screen, gridNode.getPos()));
            }
        });
    }

    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        if (!(event.getScreen() instanceof GridScreen gridScreen)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        IKnowledgeProvider provider = minecraft.player
                .getCapability(PECapabilities.KNOWLEDGE_CAPABILITY)
                .resolve()
                .orElse(null);
        if (provider == null) {
            return;
        }

        BigInteger emc = provider.getEmc();
        String text = Component.translatable(
                "gui.emc_rs.emc", TransmutationEMCFormatter.formatEMC(emc)).getString();
        int width = minecraft.font.width(text);
        int x = gridScreen.getGuiLeft() + 220 - width;
        int y = gridScreen.getGuiTop() + gridScreen.getYPlayerInventory() - 12;
        event.getGuiGraphics().drawString(minecraft.font, text, x, y, 0xFFD700, false);
    }
}
