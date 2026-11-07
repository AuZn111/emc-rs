package com.wintercogs.emcrs.network;

import com.wintercogs.emcrs.EMCRS;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public final class EmcNetworkHandler {
    private static final String PROTOCOL = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(EMCRS.MODID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals);

    private EmcNetworkHandler() {
    }

    public static void register() {
        CHANNEL.registerMessage(0, EmcActionPacket.class,
                EmcActionPacket::encode,
                EmcActionPacket::decode,
                EmcActionPacket::handle);
    }

    public static void sendToServer(EmcActionPacket packet) {
        CHANNEL.sendToServer(packet);
    }
}
