package com.moonkitty.Features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.Feature;
import com.moonkitty.Mixin.CameraAccessor;
import net.minecraft.client.render.Camera;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.minecraft.client.render.Camera;

import com.moonkitty.Gui.Menu;

import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import java.util.ArrayList;

import org.lwjgl.glfw.GLFW;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.MinecraftClient;
import com.moonkitty.MoonkittyClient;
import net.minecraft.util.Identifier;
import com.moonkitty.Features.Menu.BlinkMenu;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.ButtonWidget;
import java.util.ArrayList;
import java.util.List;

public class blink extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public MinecraftClient McClient;

    public Vec3d Freecam_position = Vec3d.ZERO;
    public Vec3d Freecam_orginal_pos = Vec3d.ZERO;

    public boolean initialized = false;

    int Pulse_tick_delta = 3;

    int Cancel_duration = 1;

    boolean Should_Cancel_packet;

    int tickCounter;

    int cancelTicksRemaining = 0;

    MinecraftClient mc = MinecraftClient.getInstance();

    public static final List<PlayerMoveC2SPacket> buffer = new ArrayList<>();

    public static boolean isFlushing = false;

    PlayerEntity player = MinecraftClient.getInstance().player;

    public static Vec3d savedPos = Vec3d.ZERO;

    @Override
    protected void onEnable() {
        if (!initialized) {

            HudRenderCallback.EVENT.register(
                    (DrawContext drawContext, net.minecraft.client.render.RenderTickCounter tickDeltaManager) -> {

                        MinecraftClient mc = MinecraftClient.getInstance();

                        if (isEnabled()) {
                            drawContext.drawTextWithShadow(
                                    mc.textRenderer,
                                    net.minecraft.text.Text.literal("Blink Is Enabled!"),
                                    200, 200,
                                    0xFFFF00FF);
                        }
                    });

            initialized = true;
            LOGGER.info("Blink Hud Inited");
        }
    }

    public blink() {
        this.name = "Blink";
        this.feature_id = 7;
        this.setEnabled(false);

    }

    public static void flushPackets() {
        isFlushing = true;

        savedPos = new Vec3d(
                MinecraftClient.getInstance().player.getX(),
                MinecraftClient.getInstance().player.getY(),
                MinecraftClient.getInstance().player.getZ());

        List<PlayerMoveC2SPacket> copy = new ArrayList<>(buffer); // copy
        buffer.clear();
        for (PlayerMoveC2SPacket packet : copy) {
            MinecraftClient.getInstance().getNetworkHandler().sendPacket(packet);
        }
        isFlushing = false;
    }

    public int getPulse() {
        return Pulse_tick_delta;
    }

    public int getCancelDuration() {
        return Cancel_duration;
    }

    public void setCancelDuration(int wanted) {
        Cancel_duration = wanted;
    }

    public boolean getShouldCancelPacket() {
        return Should_Cancel_packet;
    }

    public void setPulse(int wanted) {
        Pulse_tick_delta = wanted;
    }

    @Override
    public void init() {

        Menu menuObject = Menu.INSTANCE;

        menuObject.registerNewFeatureButton(
                ButtonWidget.builder(
                        Text.literal("Blink"),
                        btn -> {
                            MinecraftClient.getInstance().setScreen(new BlinkMenu(Menu.INSTANCE));
                        }).dimensions(100, Menu.INSTANCE.getNextY(), 200, 20).build());
    }

    @Override
    protected void onDisable() {
        System.out.println("disabled blink");
        flushPackets();
    }

    @Override
    public void tick(MinecraftClient client) {

        if (MoonkittyClient.TOGGLE_BLINK.wasPressed()) {
            LOGGER.info("Toggle Blink bind pressed");
            this.toggle();
        }

        if (this.isEnabled()) {
            tickCounter++;

            if (Pulse_tick_delta <= 0)
                Pulse_tick_delta = 1;
            if (Cancel_duration <= 0)
                Cancel_duration = 1;

            if (tickCounter >= Pulse_tick_delta) {
                cancelTicksRemaining = Cancel_duration;
                flushPackets();
                tickCounter = 0;
            }

            if (cancelTicksRemaining > 0) {
                Should_Cancel_packet = true;
                cancelTicksRemaining--;
            } else {
                Should_Cancel_packet = false;
            }

        }

    }

}