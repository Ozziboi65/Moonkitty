package com.moonkitty.Features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.BooleanSetting;
import com.moonkitty.Category;
import com.moonkitty.Feature;
import com.moonkitty.MoonkittyClient;
import com.moonkitty.NumberSetting;
import com.moonkitty.Gui.Menu;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.entity.Entity;
import net.minecraft.client.MinecraftClient;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.entity.Entity;

import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.util.Hand;

import com.google.gson.JsonObject;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.entity.Entity;

import com.moonkitty.Features.Combat.KillAuraHud;

public class AntiKnockBack extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public static MinecraftClient client;

    public float verticalSpeed = 0.5f;
    public float horizontalSpeedMultiplier = 5f;

    private float targetY = 0;

    double x = 0, z = 0;

    double speed = 0.28 * horizontalSpeedMultiplier;

    private NumberSetting speedSetting;

    public AntiKnockBack() {
        this.name = "AntiKnockBack";
        this.feature_id = 323;
        this.setCategory(Category.MOVEMENT);
        this.setEnabled(false);

        speedSetting = new NumberSetting("Horizontal Speed", 4.0, 1.0, 10.0, 0.5);
        addSetting(speedSetting);
    }

    @Override
    public void init() {
        client = MinecraftClient.getInstance();
    }

    @Override
    public void tick(MinecraftClient client) {

        if (!isEnabled() || client.player == null || client.world == null)
            return;

        horizontalSpeedMultiplier = speedSetting.getValue().floatValue();

        double yaw = Math.toRadians(client.player.getYaw());
        double speed = 0.28 * horizontalSpeedMultiplier;
        double x = 0, z = 0; // reset every tick
        targetY = 0;

        if (client.options.jumpKey.isPressed())
            targetY = verticalSpeed;
        if (client.options.sneakKey.isPressed())
            targetY = -verticalSpeed;

        if (client.options.forwardKey.isPressed()) {
            x -= Math.sin(yaw) * speed;
            z += Math.cos(yaw) * speed;
        }
        if (client.options.backKey.isPressed()) {
            x += Math.sin(yaw) * speed;
            z -= Math.cos(yaw) * speed;
        }
        if (client.options.rightKey.isPressed()) {
            x -= Math.cos(yaw) * speed;
            z -= Math.sin(yaw) * speed;
        }
        if (client.options.leftKey.isPressed()) {
            x += Math.cos(yaw) * speed;
            z += Math.sin(yaw) * speed;
        }

        client.player.setVelocity(x, targetY, z);
        client.player.fallDistance = 0;
    }
}