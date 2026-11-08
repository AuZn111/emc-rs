package com.wintercogs.emcrs.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class EmcActionPacket {
    public static final int ACTION_STATUS = 0;
    public static final int ACTION_REBIND = 1;
    public static final int ACTION_BULK_SELL = 2;
    public static final int ACTION_LEARN = 3;

    private final BlockPos gridPos;
    private final int action;

    public EmcActionPacket(BlockPos gridPos, int action) {
        this.gridPos = gridPos;
        this.action = action;
    }

    public BlockPos getGridPos() {
        return this.gridPos;
    }

    public int getAction() {
        return this.action;
    }

    public static void encode(EmcActionPacket packet, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(packet.gridPos);
        buffer.writeByte(packet.action);
    }

    public static EmcActionPacket decode(FriendlyByteBuf buffer) {
        return new EmcActionPacket(buffer.readBlockPos(), buffer.readByte());
    }

    public static void handle(EmcActionPacket packet, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        if (context.getSender() != null) {
            context.enqueueWork(() -> EmcNetworkActions.handle(
                    context.getSender(), packet.gridPos, packet.action));
        }
        context.setPacketHandled(true);
    }
}
