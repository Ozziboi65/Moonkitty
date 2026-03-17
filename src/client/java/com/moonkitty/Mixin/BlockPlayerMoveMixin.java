package com.moonkitty.Mixin;

import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.MinecraftClient;
import com.moonkitty.FeatureManager;
import com.moonkitty.Features.blink;

import java.util.ArrayList;
import java.util.List;

@Mixin(ClientCommonNetworkHandler.class)
public abstract class BlockPlayerMoveMixin {

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {

        if (FeatureManager.INSTANCE.getBlinkFeature().shouldCancelPacket
                && packet instanceof PlayerMoveC2SPacket) {
            ci.cancel();
        }

        if (FeatureManager.INSTANCE.getFreecamFeature().isEnabled()
                && packet instanceof PlayerMoveC2SPacket) {

            ci.cancel();
        }
    }
}
