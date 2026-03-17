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
import com.moonkitty.Features.blink;
import com.moonkitty.Features.esp;
import com.moonkitty.Gui.ColorPicker;

public class BlinkMenu extends Screen {
    private final Screen parent;

    int Pulsedelta;

    public BlinkMenu(Screen parent) {
        super(Text.literal("MoonKitty Blink Menu"));
        this.parent = parent;
    }

    public void closeMenu() {
        this.client.setScreen(parent);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        blink feature = FeatureManager.INSTANCE.getBlinkFeature();

        final int MIN_CANCEL = 1;
        final int MAX_CANCEL = 60;

        double initialNormCancel = (feature.tickCancelTime - MIN_CANCEL) / (double) (MAX_CANCEL - MIN_CANCEL);
        if (initialNormCancel < 0.0)
            initialNormCancel = 0.0;
        if (initialNormCancel > 1.0)
            initialNormCancel = 1.0;

        SliderWidget cancelTimeSlider = new SliderWidget(centerX - 100, centerY - 60, 200, 20,
                Text.literal("Tick Pause Time: "),
                initialNormCancel) {
            @Override
            protected void updateMessage() {
                int display = (int) Math.round(MIN_CANCEL + this.value * (MAX_CANCEL - MIN_CANCEL));
                this.setMessage(Text.literal("Tick Pause Time: " + display));
            }

            @Override
            protected void applyValue() {
                int newValue = (int) Math.round(MIN_CANCEL + this.value * (MAX_CANCEL - MIN_CANCEL));
                feature.tickCancelTime = newValue;
            }
        };

        this.addDrawableChild(cancelTimeSlider);
        cancelTimeSlider.setMessage(Text.literal("Tick Pause Time: " + feature.tickCancelTime));

        final int MIN_TIME = 1;
        final int MAX_TIME = 60;

        double initialNormTime = (feature.tickCancelTime - MIN_TIME) / (double) (MAX_TIME - MIN_TIME);
        if (initialNormTime < 0.0)
            initialNormTime = 0.0;
        if (initialNormTime > 1.0)
            initialNormTime = 1.0;

        SliderWidget timeSlider = new SliderWidget(centerX - 100, centerY - 30, 200, 20,
                Text.literal("Tick Interval Time: "),
                initialNormTime) {
            @Override
            protected void updateMessage() {
                int display = (int) Math.round(MIN_TIME + this.value * (MAX_TIME - MIN_TIME));
                this.setMessage(Text.literal("Tick Interval Time: " + display));
            }

            @Override
            protected void applyValue() {
                int newValue = (int) Math.round(MIN_TIME + this.value * (MAX_TIME - MIN_TIME));
                feature.tickTime = newValue;
            }
        };

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Hitbox Color"),
                        btn -> {
                            int current = feature.boxColor;
                            client.setScreen(new ColorPicker(this, current, c -> {
                                feature.boxColor = c;
                            }));
                        }).dimensions(centerX - 100, centerY, 200, 20).build());

        this.addDrawableChild(timeSlider);
        timeSlider.setMessage(Text.literal("Tick Interval Time: " + feature.tickTime));

        this.addDrawableChild(
                new TextWidget(
                        centerX - 150,
                        centerY - 120,
                        300, 20,
                        Text.literal("Makes Your Movemnt 'Fake lag'"),
                        this.textRenderer));

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal(feature.name + ": " + (feature.isEnabled() ? "ON" : "OFF")),
                        button -> {
                            feature.toggle();
                            this.init();
                        }).dimensions(centerX - 100, centerY - 90, 200, 20).build());

    }
}
