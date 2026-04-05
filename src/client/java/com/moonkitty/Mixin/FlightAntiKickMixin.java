package com.moonkitty.Mixin;

import com.moonkitty.FeatureManager;
import com.moonkitty.Features.Flight;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonNetworkHandler.class)
public class FlightAntiKickMixin {

    @Inject(method = "sendPacket", at = @At("HEAD"))
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        if (!(packet instanceof PlayerMoveC2SPacket movePacket)) {
            return;
        }

        Flight flight = FeatureManager.INSTANCE.getFlightFeature();
        if (flight == null) {
            return;
        }

        flight.onPacketSend(movePacket);
    }
}
