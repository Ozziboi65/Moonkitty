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

        final int MIN_SPEED = 1;
        final int MAX_SPEED = 85;

        double initialNorm = (feature.getPulse() - MIN_SPEED) / (double) (MAX_SPEED - MIN_SPEED);
        if (initialNorm < 0.0)
            initialNorm = 0.0;
        if (initialNorm > 1.0)
            initialNorm = 1.0;

        SliderWidget PulseSlider = new SliderWidget(centerX - 100, centerY - 60, 200, 20,
                Text.literal("Pulse(In Ticks): "),
                initialNorm) {

            @Override
            protected void updateMessage() {
                int display = (int) Math.round(MIN_SPEED + this.value * (MAX_SPEED - MIN_SPEED));
                this.setMessage(Text.literal("Pulse(In Ticks): " + display));
            }

            @Override
            protected void applyValue() {
                int newValue = (int) Math.round(MIN_SPEED + this.value * (MAX_SPEED - MIN_SPEED));
                feature.setPulse(newValue);
            }
        };

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

        this.addDrawableChild(PulseSlider);

    }
}
