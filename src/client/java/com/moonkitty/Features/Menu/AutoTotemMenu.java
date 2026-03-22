package com.moonkitty.Features.Menu;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.SliderWidget;
import com.moonkitty.Feature;
import com.moonkitty.FeatureManager;
import com.moonkitty.Features.AutoTotem;
import com.moonkitty.Features.esp;
import com.moonkitty.Features.freecam;
import com.moonkitty.Features.triggerbot;

public class AutoTotemMenu extends Screen {
    private final Screen parent;

    public AutoTotemMenu(Screen parent) {
        super(Text.literal("MoonKitty Autototem Menu"));
        this.parent = parent;
    }

    public void closeMenu() {
        this.client.setScreen(parent);
    }

    final int MIN_DELAY = 0;
    final int MAX_DELAY = 1000;

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        AutoTotem feature = FeatureManager.INSTANCE.getAutoTotemFeature();

        double initialNorm = (feature.getDelay() - MIN_DELAY) / (double) (MAX_DELAY - MIN_DELAY);
        if (initialNorm < 0.0)
            initialNorm = 0.0;
        if (initialNorm > 1.0)
            initialNorm = 1.0;

        SliderWidget delaySlider = new SliderWidget(centerX - 100, centerY - 60, 200, 20, Text.literal("Delay(ms): "),
                initialNorm) {
            @Override
            protected void updateMessage() {
                int display = (int) Math.round(MIN_DELAY + this.value * (MAX_DELAY - MIN_DELAY));
                this.setMessage(Text.literal("Delay(ms): " + display));
            }

            @Override
            protected void applyValue() {
                int newValue = (int) Math.round(MIN_DELAY + this.value * (MAX_DELAY - MIN_DELAY));
                feature.setDelay(newValue);
            }
        };

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal(feature.name + ": " + (feature.isEnabled() ? "ON" : "OFF")),
                        button -> {
                            feature.toggle();
                            this.init();
                        }).dimensions(centerX - 100, centerY - 90, 200, 20).build());

        this.addDrawableChild(delaySlider);
        delaySlider.setMessage(Text.literal("Delay(ms): " + feature.getDelay()));

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Disable In Containers: " + (feature.getDisableContainer() ? "ON" : "OFF")),
                        button -> {
                            feature.toggleContainer();
                            this.init();
                        }).dimensions(centerX - 125, centerY - 30, 150, 20).build());

    }
}
