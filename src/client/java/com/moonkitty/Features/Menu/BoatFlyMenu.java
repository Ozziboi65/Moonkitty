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
import com.moonkitty.Features.BoatFly;

public class BoatFlyMenu extends Screen {
    private final Screen parent;

    private int playerRed;
    private int playerGreen;
    private int playerBlue;

    public BoatFlyMenu(Screen parent) {
        super(Text.literal("MoonKitty BoatFly Menu"));
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

        BoatFly boatFeature = FeatureManager.INSTANCE.getBoatFlyFeature();

        final double maxSpeed = 3.0;
        SliderWidget speedSlider = new SliderWidget(centerX - 100, centerY - 10, 200, 20, Text.literal("Speed: "),
                boatFeature.getSpeed() / maxSpeed) {

            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Speed: " + String.format("%.2f", this.value * maxSpeed)));
            }

            @Override
            protected void applyValue() {
                boatFeature.setSpeed(this.value * maxSpeed);
            }
        };

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal(boatFeature.name + ": " + (boatFeature.isEnabled() ? "ON" : "OFF")),
                        button -> {
                            boatFeature.toggle();
                            this.init();
                        }).dimensions(centerX - 100, centerY - 90, 200, 20).build());

        this.addDrawableChild(speedSlider);

        final double maxFall = 1.0;
        SliderWidget fallSlider = new SliderWidget(centerX - 100, centerY + 25, 200, 20, Text.literal("Descent: "),
                boatFeature.getFallSpeed() / maxFall) {

            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Descent: " + String.format("%.3f b/s", this.value * maxFall)));
            }

            @Override
            protected void applyValue() {
                boatFeature.setFallSpeed(this.value * maxFall);
            }
        };

        this.addDrawableChild(fallSlider);

    }
}
