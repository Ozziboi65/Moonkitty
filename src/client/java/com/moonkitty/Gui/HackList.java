package com.moonkitty.Gui;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.moonkitty.Feature;
import com.moonkitty.Mixin.CameraAccessor;
import net.minecraft.client.render.Camera;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.minecraft.client.render.Camera;

import com.moonkitty.Gui.Menu;
import com.moonkitty.FeatureManager;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.Entity;
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
import net.minecraft.client.network.AbstractClientPlayerEntity;

import java.util.ArrayList;
import java.util.List;

import com.moonkitty.Features.Menu.KillAuraMenu;
import com.moonkitty.Gui.Menu;
import com.moonkitty.FeatureManager;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class HackList {

    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public static MinecraftClient McClient;

    static int screenWidth;
    static int screenHeight;

    static int verticalPadding = 75;

    boolean initialized = false;

    static int nextY;

    static int linePadding = 15;

    public static void init() {
        McClient = MinecraftClient.getInstance();
        Menu menuObject = Menu.INSTANCE;

        HudRenderCallback.EVENT.register(
                (DrawContext drawContext, net.minecraft.client.render.RenderTickCounter tickDeltaManager) -> {
                    MinecraftClient mc = MinecraftClient.getInstance();

                    if (McClient.getWindow() != null) {
                        screenWidth = McClient.getWindow().getScaledWidth();
                        screenHeight = McClient.getWindow().getScaledHeight();
                    }

                    nextY = verticalPadding;

                    for (Feature feature : FeatureManager.INSTANCE.featureList) {
                        if (!feature.isEnabled())
                            continue;

                        Text featureText = Text.literal(feature.name);

                        int textWidth = mc.textRenderer.getWidth(featureText);
                        int x = screenWidth - textWidth - 10;

                        drawContext.drawTextWithShadow(
                                mc.textRenderer,
                                featureText,
                                x, nextY,
                                0xFFFFFFFF);

                        nextY += linePadding;
                    }

                });

    }

}