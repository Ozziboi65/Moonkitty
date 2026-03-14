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
import com.moonkitty.Features.Visuals.PlayerAura;
import java.util.ArrayList;
import java.util.List;

public class PlayerAuraMenu extends Screen {
    private final Screen parent;
    private TextFieldWidget particleIdentifier;

    public PlayerAuraMenu(Screen parent) {
        super(Text.literal("MoonKitty PlayerAura Menu"));
        this.parent = parent;
    }

    public void closeMenu() {
        this.client.setScreen(parent);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        PlayerAura feature = FeatureManager.INSTANCE.getPlayerAura();

        super.init();

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal(feature.name + ": " + (feature.isEnabled() ? "ON" : "OFF")),
                        button -> {
                            feature.toggle();
                            this.init();
                        }).dimensions(centerX - 100, centerY - 90, 200, 20).build());

        this.addDrawableChild(
                new TextWidget(
                        centerX - 250,
                        centerY - 60,
                        500, 20,
                        Text.literal("Identifiers can be found at: https://minecraft.fandom.com/wiki/Particles"),
                        this.textRenderer));

        TextFieldWidget particleIdentifier = new TextFieldWidget(
                this.textRenderer,
                centerX - 100,
                centerY - 30,
                200,
                20,
                Text.literal("Identifier of the particle"));

        this.addDrawableChild(particleIdentifier);
        this.setFocused(particleIdentifier);

        particleIdentifier.setChangedListener(value -> {
            feature.setIdentifier(value);
        });

    }

}
