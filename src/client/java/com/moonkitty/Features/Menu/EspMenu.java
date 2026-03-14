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
import com.moonkitty.Features.esp;

import com.moonkitty.Gui.ColorPicker;
import com.moonkitty.Gui.Menu;

import java.util.function.Consumer;

public class EspMenu extends Screen {
    private final Screen parent;

    private int playerRed;
    private int playerGreen;
    private int playerBlue;

    public EspMenu(Screen parent) {
        super(Text.literal("MoonKitty ESP Menu"));
        this.parent = parent;
    }

    public void closeMenu() {
        this.client.setScreen(parent);
    }

    private int toArgb(int R, int G, int B) {
        return (255 << 24) | (R << 16) | (G << 8) | B;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        esp Espfeature = FeatureManager.INSTANCE.getEspFeature();

        int color = Espfeature.color_player;
        this.playerRed = (color >> 16) & 0xFF;
        this.playerGreen = (color >> 8) & 0xFF;
        this.playerBlue = color & 0xFF;

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Player Color"),
                        btn -> {
                            int current = Espfeature.color_player;
                            client.setScreen(new ColorPicker(this, current, c -> {
                                Espfeature.setPlayerColor(c);
                            }));
                        }).dimensions(centerX - 100, centerY - 30, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Hostile Color"),
                        btn -> {
                            int current = Espfeature.color_hostile;
                            client.setScreen(new ColorPicker(this, current, c -> {
                                Espfeature.color_hostile = c;
                            }));
                        }).dimensions(centerX - 100, centerY - 60, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Item Color"),
                        btn -> {
                            int current = Espfeature.color_item;
                            client.setScreen(new ColorPicker(this, current, c -> {
                                Espfeature.color_item = c;
                            }));
                        }).dimensions(centerX - 100, centerY, 200, 20).build());

        this.addDrawableChild(
                new TextWidget(
                        centerX - 150,
                        centerY - 120,
                        325, 20,
                        Text.literal("Draws an outline/box around chosen Entities"),
                        this.textRenderer));

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal(Espfeature.name + ": " + (Espfeature.isEnabled() ? "ON" : "OFF")),
                        button -> {
                            Espfeature.toggle();
                            this.init();
                        }).dimensions(centerX - 100, centerY - 90, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Draw Outline: " + (Espfeature.getOutline() ? "ON" : "OFF")),
                        button -> {
                            Espfeature.toggleOutline();
                            this.init();
                        }).dimensions(centerX - 100, centerY + 120, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Draw Box: " + (Espfeature.getBox() ? "ON" : "OFF")),
                        button -> {
                            Espfeature.toggleBox();
                            this.init();
                        }).dimensions(centerX - 100, centerY + 90, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Show Hostile: " + (Espfeature.getHostile() ? "ON" : "OFF")),
                        button -> {
                            Espfeature.toggleHostile();
                            this.init();
                        }).dimensions(centerX - 100, centerY + 30, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Show Items: " + (Espfeature.getItem() ? "ON" : "OFF")),
                        button -> {
                            Espfeature.toggleItem();
                            this.init();
                        }).dimensions(centerX - 100, centerY + 60, 200, 20).build());

    }
}
