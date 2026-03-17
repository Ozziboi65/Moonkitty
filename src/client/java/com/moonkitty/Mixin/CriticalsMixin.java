package com.moonkitty.Mixin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.moonkitty.Features.Combat.Criticals;
import com.moonkitty.FeatureManager;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@Mixin(ClientCommonNetworkHandler.class)
public class CriticalsMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    private static boolean sendingCritical = false;

    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {

        if (sendingCritical)
            return;

        if (!(packet instanceof PlayerInteractEntityC2SPacket p))
            return;

        Criticals criticals = FeatureManager.INSTANCE.getCritsFeature();

        if (criticals == null || !criticals.isEnabled())
            return;

        boolean canCrit = criticals.canCrit();

        if (!canCrit) {
            return;
        }

        ci.cancel();

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.getNetworkHandler() == null) {
            LOGGER.error("Network handler is null!");
            return;
        }

        sendingCritical = true;
        try {

            criticals.sendFakePacket(0.0625);

            criticals.sendFakePacket(0);

            client.getNetworkHandler().sendPacket(packet);
        } finally {
            sendingCritical = false;
        }
    }
}