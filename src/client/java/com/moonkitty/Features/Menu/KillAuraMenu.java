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
import com.moonkitty.Features.Combat.KillAura;

public class KillAuraMenu extends Screen {
    private final Screen parent;

    public KillAuraMenu(Screen parent) {
        super(Text.literal("MoonKitty KillAura Menu"));
        this.parent = parent;
    }

    public void closeMenu() {
        this.client.setScreen(parent);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        KillAura feature = FeatureManager.INSTANCE.getKillAuraFeature();

        final int MIN_DELAY = 0;
        final int MAX_DELAY = 1000;

        final float MIN_REACH = 0.0f;
        final float MAX_REACH = 12.0f;

        double initialNorm = (feature.getReach() - MIN_REACH) / (double) (MAX_REACH - MIN_REACH);
        if (initialNorm < 0.0)
            initialNorm = 0.0;
        if (initialNorm > 1.0)
            initialNorm = 1.0;

        SliderWidget reachSlider = new SliderWidget(centerX - 100, centerY - 30, 200, 20, Text.literal("Reach: "),
                initialNorm) {

            @Override
            protected void updateMessage() {
                double reach = MIN_REACH + this.value * (MAX_REACH - MIN_REACH);
                this.setMessage(Text.literal(String.format("Reach: %.2f", reach)));
            }

            @Override
            protected void applyValue() {
                float newReach = (float) (MIN_REACH + this.value * (MAX_REACH - MIN_REACH));
                feature.setReach(newReach);
            }
        };

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Swing Hand" + ": " + (feature.isSwing() ? "ON" : "OFF")),
                        button -> {
                            feature.toggleSwing();
                            this.init();
                        }).dimensions(centerX - 100, centerY, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal(feature.name + ": " + (feature.isEnabled() ? "ON" : "OFF")),
                        button -> {
                            feature.toggle();
                            this.init();
                        }).dimensions(centerX - 100, centerY - 90, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Check If Visible" + ": " + (feature.isVis() ? "ON" : "OFF")),
                        button -> {
                            feature.toggleVis();
                            this.init();
                        }).dimensions(centerX - 100, centerY + 30, 200, 20).build());

        this.addDrawableChild(reachSlider);

        reachSlider.setMessage(Text.literal(String.format("Reach: %.2f", feature.getReach())));

        double initialNorm2 = (feature.getDelayMs() - MIN_DELAY) / (double) (MAX_DELAY - MIN_DELAY);
        if (initialNorm2 < 0.0)
            initialNorm2 = 0.0;
        if (initialNorm2 > 1.0)
            initialNorm2 = 1.0;

        SliderWidget delaySlider = new SliderWidget(centerX - 100, centerY - 60, 200, 20, Text.literal("Delay(ms): "),
                initialNorm2) {
            @Override
            protected void updateMessage() {
                int display = (int) Math.round(MIN_DELAY + this.value * (MAX_DELAY - MIN_DELAY));
                this.setMessage(Text.literal("Delay(ms): " + display));
            }

            @Override
            protected void applyValue() {
                int newValue = (int) Math.round(MIN_DELAY + this.value * (MAX_DELAY - MIN_DELAY));
                feature.setDelayMs(newValue);
            }
        };

        this.addDrawableChild(delaySlider);

        int delayDisplay = feature.getDelayMs();
        delaySlider.setMessage(Text.literal("Delay(ms): " + delayDisplay));

    }
}
