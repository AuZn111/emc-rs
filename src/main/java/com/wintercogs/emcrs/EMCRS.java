package com.wintercogs.emcrs;

import com.wintercogs.emcrs.config.EmcConfig;
import com.wintercogs.emcrs.network.EmcNetworkHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EMCRS.MODID)
public class EMCRS {
    public static final String MODID = "emc_rs";

    public EMCRS() {
        ModLoadingContext.get().registerConfig(
                ModConfig.Type.COMMON, EmcConfig.SPEC, "emc_rs-common.toml");

        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(EmcNetworkHandler::register);
    }
}
