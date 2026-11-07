package com.wintercogs.emcrs.mixin;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.network.Network;
import com.wintercogs.emcrs.tick.EmcNetworkTicker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Network.class)
public class NetworkMixin {
    @Inject(method = "update", at = @At("TAIL"))
    private void emcrs$tickNetwork(CallbackInfo ci) {
        EmcNetworkTicker.tick((INetwork) (Object) this);
    }
}
