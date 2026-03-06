package com.moonkitty.Features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.Feature;
import com.moonkitty.Features.Menu.EspMenu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import com.moonkitty.Gui.Menu;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

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

import com.moonkitty.Features.Menu.EspMenu;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;

public class esp extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public MinecraftClient McClient;

    private int range = 50;
    private World world;

    private boolean showHostile;
    private boolean showItem;

    public int color_player = 0xff00ff;

    public int color_hostile = 0xffffff;

    public int color_item = 0xfbff00;

    public esp() {
        this.name = "ESP";
        this.feature_id = 1;
        this.setEnabled(true);
    }

    public boolean getHostile() {
        return showHostile;
    }

    public boolean getItem() {
        return showItem;
    }

    @Override
    public void init() {
        this.McClient = MinecraftClient.getInstance();
        Menu menuObject = Menu.INSTANCE;

        menuObject.registerNewFeatureButton(
                ButtonWidget.builder(
                        Text.literal("ESP"),
                        btn -> {
                            MinecraftClient.getInstance().setScreen(new EspMenu(Menu.INSTANCE));
                        }).dimensions(100, Menu.INSTANCE.getNextY(), 200, 20).build());
    }

    public void setItem(boolean enabled) {
        if (this.showItem == enabled)
            return;
        this.showItem = enabled;
    }

    public void setHostile(boolean enabled) {
        if (this.showHostile == enabled)
            return;
        this.showHostile = enabled;
    }

    public void toggleHostile() {
        setHostile(!this.showHostile);
    }

    public void toggleItem() {
        setItem(!this.showItem);
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setPlayerColor(int color) {
        this.color_player = color;
    }

    public void setHostileColor(int color) {
        this.color_hostile = color;
    }
}