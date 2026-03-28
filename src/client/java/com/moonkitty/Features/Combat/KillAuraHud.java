package com.moonkitty.Features.Combat;

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
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.AbstractClientPlayerEntity;

import java.util.ArrayList;
import java.util.List;

import com.moonkitty.Gui.Menu;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;

public class KillAuraHud {

        public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
        public static MinecraftClient McClient;

        static int screenWidth;
        static int screenHeight;

        static int verticalPadding = 75;

        static int panelInternalPadding = 10;

        boolean initialized = false;

        public static AbstractClientPlayerEntity target;

        public static void init() {
                McClient = MinecraftClient.getInstance();
                Menu menuObject = Menu.INSTANCE;

                HudRenderCallback.EVENT.register(
                                (DrawContext drawContext,
                                                net.minecraft.client.render.RenderTickCounter tickDeltaManager) -> {
                                        MinecraftClient mc = MinecraftClient.getInstance();

                                        if (target == null)
                                                return;

                                        if (McClient.getWindow() != null) {
                                                screenWidth = McClient.getWindow().getScaledWidth();
                                                screenHeight = McClient.getWindow().getScaledHeight();
                                        }

                                        net.minecraft.text.Text targetText = net.minecraft.text.Text
                                                        .literal("KillAura Target: " + target.getNameForScoreboard());
                                        int textWidth = mc.textRenderer.getWidth(targetText);
                                        int x = (screenWidth - textWidth) / 2;
                                        int y = screenHeight - mc.textRenderer.fontHeight - verticalPadding;

                                        net.minecraft.text.Text targetTextHP = net.minecraft.text.Text
                                                        .literal((String.format("HP: %.1f", target.getHealth())
                                                                        + String.format(" / %.1f",
                                                                                        target.getHealth())));
                                        int textWidth2 = mc.textRenderer.getWidth(targetTextHP);
                                        int x2 = (screenWidth - textWidth2) / 2;
                                        int y2 = screenHeight - mc.textRenderer.fontHeight - verticalPadding
                                                        - (mc.textRenderer.fontHeight * 2);

                                        net.minecraft.item.ItemStack heldStack = target.getMainHandStack();
                                        String heldName = "None";
                                        if (heldStack != null && !heldStack.isEmpty()) {
                                                heldName = heldStack.getName().getString();
                                        }

                                        net.minecraft.text.Text targetTextCooldown = net.minecraft.text.Text
                                                        .literal("Holding Item: " + heldName);

                                        int textWidthCooldown = mc.textRenderer.getWidth(targetTextCooldown);

                                        int xCooldown = (screenWidth - textWidthCooldown) / 2;
                                        int yCooldown = screenHeight - mc.textRenderer.fontHeight - verticalPadding
                                                        - mc.textRenderer.fontHeight;

                                        int panelWidth = textWidth + panelInternalPadding;
                                        int panelHeight = (mc.textRenderer.fontHeight * 3);

                                        int x3 = (screenWidth - panelWidth) / 2;
                                        int x4 = x3 + panelWidth;

                                        int y4 = screenHeight - verticalPadding;
                                        int y3 = y4 - panelHeight;

                                        drawContext.fill(x3, y3, x4, y4, 0x4D000000);

                                        drawContext.drawTextWithShadow(
                                                        mc.textRenderer,
                                                        targetTextHP,
                                                        x2, y,
                                                        0xFFFFFFFF);

                                        drawContext.drawTextWithShadow(
                                                        mc.textRenderer,
                                                        targetText,
                                                        x, y2,
                                                        0xFFFFFFFF);

                                        drawContext.drawTextWithShadow(
                                                        mc.textRenderer,
                                                        targetTextCooldown,
                                                        xCooldown, yCooldown,
                                                        0xFFFFFFFF);

                                });

        }

}