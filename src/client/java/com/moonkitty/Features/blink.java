package com.moonkitty.Features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.BooleanSetting;
import com.moonkitty.Category;
import com.moonkitty.Feature;
import com.moonkitty.Mixin.CameraAccessor;
import com.moonkitty.keybind.Keybind;
import com.moonkitty.keybind.KeybindManager;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DrawStyle;
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
import com.moonkitty.NumberSetting;

import net.minecraft.util.Identifier;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.ButtonWidget;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.Box;

import net.minecraft.world.debug.gizmo.GizmoDrawing;

public class blink extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");

    public boolean initialized = false;
    public boolean shouldCancelPacket;

    int tickCounter;

    public int tickTime = 4;
    public int tickCancelTime = 2;

    public int boxColor = 0xFF3eadad;

    MinecraftClient mc = MinecraftClient.getInstance();

    public Box lastHitBox;

    private NumberSetting tickTimeSetting;
    private NumberSetting tickCancelTimeSetting;

    @Override
    public void onEnable() {
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
        this.setCategory(Category.MOVEMENT);
        this.setEnabled(false);

        tickTimeSetting = new NumberSetting("interval(ticks)", 4.0, 1.0, 20.0, 1.0);
        addSetting(tickTimeSetting);
        tickCancelTimeSetting = new NumberSetting("cancel time(ticks)", 2.0, 1.0, 10.0, 1.0);
        addSetting(tickCancelTimeSetting);

        Keybind bind = new Keybind(this, GLFW.GLFW_KEY_Z);
        KeybindManager.registerKeybind(bind);
    }

    @Override
    public void init() {

        Menu menuObject = Menu.INSTANCE;

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            if (!this.isEnabled())
                return;

            if (lastHitBox != null) {
                GizmoDrawing.box(lastHitBox, DrawStyle.stroked(boxColor, 2));
            }
        });
    }

    @Override
    public void onDisable() {
        tickCounter = 0;
        shouldCancelPacket = false;
    }

    @Override
    public void tick(MinecraftClient client) {
        tickTime = tickTimeSetting.getValue().intValue();
        tickCancelTime = tickCancelTimeSetting.getValue().intValue();

        if (lastHitBox == null && client.player != null) {
            lastHitBox = client.player.getBoundingBox();
        }

        if (isEnabled()) {

            tickCounter++;

            if (tickCounter < tickCancelTime) {
                shouldCancelPacket = true;

                if (tickCounter == 1 && client.player != null) {
                    lastHitBox = client.player.getBoundingBox();
                }

            } else if (tickCounter < tickTime * 2) {
                shouldCancelPacket = false;
            } else {
                shouldCancelPacket = false;
                tickCounter = 0;
            }
        }

    }

}