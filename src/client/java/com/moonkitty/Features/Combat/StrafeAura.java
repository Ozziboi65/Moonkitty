package com.moonkitty.Features.Combat;

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

import com.moonkitty.Util.ConfigUtil;
import com.google.gson.JsonObject;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.entity.Entity;

import com.moonkitty.Features.Combat.KillAuraHud;

public class StrafeAura extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public static MinecraftClient client;

    public float minTargetDistance = 6f;
    public float maxDist = 2f;
    public float minDist = 0.2f;
    public float strength = 2f;
    public float maxY = 1f;
    private int randomTickCounter = 0;
    private Vec3d randomDirection = Vec3d.ZERO;
    public int changeRate = 10;
    public boolean cancelVelocity = true;

    private BooleanSetting cancelVelocitySetting;
    private NumberSetting radiusSetting;
    private NumberSetting strengthSetting;

    public float getMaxDist() {
        return maxDist;
    }

    public void setMaxDist(float value) {
        maxDist = value;
    }

    public float getTargetDist() {
        return minTargetDistance;
    }

    public void setTargetDist(float value) {
        minTargetDistance = value;
    }

    public boolean getCancelVelocity() {
        return cancelVelocity;
    }

    public void setancelVelocity(boolean enabled) {
        if (this.cancelVelocity == enabled)
            return;
        this.cancelVelocity = enabled;
    }

    public void toggleancelVelocity() {
        setancelVelocity(!this.cancelVelocity);
    }

    public StrafeAura() {
        this.name = "Strafe Aura";
        this.feature_id = 323;
        this.setCategory(Category.COMBAT);
        this.setEnabled(false);

        cancelVelocitySetting = new BooleanSetting("Cancel External Velocity", true);
        addSetting(cancelVelocitySetting);

        radiusSetting = new NumberSetting("Radius", 2.0, 1.0, 5.0, 0.5);
        addSetting(radiusSetting);

        strengthSetting = new NumberSetting("Strength", 2.0, 1.0, 5.0, 0.5);
        addSetting(strengthSetting);
    }

    @Override
    public void init() {
        client = MinecraftClient.getInstance();
        Menu menuObject = Menu.INSTANCE;

        KillAuraHud.init();

        // this.reach = (float) ConfigUtil.getDouble("killAura.range", this.reach);
        // this.attackDelay = ConfigUtil.getInt("killAura.delayMs", this.attackDelay);
        // this.setEnabled(ConfigUtil.getBoolean("killAura.enabled", isEnabled()));

    }

    @Override
    public void tick(MinecraftClient client) {

        if (!isEnabled() || client.player == null || client.world == null)
            return;

        cancelVelocity = cancelVelocitySetting.getValue();
        maxDist = radiusSetting.getValue().floatValue();
        strength = strengthSetting.getValue().floatValue();

        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {

            if (player == client.player)
                continue;

            double dx = client.player.getX() - player.getX();
            double dy = client.player.getY() - player.getY();
            double dz = client.player.getZ() - player.getZ();

            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq > minTargetDistance * minTargetDistance)
                continue;

            if (cancelVelocity) {
                client.player.setVelocity(0, client.player.getVelocity().y, 0);
            }

            Vec3d playerPos = new Vec3d(client.player.getX(), client.player.getY(), client.player.getZ());
            Vec3d targetPos = new Vec3d(player.getX(), player.getY(), player.getZ());

            Vec3d toTarget = targetPos.subtract(playerPos);
            double dist = Math.sqrt(toTarget.x * toTarget.x + toTarget.z * toTarget.z);
            Vec3d dir = toTarget.normalize();

            Vec3d strafeDir = new Vec3d(-dir.z, 0, dir.x).normalize();

            if (Math.abs(client.player.getVelocity().y) < maxY) { // check Y so it doesent try to kill it self
                if (dist > maxDist) { // if too far push to target

                    client.player.addVelocity(dir.multiply(strength));

                } else if (dist < minDist) {
                    if (randomTickCounter <= 0) {
                        randomDirection = new Vec3d(
                                (Math.random() - 0.5) * 2,
                                0,
                                (Math.random() - 0.5) * 2).normalize();
                        randomTickCounter = changeRate;
                    }
                    randomTickCounter--;
                    client.player.addVelocity(randomDirection.multiply(strength));
                } else {

                    client.player.addVelocity(strafeDir.multiply(strength));
                }
            }
        }
    }
}