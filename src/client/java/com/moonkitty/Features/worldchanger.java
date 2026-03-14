package com.moonkitty.Features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.Feature;
import com.moonkitty.Gui.Menu;
import com.moonkitty.Features.Menu.worldchangerMenu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;

import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.ButtonWidget;

public class worldchanger extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public MinecraftClient McClient;

    public long time = 22000;
    private World world;

    public worldchanger() {
        this.name = "WorldChanger";
        this.feature_id = 5;
        this.setEnabled(true);
    }

    public long getTime() {
        return time;
    }

    @Override
    public void init() {
        Menu menuObject = Menu.INSTANCE;

        menuObject.registerNewFeatureButton(
                ButtonWidget.builder(
                        Text.literal("WorldChanger"),
                        btn -> {
                            MinecraftClient.getInstance().setScreen(new worldchangerMenu(Menu.INSTANCE));
                        }).dimensions(100, Menu.INSTANCE.getNextY(), 200, 20).build());
    }

    public void setTime(long Wantedtime) {
        this.time = Wantedtime;
    }
}