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

public class freecamMenu extends Screen {
    private final Screen parent;

    public freecamMenu(Screen parent) {
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

        freecam feature = FeatureManager.INSTANCE.getFreecamFeature();

        SliderWidget speedSlider = new SliderWidget(centerX - 100, centerY - 30, 200, 20, Text.literal("Speed: "),
                feature.GetSpeed()) {

            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal(String.format("Speed: %.2f", this.value)));
            }

            @Override
            protected void applyValue() {
                feature.setSpeed((float) this.value);
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
                        Text.literal("Dont Cull Caves: " + (feature.GetOptimise() ? "ON" : "OFF")),
                        button -> {
                            feature.toggleOptimise();
                            this.init();
                        }).dimensions(centerX - 100, centerY - 60, 200, 20).build());

        this.addDrawableChild(speedSlider);
        speedSlider.setMessage(Text.literal(String.format("Speed: %.2f", feature.GetSpeed())));
    }
}
