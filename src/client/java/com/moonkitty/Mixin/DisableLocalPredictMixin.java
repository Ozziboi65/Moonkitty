package com.moonkitty.Mixin;

import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.moonkitty.FeatureManager;
import net.minecraft.client.network.ClientPlayerEntity;

@Mixin(ClientPlayerEntity.class)
public abstract class DisableLocalPredictMixin {

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    private void onTickMovement(CallbackInfo ci) {
        if (FeatureManager.INSTANCE.getFreecamFeature().cancelPackets) {
            ci.cancel();
        }
    }
}