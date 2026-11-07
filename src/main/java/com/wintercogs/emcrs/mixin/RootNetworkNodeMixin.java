package com.wintercogs.emcrs.mixin;

import com.refinedmods.refinedstorage.apiimpl.network.node.RootNetworkNode;
import com.wintercogs.emcrs.data.EmcNetworkData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

/**
 * RS's controller root node has no owner field (setOwner is a no-op). Capture
 * the controller placer here so the network has a stable ProjectE EMC owner.
 */
@Mixin(RootNetworkNode.class)
public abstract class RootNetworkNodeMixin {
    @Shadow
    @Final
    private Level level;

    @Shadow
    @Final
    private BlockPos pos;

    @Inject(method = "setOwner", at = @At("HEAD"))
    private void emcrs$setOwner(UUID owner, CallbackInfo ci) {
        if (owner != null && this.level instanceof ServerLevel serverLevel) {
            EmcNetworkData data = EmcNetworkData.get(serverLevel);
            data.setOwner(this.pos, owner);
        }
    }

    @Inject(method = "getOwner", at = @At("HEAD"), cancellable = true)
    private void emcrs$getOwner(CallbackInfoReturnable<UUID> cir) {
        if (this.level instanceof ServerLevel serverLevel) {
            UUID owner = EmcNetworkData.get(serverLevel).getOwner(this.pos);
            if (owner != null) {
                cir.setReturnValue(owner);
            }
        }
    }
}
