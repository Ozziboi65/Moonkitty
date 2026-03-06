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

import java.lang.Math;

import net.minecraft.client.render.Camera;
import com.moonkitty.FeatureManager;
import com.moonkitty.Features.freecam;

import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.*;

@Mixin(Camera.class)
public abstract class FreecamMixin {

    @Shadow
    protected abstract void setPos(double x, double y, double z);

    @Shadow
    protected abstract Vec3d getCameraPos();

    @Shadow
    protected abstract float getYaw();

    freecam freecam_feature = FeatureManager.INSTANCE.getFreecamFeature();

    MinecraftClient client = MinecraftClient.getInstance();

    @Inject(at = @At("TAIL"), method = "update", cancellable = true)
    private void update(World area, Entity focusedEntity, boolean thirdPerson, boolean inverseView, float tickProgress,
            CallbackInfo ci) {

        if (!freecam_feature.isEnabled())
            return;

        freecam_feature.Freecam_yaw = Math.toRadians(this.getYaw());

        if (client.options.forwardKey.isPressed()) {
            freecam_feature.Freecam_position = freecam_feature.Freecam_position
                    .add(-Math.sin(freecam_feature.Freecam_yaw) * freecam_feature.speed, 0,
                            Math.cos(freecam_feature.Freecam_yaw) * freecam_feature.speed);
        }

        if (client.options.backKey.isPressed()) {
            freecam_feature.Freecam_position = freecam_feature.Freecam_position
                    .add(Math.sin(freecam_feature.Freecam_yaw) * freecam_feature.speed, 0,
                            -Math.cos(freecam_feature.Freecam_yaw) * freecam_feature.speed);
        }

        if (client.options.rightKey.isPressed()) {
            freecam_feature.Freecam_position = freecam_feature.Freecam_position
                    .add(-Math.sin(freecam_feature.Freecam_yaw + Math.PI / 2) * freecam_feature.speed, 0,
                            Math.cos(freecam_feature.Freecam_yaw + Math.PI / 2) * freecam_feature.speed);
        }

        if (client.options.leftKey.isPressed()) {
            freecam_feature.Freecam_position = freecam_feature.Freecam_position
                    .add(Math.sin(freecam_feature.Freecam_yaw + Math.PI / 2) * freecam_feature.speed, 0,
                            -Math.cos(freecam_feature.Freecam_yaw + Math.PI / 2) * freecam_feature.speed);
        }

        if (client.options.sneakKey.isPressed()) {
            freecam_feature.Freecam_position = freecam_feature.Freecam_position.add(0, -freecam_feature.speed, 0);
        }
        if (client.options.jumpKey.isPressed()) {
            freecam_feature.Freecam_position = freecam_feature.Freecam_position.add(0, freecam_feature.speed, 0);
        }

        this.setPos(freecam_feature.Freecam_position.x, freecam_feature.Freecam_position.y,
                freecam_feature.Freecam_position.z);

    }

}
