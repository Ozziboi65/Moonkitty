package com.moonkitty.Mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.moonkitty.Features.BoatFly;

import java.lang.Math;

import net.minecraft.client.render.Camera;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.network.packet.c2s.play.VehicleMoveC2SPacket;
import net.minecraft.client.MinecraftClient;
import com.moonkitty.FeatureManager;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.*;

@Mixin(ClientPlayerEntity.class)
public abstract class BoatFlyMixin {

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    private void onSendMovementPackets(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!(client.player.hasVehicle() && client.player.getVehicle() instanceof BoatEntity))
            return;

        Entity vehicle = client.player.getVehicle();

        BoatFly boat_feature = FeatureManager.INSTANCE.getBoatFlyFeature();

        double yawRad = Math.toRadians(client.player.getYaw());
        double speed = boat_feature.getSpeed();

        if (!boat_feature.isEnabled())
            return;

        int forward = client.options.forwardKey.isPressed() ? 1 : (client.options.backKey.isPressed() ? -1 : 0);
        int strafe = client.options.leftKey.isPressed() ? 1 : (client.options.rightKey.isPressed() ? -1 : 0);

        double dx = (-Math.sin(yawRad) * forward + Math.cos(yawRad) * strafe) * speed;
        double dz = (Math.cos(yawRad) * forward + Math.sin(yawRad) * strafe) * speed;

        double newX = vehicle.getX() + dx;
        double newZ = vehicle.getZ() + dz;

        double targetY = boat_feature.getTargetY();
        if (Double.isNaN(targetY)) {
            targetY = vehicle.getY();
        }

        // adjust targetY from input; when no vertical input is pressed, slowly descend
        if (client.options.jumpKey.isPressed()) {
            targetY += 0.5;
        } else if (client.options.sneakKey.isPressed()) {
            targetY -= 0.5;
        } else {

            double fallSpeed = boat_feature.getFallSpeed();
            if (fallSpeed > 0.0) {
                double perTickFall = fallSpeed / 20.0;
                targetY -= perTickFall;
            }
        }

        boat_feature.setTargetY(targetY);

        double newY = targetY;

        vehicle.setPosition(newX, newY, newZ);

        client.getNetworkHandler().sendPacket(
                new VehicleMoveC2SPacket(
                        new Vec3d(newX, newY, newZ),
                        client.player.getYaw(),
                        client.player.getPitch(),
                        false));

        ci.cancel();
    }
}