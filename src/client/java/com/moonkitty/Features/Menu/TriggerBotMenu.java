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
import com.moonkitty.Features.esp;
import com.moonkitty.Features.freecam;
import com.moonkitty.Features.triggerbot;

public class TriggerBotMenu extends Screen {
    private final Screen parent;

    public TriggerBotMenu(Screen parent) {
        super(Text.literal("MoonKitty Freecam Menu"));
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

        triggerbot feature = FeatureManager.INSTANCE.getTriggerbotFeature();

        double initialNorm = (feature.getAttackDelayMs() - MIN_DELAY) / (double) (MAX_DELAY - MIN_DELAY);
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
                feature.setAttackDelayMs(newValue);
            }
        };

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal(feature.name + ": " + (feature.isEnabled() ? "ON" : "OFF")),
                        button -> {
                            feature.toggle();
                            this.init();
                        }).dimensions(centerX - 100, centerY - 90, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Simulate Mouse Click: " + (feature.getSimClick() ? "ON" : "OFF")),
                        button -> {
                            feature.toggleSimClick();
                            this.init();
                        }).dimensions(centerX - 100, centerY - 30, 200, 20).build());

        this.addDrawableChild(delaySlider);
        delaySlider.setMessage(Text.literal("Delay(ms): " + feature.getAttackDelayMs()));
    }
}
