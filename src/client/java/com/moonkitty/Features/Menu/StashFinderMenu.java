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
import com.moonkitty.Features.StashFinder;

public class StashFinderMenu extends Screen {
    private final Screen parent;

    public StashFinderMenu(Screen parent) {
        super(Text.literal("MoonKitty StashFinderMenu"));
        this.parent = parent;
    }

    public void closeMenu() {
        this.client.setScreen(parent);
    }

    final int MIN_BLOCK = 1;
    final int MAX_BLOCK = 100;

    final int MIN_VOLUME = 0;
    final int MAX_VOLUME = 400;

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        StashFinder feature = FeatureManager.INSTANCE.getStashFinderFeature();

        double initialNorm = (feature.minBlockEntityCount - MIN_BLOCK) / (double) (MAX_BLOCK - MIN_BLOCK);
        if (initialNorm < 0.0)
            initialNorm = 0.0;
        if (initialNorm > 1.0)
            initialNorm = 1.0;

        double initialNorm2 = (feature.detectSoundVolume - MIN_VOLUME) / (double) (MAX_VOLUME - MIN_VOLUME);
        if (initialNorm2 < 0.0)
            initialNorm2 = 0.0;
        if (initialNorm2 > 1.0)
            initialNorm2 = 1.0;

        SliderWidget minBlockSlider = new SliderWidget(centerX - 100, centerY - 60, 200, 20,
                Text.literal("Minimium BlockEntities: "),
                initialNorm) {
            @Override
            protected void updateMessage() {
                int display = (int) Math.round(MIN_BLOCK + this.value * (MAX_BLOCK - MIN_BLOCK));
                this.setMessage(Text.literal("Minimium BlockEntities: " + display));
            }

            @Override
            protected void applyValue() {
                int newValue = (int) Math.round(MIN_BLOCK + this.value * (MAX_BLOCK - MIN_BLOCK));
                feature.minBlockEntityCount = newValue;
            }
        };

        SliderWidget volumeSlider = new SliderWidget(centerX - 100, centerY, 200, 20,
                Text.literal("Detect Sound Volume: "),
                initialNorm2) {
            @Override
            protected void updateMessage() {
                int display = (int) Math.round(MIN_VOLUME + this.value * (MAX_VOLUME - MIN_VOLUME));
                this.setMessage(Text.literal("Detect Sound Volume: " + display));
            }

            @Override
            protected void applyValue() {
                int newValue = (int) Math.round(MIN_VOLUME + this.value * (MAX_VOLUME - MIN_VOLUME));
                feature.detectSoundVolume = newValue;
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
                        Text.literal("Clear Cache"),
                        button -> {
                            feature.clearAllCache();
                            this.init();
                        }).dimensions(centerX - 100, centerY - 30, 200, 20).build());

        this.addDrawableChild(minBlockSlider);
        minBlockSlider.setMessage(Text.literal("Minimium BlockEntities: " + feature.minBlockEntityCount));

        this.addDrawableChild(volumeSlider);
        volumeSlider.setMessage(Text.literal("Detect Sound Volume: " + feature.detectSoundVolume));

    }
}
