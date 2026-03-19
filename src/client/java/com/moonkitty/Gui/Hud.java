package com.moonkitty.Gui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.Feature;
import com.moonkitty.FeatureManager;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class Hud {

    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");

    static int screenWidth;
    static int screenHeight;

    static int HackListverticalPadding = 75;

    boolean initialized = false;

    static int nextY;

    static int linePadding = 15;

    static int cordsX = 35;
    static int cordsY;
    static int cordsColor = 0xFF00fbff;

    public static void init() {
        HudRenderCallback.EVENT.register(
                (DrawContext drawContext, net.minecraft.client.render.RenderTickCounter tickDeltaManager) -> {
                    MinecraftClient client = MinecraftClient.getInstance();

                    if (client.getWindow() != null) {
                        screenWidth = client.getWindow().getScaledWidth();
                        screenHeight = client.getWindow().getScaledHeight();
                    }

                    nextY = HackListverticalPadding;

                    for (Feature feature : FeatureManager.INSTANCE.featureList) {
                        if (!feature.isEnabled())
                            continue;

                        Text featureText = Text.literal(feature.name);

                        int textWidth = client.textRenderer.getWidth(featureText);
                        int x = screenWidth - textWidth - 10;

                        drawContext.drawTextWithShadow(
                                client.textRenderer,
                                featureText,
                                x, nextY,
                                0xFFFFFFFF);

                        nextY += linePadding;
                    }

                    Text cordText = Text.literal(
                            "X:" + client.player.getBlockX() +
                                    ", Y: " + client.player.getBlockY() +
                                    ", Z: " + client.player.getBlockZ());

                    cordsY = screenHeight - (screenHeight / 3);
                    drawContext.drawTextWithShadow(
                            client.textRenderer,
                            cordText,
                            cordsX, cordsY,
                            cordsColor);

                    nextY += linePadding;

                });

    }

}