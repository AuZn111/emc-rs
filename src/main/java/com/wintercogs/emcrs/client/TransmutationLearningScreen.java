package com.wintercogs.emcrs.client;

import com.wintercogs.emcrs.EMCRS;
import com.wintercogs.emcrs.network.EmcActionPacket;
import com.wintercogs.emcrs.network.EmcNetworkHandler;
import moze_intel.projecte.api.ItemInfo;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import moze_intel.projecte.api.capabilities.PECapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * A compact, ProjectE-style "learning" panel opened from the RS terminal side
 * bar. It shows the player's learned items with search and scrolling, and can
 * send a learning request for the item on the cursor.
 */
public class TransmutationLearningScreen extends Screen {
    private static final int COLS = 9;
    private static final int ROWS = 5;
    private static final int SLOT = 18;
    private static final int PANEL_W = COLS * SLOT + 20;
    private static final int PANEL_H = ROWS * SLOT + 110;

    private final BlockPos gridPos;
    private final List<ItemStack> allItems = new ArrayList<>();
    private final List<ItemStack> filteredItems = new ArrayList<>();

    private EditBox search;
    private int left;
    private int top;
    private int scroll;

    public TransmutationLearningScreen(BlockPos gridPos) {
        super(Component.translatable("gui.emc_rs.learn.title"));
        this.gridPos = gridPos;
    }

    @Override
    protected void init() {
        super.init();
        this.left = (this.width - PANEL_W) / 2;
        this.top = (this.height - PANEL_H) / 2;

        this.search = new EditBox(this.font, this.left + 10, this.top + 36,
                PANEL_W - 20, 18, Component.translatable("gui.emc_rs.learn.search"));
        this.search.setHint(Component.translatable("gui.emc_rs.learn.search_hint"));
        this.search.setResponder(value -> rebuildFiltered());
        this.addRenderableWidget(this.search);

        this.addRenderableWidget(Button.builder(
                        Component.translatable("gui.emc_rs.learn.record"),
                        button -> EmcNetworkHandler.sendToServer(
                                new EmcActionPacket(this.gridPos, EmcActionPacket.ACTION_LEARN)))
                .bounds(this.left + 10, this.top + PANEL_H - 30, 130, 20)
                .build());
        this.addRenderableWidget(Button.builder(
                        Component.translatable("gui.emc_rs.learn.refresh"),
                        button -> rebuildAll())
                .bounds(this.left + 148, this.top + PANEL_H - 30, 60, 20)
                .build());

        rebuildAll();
    }

    private void rebuildAll() {
        this.allItems.clear();
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            IKnowledgeProvider provider = player.getCapability(PECapabilities.KNOWLEDGE_CAPABILITY)
                    .resolve().orElse(null);
            if (provider != null) {
                for (ItemInfo info : provider.getKnowledge()) {
                    ItemStack stack = info.createStack();
                    if (!stack.isEmpty()) {
                        stack.setCount(1);
                        this.allItems.add(stack);
                    }
                }
            }
        }
        this.allItems.sort(Comparator.comparing(
                stack -> stack.getHoverName().getString(), String.CASE_INSENSITIVE_ORDER));
        rebuildFiltered();
    }

    private void rebuildFiltered() {
        this.filteredItems.clear();
        String query = this.search == null
                ? ""
                : this.search.getValue().trim().toLowerCase(Locale.ROOT);
        for (ItemStack stack : this.allItems) {
            if (query.isEmpty()
                    || stack.getHoverName().getString().toLowerCase(Locale.ROOT).contains(query)) {
                this.filteredItems.add(stack);
            }
        }
        this.scroll = Math.max(0, Math.min(this.scroll, maxScroll()));
    }

    private int maxScroll() {
        return Math.max(0, (this.filteredItems.size() + COLS - 1) / COLS - ROWS);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0xD0101018);

        graphics.fill(this.left - 4, this.top - 4,
                this.left + PANEL_W + 4, this.top + PANEL_H + 4, 0xFF3A3152);
        graphics.fill(this.left, this.top,
                this.left + PANEL_W, this.top + PANEL_H, 0xF0181424);

        graphics.drawString(this.font, this.title, this.left + 10, this.top + 10, 0xFFFFFFFF, false);
        graphics.drawString(this.font,
                Component.translatable("gui.emc_rs.learn.count", this.filteredItems.size()),
                this.left + 10, this.top + 24, 0xFFBFB6DA, false);

        int gridTop = this.top + 60;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int index = (this.scroll + row) * COLS + col;
                int x = this.left + 10 + col * SLOT;
                int y = gridTop + row * SLOT;
                graphics.fill(x, y, x + 16, y + 16, 0xFF2A2438);
                if (index < this.filteredItems.size()) {
                    ItemStack stack = this.filteredItems.get(index);
                    graphics.renderItem(stack, x, y);
                    if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                        graphics.renderTooltip(this.font, stack, mouseX, mouseY);
                    }
                }
            }
        }

        if (this.filteredItems.isEmpty()) {
            graphics.drawString(this.font,
                    Component.translatable("gui.emc_rs.learn.empty"),
                    this.left + 10, gridTop + 42, 0xFF8F86A8, false);
        }

        if (maxScroll() > 0) {
            int barX = this.left + PANEL_W - 8;
            int barY = gridTop;
            graphics.fill(barX, barY, barX + 3, barY + ROWS * SLOT, 0xFF2A2438);
            int thumbH = Math.max(12, ROWS * SLOT / (maxScroll() + 1));
            int thumbY = barY + (ROWS * SLOT - thumbH) * this.scroll / Math.max(1, maxScroll());
            graphics.fill(barX, thumbY, barX + 3, thumbY + thumbH, 0xFF9A7CFF);
        }

        for (var renderable : this.renderables) {
            renderable.render(graphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int next = this.scroll + (delta > 0 ? -1 : 1);
        this.scroll = Math.max(0, Math.min(next, maxScroll()));
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
