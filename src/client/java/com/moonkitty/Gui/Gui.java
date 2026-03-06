package com.moonkitty.Gui;

import com.moonkitty.Feature;
import com.moonkitty.FeatureManager;
import com.moonkitty.Features.esp;
import com.moonkitty.Features.Menu.freecamMenu;
import com.moonkitty.Features.Menu.EspMenu;
import com.moonkitty.Features.fakeplayer;
import com.moonkitty.Features.Menu.worldchangerMenu;
import com.moonkitty.Features.companion;
import com.moonkitty.Features.Menu.companionMenu;
import com.moonkitty.Features.Menu.BlinkMenu;
import com.moonkitty.Features.Menu.TriggerBotMenu;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.ScrollableWidget;

public class Gui extends Screen {

    private final Screen parent;

    public Gui(Screen parent) {
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

        int menu_height = this.height;
        int menu_width = this.width;

        esp feature = FeatureManager.INSTANCE.getEspFeature();

        fakeplayer fakeplayerfeature = FeatureManager.INSTANCE.getFakeplayerFeature();

        companion companionfeature = FeatureManager.INSTANCE.getCompanionFeature();

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("ESP"),
                        button -> {
                            this.client.setScreen(new EspMenu(this));
                        }).dimensions(centerX - 100, centerY - 89, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Freecam"),
                        button -> {
                            this.client.setScreen(new freecamMenu(this));
                        }).dimensions(centerX - 100, centerY - 40, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("TriggerBot"),
                        button -> {
                            this.client.setScreen(new TriggerBotMenu(this));
                        }).dimensions(centerX + 133, centerY - 40, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("worldChanger"),
                        button -> {
                            this.client.setScreen(new worldchangerMenu(this));
                        }).dimensions(centerX - 100, centerY, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("companion"),
                        button -> {
                            this.client.setScreen(new companionMenu(this));
                        }).dimensions(centerX + 133, centerY, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Blink"),
                        button -> {
                            this.client.setScreen(new BlinkMenu(this));
                        }).dimensions(centerX + 133, centerY + 40, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Fake Player"),
                        button -> {
                            fakeplayerfeature.spawnPlayer();
                        }).dimensions(centerX - 100, centerY + 40, 200, 20).build());

        this.addDrawableChild(
                ButtonWidget.builder(
                        Text.literal("Close"),
                        button -> this.closeMenu())

                        .dimensions(centerX - 100, centerY + 80, 200, 20)
                        .build());

    }

}