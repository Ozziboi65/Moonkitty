package com.moonkitty.Gui;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.gui.DrawContext;

import java.util.function.Consumer;

import com.moonkitty.Feature;
import com.moonkitty.FeatureManager;
import com.moonkitty.Features.ChestEsp;

public class ColorPicker extends Screen {
    private final Screen parent;
    private final Consumer<Integer> onSelect;
    private int red;
    private int green;
    private int blue;
    private int alpha;
    private int currentColor = 0xFFFFFFFF;

    private final int PREVIEW_SIZE = 80;

    public ColorPicker(Screen parent) {
        this(parent, 0xFFFFFFFF, null);
    }

    public ColorPicker(Screen parent, int initialColor, Consumer<Integer> onSelect) {
        super(Text.literal("MoonKitty Color Picker"));
        this.parent = parent;
        this.onSelect = onSelect;
        this.currentColor = initialColor;
        this.alpha = (initialColor >> 24) & 0xFF;
    }

    public void closeMenu() {
        this.client.setScreen(parent);
    }

    private int toArgb(int r, int g, int b, int a) {
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int previewX1 = centerX - PREVIEW_SIZE / 2;
        int previewX2 = centerX + PREVIEW_SIZE / 2;
        int previewY1 = (centerY - PREVIEW_SIZE / 2) + 120;
        int previewY2 = (centerY + PREVIEW_SIZE / 2) + 120;

        context.fill(previewX1, previewY1, previewX2, previewY2, toArgb(red, green, blue, 255));

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.red = (this.currentColor >> 16) & 0xFF;
        this.green = (this.currentColor >> 8) & 0xFF;
        this.blue = this.currentColor & 0xFF;
        this.alpha = (this.currentColor >> 24) & 0xFF;

        SliderWidget colorRSlider = new SliderWidget(centerX - 100, centerY - 60, 200, 20,
                Text.literal("Red: "),
                this.red / 255.0) {

            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Red: " + (int) (this.value * 255)));
            }

            @Override
            protected void applyValue() {
                red = (int) (this.value * 255);
                int colorRGB = toArgb((int) red, (int) green, (int) blue, (int) alpha);
                currentColor = colorRGB;
            }
        };

        SliderWidget colorGSlider = new SliderWidget(centerX - 100, centerY - 30, 200, 20,
                Text.literal("Green: "),
                this.green / 255.0) {

            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Green: " + (int) (this.value * 255)));
            }

            @Override
            protected void applyValue() {
                green = (int) (this.value * 255);
                int colorRGB = toArgb((int) red, (int) green, (int) blue, (int) alpha);
                currentColor = colorRGB;
            }
        };

        SliderWidget colorBSlider = new SliderWidget(centerX - 100, centerY + 0, 200, 20,
                Text.literal("Blue: "),
                this.blue / 255.0) {

            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Blue: " + (int) (this.value * 255)));
            }

            @Override
            protected void applyValue() {
                blue = (int) (this.value * 255);
                int colorRGB = toArgb((int) red, (int) green, (int) blue, (int) alpha);
                currentColor = colorRGB;
            }
        };

        SliderWidget colorASlider = new SliderWidget(centerX - 100, centerY + 30, 200, 20,
                Text.literal("Alpha: "),
                this.alpha / 255.0) {

            @Override
            protected void updateMessage() {
                this.setMessage(Text.literal("Alpha: " + (int) (this.value * 255)));
            }

            @Override
            protected void applyValue() {
                alpha = (int) (this.value * 255);
                int colorRGB = toArgb((int) red, (int) green, (int) blue, (int) alpha);
                currentColor = colorRGB;
                this.setMessage(Text.literal("Alpha: " + alpha));

                this.setAlpha(1.0f);
            }
        };

        this.addDrawableChild(
                new TextWidget(
                        centerX - 150,
                        centerY - 120,
                        325, 20,
                        Text.literal("Choose A Color."),
                        this.textRenderer));

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Apply"),
                        button -> {
                            if (this.onSelect != null)
                                this.onSelect.accept(this.currentColor);
                            this.client.setScreen(parent);
                        }).dimensions(centerX - 100, centerY - 90, 200, 20).build());

        this.addDrawableChild(colorRSlider);
        colorRSlider.setMessage(Text.literal("Red: " + this.red));
        this.addDrawableChild(colorGSlider);
        colorGSlider.setMessage(Text.literal("Green: " + this.green));
        this.addDrawableChild(colorBSlider);
        colorBSlider.setMessage(Text.literal("Blue: " + this.blue));
        this.addDrawableChild(colorASlider);
        colorASlider.setMessage(Text.literal("Alpha: " + this.alpha));

    }

    @Override
    public void removed() {
        if (this.onSelect != null)
            this.onSelect.accept(this.currentColor);
        super.removed();
    }
}
