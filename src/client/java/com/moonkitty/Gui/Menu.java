package com.moonkitty.Gui;

import java.util.ArrayList;

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
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.ScrollableWidget;
import java.util.ArrayList;
import java.util.List;

public class Menu extends Screen {
    public static Menu INSTANCE;
    private final Screen parent;

    private int nextButtonY = 20;

    int Xsize;
    int Ysize;

    static int screenWidth;
    static int screenHeight;

    static Boolean inited = false;

    private static final List<ButtonWidget> pendingButtons = new ArrayList<>();

    public static int bgColor = 0xCC0D0D1A;

    public Menu(Screen parent) {
        super(Text.literal("Menu"));
        this.parent = parent;
        INSTANCE = this;
    }

    public void registerNewFeatureButton(ButtonWidget button) {

        pendingButtons.add(button);
        nextButtonY += 25;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, bgColor);
    }

    @Override
    protected void init() {
        super.init();
        for (ButtonWidget button : pendingButtons) {
            this.addDrawableChild(button);
        }

    }

    public int getWidth() {
        Xsize = this.width / 2;
        return Xsize;
    }

    public int getHeight() {
        Ysize = this.height / 2;
        return Ysize;
    }

    public int getNextY() {
        return nextButtonY;
    }

}