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
import com.moonkitty.Features.worldchanger;

public class worldchangerMenu extends Screen {

    private final Screen parent;

    public worldchangerMenu(Screen parent) {
        super(Text.literal("MoonKitty Tracer Menu"));
        this.parent = parent;
    }

    public void closeMenu() {
        this.client.setScreen(parent);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        worldchanger feature = FeatureManager.INSTANCE.getWorldchangerFeature();

        final long MAX_TIME = 24000L;
        double initial = Math.floorMod(feature.getTime(), MAX_TIME) / (double) MAX_TIME;

        SliderWidget timeSlider = new SliderWidget(centerX - 100, centerY - 70, 200, 20, Text.literal("Time: "),
                initial) {

            @Override
            protected void updateMessage() {
                long ticks = (long) (this.value * MAX_TIME);
                this.setMessage(Text.literal(String.format("Time: %d", ticks)));
            }

            @Override
            protected void applyValue() {
                long ticks = (long) (this.value * MAX_TIME);
                feature.setTime(ticks);
            }
        };

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal(feature.name + ": " + (feature.isEnabled() ? "ON" : "OFF")),
                        button -> {
                            feature.toggle();
                            this.init();
                        }).dimensions(centerX - 100, centerY - 90, 200, 20).build());

        this.addDrawableChild(timeSlider);
    }
}
