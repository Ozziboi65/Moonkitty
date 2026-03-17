package com.moonkitty.Features.Menu;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;

import com.moonkitty.Feature;
import com.moonkitty.FeatureManager;
import com.moonkitty.Features.Combat.StrafeAura;
import com.moonkitty.Features.Visuals.PlayerAura;
import java.util.ArrayList;
import java.util.List;

public class StrafeAuraMenu extends Screen {
    private final Screen parent;
    private TextFieldWidget particleIdentifier;

    public StrafeAuraMenu(Screen parent) {
        super(Text.literal("MoonKitty Menu"));
        this.parent = parent;
    }

    public void closeMenu() {
        this.client.setScreen(parent);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        final int MIN_MAX_DIST = 0;
        final int MAX_MAX_DIST = 10;

        final int MIN_TARGET_DIST = 0;
        final int MAX_TARGET_DIST = 10;

        StrafeAura feature = FeatureManager.INSTANCE.getStrafeAuraFeature();

        super.init();

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal(feature.name + ": " + (feature.isEnabled() ? "ON" : "OFF")),
                        button -> {
                            feature.toggle();
                            this.init();
                        }).dimensions(centerX - 100, centerY - 90, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Cancel External Velocity: " + (feature.getCancelVelocity() ? "ON" : "OFF")),
                        button -> {
                            feature.toggleancelVelocity();
                            this.init();
                        }).dimensions(centerX - 100, centerY, 200, 20).build());

        Text descriptionText = Text.literal("Strafe Around Target, Be Careful Might Cause FallDamage");
        int textWidth = this.textRenderer.getWidth(descriptionText);

        this.addDrawableChild(
                new TextWidget(
                        (this.width - textWidth) / 2,
                        centerY - 130,
                        textWidth, 20,
                        descriptionText,
                        this.textRenderer));

        double initialNorm2 = (feature.getMaxDist() - MIN_MAX_DIST) / (double) (MAX_MAX_DIST - MIN_MAX_DIST);
        if (initialNorm2 < 0.0)
            initialNorm2 = 0.0;
        if (initialNorm2 > 1.0)
            initialNorm2 = 1.0;

        SliderWidget radiusSlider = new SliderWidget(centerX - 100, centerY - 60, 200, 20,
                Text.literal("Circle Radius: "),
                initialNorm2) {
            @Override
            protected void updateMessage() {
                float display = (float) Math.round(MIN_MAX_DIST + this.value * (MAX_MAX_DIST - MIN_MAX_DIST));
                this.setMessage(Text.literal("Circle Radius: " + display));
            }

            @Override
            protected void applyValue() {
                float newValue = (float) Math.round(MIN_MAX_DIST + this.value * (MAX_MAX_DIST - MIN_MAX_DIST));
                feature.setMaxDist(newValue);
            }
        };

        this.addDrawableChild(radiusSlider);

        float Display1 = feature.getMaxDist();
        radiusSlider.setMessage(Text.literal("Circle Radius: " + Display1));

        double initialNorm3 = (feature.getTargetDist() - MIN_TARGET_DIST)
                / (double) (MAX_TARGET_DIST - MIN_TARGET_DIST);
        if (initialNorm3 < 0.0)
            initialNorm3 = 0.0;
        if (initialNorm3 > 1.0)
            initialNorm3 = 1.0;

        SliderWidget distSlider = new SliderWidget(centerX - 100, centerY - 30, 200, 20,
                Text.literal("Target Distance: "),
                initialNorm3) {
            @Override
            protected void updateMessage() {
                float display = (float) Math.round(MIN_TARGET_DIST + this.value * (MAX_TARGET_DIST - MIN_TARGET_DIST));
                this.setMessage(Text.literal("Target Distance: " + display));
            }

            @Override
            protected void applyValue() {
                float newValue = (float) Math.round(MIN_TARGET_DIST + this.value * (MAX_TARGET_DIST - MIN_TARGET_DIST));
                feature.setTargetDist(newValue);
            }
        };

        this.addDrawableChild(distSlider);

        float display2 = feature.getTargetDist();
        distSlider.setMessage(Text.literal("Target Distance: " + display2));

    }

}
