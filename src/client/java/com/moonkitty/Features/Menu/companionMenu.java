package com.moonkitty.Features.Menu;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;

import com.moonkitty.Feature;
import com.moonkitty.FeatureManager;
import com.moonkitty.Features.esp;
import com.moonkitty.Features.freecam;
import com.moonkitty.Features.companion;

public class companionMenu extends Screen {
    private final Screen parent;

    public companionMenu(Screen parent) {
        super(Text.literal("MoonKitty Freecam Menu"));
        this.parent = parent;
    }

    public void closeMenu() {
        this.client.setScreen(parent);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        companion feature = FeatureManager.INSTANCE.getCompanionFeature();

        final int MIN_X = 0;
        final int MAX_X = this.width;

        final int MIN_Y = 0;
        final int MAX_Y = this.height;

        final int MIN_SPEED = 0;
        final int MAX_SPEED = 25;

        final int MIN_WIDTH = 1;
        final int MAX_WIDTH = 64;

        double initialNormY = (feature.getY() - MIN_Y) / (double) (MAX_Y - MIN_Y);
        if (initialNormY < 0.0)
            initialNormY = 0.0;
        if (initialNormY > 1.0)
            initialNormY = 1.0;

        double initialNorm = (feature.getX() - MIN_X) / (double) (MAX_X - MIN_X);
        if (initialNorm < 0.0)
            initialNorm = 0.0;
        if (initialNorm > 1.0)
            initialNorm = 1.0;

        double initialNormSpeed = (feature.getSpeed() - MIN_SPEED) / (double) (MAX_SPEED - MIN_SPEED);
        if (initialNormSpeed < 0.0)
            initialNormSpeed = 0.0;
        if (initialNormSpeed > 1.0)
            initialNormSpeed = 1.0;

        SliderWidget XcordSlider = new SliderWidget(centerX - 100, centerY - 60, 200, 20, Text.literal("X: "),
                initialNorm) {
            @Override
            protected void updateMessage() {
                int display = (int) Math.round(MIN_X + this.value * (MAX_X - MIN_X));
                this.setMessage(Text.literal("X: " + display));
            }

            @Override
            protected void applyValue() {
                int newX = (int) Math.round(MIN_X + this.value * (MAX_X - MIN_X));
                feature.setX(newX);
            }
        };

        TextFieldWidget GifName = new TextFieldWidget(
                this.textRenderer,
                centerX - 100,
                centerY,
                200,
                20,
                Text.literal("Name Of The Gif File (example.gif)"));

        SliderWidget YcordSlider = new SliderWidget(centerX - 100, centerY - 30, 200, 20, Text.literal("Y: "),
                initialNormY) {
            @Override
            protected void updateMessage() {
                int display = (int) Math.round(MIN_Y + this.value * (MAX_Y - MIN_Y));
                this.setMessage(Text.literal("Y: " + display));
            }

            @Override
            protected void applyValue() {
                int newY = (int) Math.round(MIN_Y + this.value * (MAX_Y - MIN_Y));
                feature.setY(newY);
            }
        };

        SliderWidget SpeedSlider = new SliderWidget(centerX - 100, centerY + 25, 200, 20,
                Text.literal("Ticks Per Frame: "),
                initialNormSpeed) {
            @Override
            protected void updateMessage() {
                int display = (int) Math.round(MIN_SPEED + this.value * (MAX_SPEED - MIN_SPEED));
                this.setMessage(Text.literal("Ticks Per Frame: " + display));
            }

            @Override
            protected void applyValue() {
                int newSpeed = (int) Math.round(MIN_SPEED + this.value * (MAX_SPEED - MIN_SPEED));
                feature.setSpeed(newSpeed);
            }
        };

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal(feature.name + ": " + (feature.isEnabled() ? "ON" : "OFF")),
                        button -> {
                            feature.toggle();
                            this.init();
                        }).dimensions(centerX - 100, centerY - 90, 200, 20).build());

        Text descriptionText = Text.literal(
                "Small Gif That You Can Move Around :3, Place Your Gif At The [YOUR MINECRAFT FOLDER]/moonkitty/ Folder And Type The Name At The TextBox");

        int textWidth = this.textRenderer.getWidth(descriptionText);

        this.addDrawableChild(
                new TextWidget(
                        (this.width - textWidth) / 2,
                        centerY - 130,
                        textWidth, 20,
                        descriptionText,
                        this.textRenderer));

        GifName.setChangedListener(value -> {
            feature.fileName = value;
            feature.refresh();
        });

        this.addDrawableChild(XcordSlider);
        XcordSlider.setMessage(Text.literal("X: " + feature.getX()));
        this.addDrawableChild(YcordSlider);
        YcordSlider.setMessage(Text.literal("Y: " + feature.getY()));
        this.addDrawableChild(SpeedSlider);
        SpeedSlider.setMessage(Text.literal("Ticks Per Frame: " + feature.getSpeed()));
        this.addDrawableChild(GifName);
        this.setFocused(GifName);
    }
}