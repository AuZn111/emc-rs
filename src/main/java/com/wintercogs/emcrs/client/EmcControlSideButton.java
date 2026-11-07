package com.wintercogs.emcrs.client;

import com.refinedmods.refinedstorage.screen.BaseScreen;
import com.refinedmods.refinedstorage.screen.widget.sidebutton.SideButton;
import com.wintercogs.emcrs.EMCRS;
import com.wintercogs.emcrs.network.EmcActionPacket;
import com.wintercogs.emcrs.network.EmcNetworkHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

public class EmcControlSideButton extends SideButton {
    private static final ResourceLocation ICON =
            new ResourceLocation(EMCRS.MODID, "textures/gui/emc_button.png");

    private final BlockPos gridPos;

    public EmcControlSideButton(BaseScreen<?> screen, BlockPos gridPos) {
        super(screen);
        this.gridPos = gridPos;
    }

    @Override
    public void onPress() {
        int action = Screen.hasControlDown()
                ? EmcActionPacket.ACTION_BULK_SELL
                : Screen.hasShiftDown()
                ? EmcActionPacket.ACTION_REBIND
                : EmcActionPacket.ACTION_STATUS;
        EmcNetworkHandler.sendToServer(new EmcActionPacket(this.gridPos, action));
    }

    @Override
    protected void renderButtonIcon(GuiGraphics graphics, int x, int y) {
        // 256x256 sheet with the actual 16x16 art in the top-left.
        graphics.blit(ICON, x, y, 0, 0, 16, 16);
    }

    @Override
    protected String getSideButtonTooltip() {
        return I18n.get("sidebutton.emc_rs.emc")
                + "\n"
                + ChatFormatting.GRAY
                + I18n.get("sidebutton.emc_rs.emc.hint");
    }
}
