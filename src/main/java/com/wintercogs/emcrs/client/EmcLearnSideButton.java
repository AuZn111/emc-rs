package com.wintercogs.emcrs.client;

import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.wintercogs.emcrs.EMCRS;
import com.wintercogs.emcrs.network.EmcActionPacket;
import com.wintercogs.emcrs.network.EmcNetworkHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

/**
 * "Learning bar" control. It records the item on the player's cursor, or the
 * terminal's four filter slots as a fallback, into ProjectE knowledge.
 */
public class EmcLearnSideButton extends SideButton {
    private static final ResourceLocation ICON =
            new ResourceLocation(EMCRS.MODID, "textures/gui/learn_button.png");

    private final BlockPos gridPos;

    public EmcLearnSideButton(BaseScreen<?> screen, BlockPos gridPos) {
        super(screen);
        this.gridPos = gridPos;
    }

    @Override
    public void onPress() {
        EmcNetworkHandler.sendToServer(
                new EmcActionPacket(this.gridPos, EmcActionPacket.ACTION_OPEN_TABLE));
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        graphics.blit(ICON, x, y, 0, 0, 16, 16);
    }

    @Override
    protected String getSideButtonTooltip() {
        return I18n.get("sidebutton.emc_rs.learn")
                + "\n"
                + ChatFormatting.GRAY
                + I18n.get("sidebutton.emc_rs.learn.hint");
    }
}
