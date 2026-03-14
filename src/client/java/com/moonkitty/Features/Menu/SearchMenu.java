package com.moonkitty.Features.Menu;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;

import com.moonkitty.Feature;
import com.moonkitty.FeatureManager;
import com.moonkitty.Features.esp;
import com.moonkitty.Gui.ColorPicker;
import com.moonkitty.Features.Search;

public class SearchMenu extends Screen {
    private final Screen parent;

    public SearchMenu(Screen parent) {
        super(Text.literal("MoonKitty search Menu"));
        this.parent = parent;
    }

    public void closeMenu() {
        this.client.setScreen(parent);
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        Search feature = FeatureManager.INSTANCE.getSearchFeature();

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Color"),
                        btn -> {
                            int current = feature.color;
                            client.setScreen(new ColorPicker(this, current, c -> {
                                feature.color = c;
                            }));
                        }).dimensions(centerX - 100, centerY - 60, 200, 20).build());

        TextFieldWidget identifier = new TextFieldWidget(
                this.textRenderer,
                centerX - 100,
                centerY - 30,
                200,
                20,
                Text.literal("Identifier Of The Block"));

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal(feature.name + ": " + (feature.isEnabled() ? "ON" : "OFF")),
                        button -> {
                            feature.toggle();
                            this.init();
                        }).dimensions(centerX - 100, centerY - 90, 200, 20).build());

        this.addDrawableChild(identifier);
        this.setFocused(identifier);

        identifier.setChangedListener(value -> {
            feature.setTargetFromString(value);
        });

    }
}
