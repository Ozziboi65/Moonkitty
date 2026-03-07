package com.moonkitty.Features.Menu;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextWidget;

import com.moonkitty.Feature;
import com.moonkitty.FeatureManager;
import com.moonkitty.Features.ChestEsp;

public class ChestEspMenu extends Screen {
    private final Screen parent;

    private int playerRed;
    private int playerGreen;
    private int playerBlue;

    public ChestEspMenu(Screen parent) {
        super(Text.literal("MoonKitty ESP Menu"));
        this.parent = parent;
    }

    public void closeMenu() {
        this.client.setScreen(parent);
    }

    private int toArgb(int R, int G, int B) {
        return (255 << 24) | (R << 16) | (G << 8) | B;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        ChestEsp Espfeature = FeatureManager.INSTANCE.getChestEspFeature();

        int color = Espfeature.chestColor;
        this.playerRed = (color >> 16) & 0xFF;
        this.playerGreen = (color >> 8) & 0xFF;
        this.playerBlue = color & 0xFF;
        SliderWidget colorRSlider = new SliderWidget(centerX - 100, centerY - 60, 200, 20,
                Text.literal("Normal Chest Red: "),
                this.playerRed / 255.0) {

            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Normal Chest Red: " + (int) (this.value * 255)));
            }

            @Override
            protected void applyValue() {
                playerRed = (int) (this.value * 255);
                int colorRGB = toArgb(playerRed, playerGreen, playerBlue);
                Espfeature.setChestColor(colorRGB);
            }
        };

        SliderWidget colorGSlider = new SliderWidget(centerX - 100, centerY - 30, 200, 20,
                Text.literal("Normal Chest Green: "),
                this.playerGreen / 255.0) {

            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Normal Chest Green: " + (int) (this.value * 255)));
            }

            @Override
            protected void applyValue() {
                playerGreen = (int) (this.value * 255);
                int colorRGB = toArgb(playerRed, playerGreen, playerBlue);
                Espfeature.setChestColor(colorRGB);
            }
        };

        SliderWidget colorBSlider = new SliderWidget(centerX - 100, centerY + 0, 200, 20,
                Text.literal("Normal Chest Blue: "),
                this.playerBlue / 255.0) {

            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Normal Chest Blue: " + (int) (this.value * 255)));
            }

            @Override
            protected void applyValue() {
                playerBlue = (int) (this.value * 255);
                int colorRGB = toArgb(playerRed, playerGreen, playerBlue);
                Espfeature.setChestColor(colorRGB);
            }
        };

        this.addDrawableChild(
                new TextWidget(
                        centerX - 150,
                        centerY - 120,
                        325, 20,
                        Text.literal("Highlights Storage Blocks"),
                        this.textRenderer));

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal(Espfeature.name + ": " + (Espfeature.isEnabled() ? "ON" : "OFF")),
                        button -> {
                            Espfeature.toggle();
                            this.init();
                        }).dimensions(centerX - 100, centerY - 90, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Show Chests: " + (Espfeature.getRenderChest() ? "ON" : "OFF")),
                        button -> {
                            Espfeature.toggleRenderChest();
                            this.init();
                        }).dimensions(centerX - 100, centerY + 30, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Show Ender Chests: " + (Espfeature.getRenderEnderChest() ? "ON" : "OFF")),
                        button -> {
                            Espfeature.toggleRenderEnderChest();
                            this.init();
                        }).dimensions(centerX - 100, centerY + 60, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Show Barrels: " + (Espfeature.getRenderBarrel() ? "ON" : "OFF")),
                        button -> {
                            Espfeature.toggleRenderBarrel();
                            this.init();
                        }).dimensions(centerX - 100, centerY + 90, 200, 20).build());

        this.addDrawableChild(colorRSlider);
        this.addDrawableChild(colorGSlider);
        this.addDrawableChild(colorBSlider);

    }
}
