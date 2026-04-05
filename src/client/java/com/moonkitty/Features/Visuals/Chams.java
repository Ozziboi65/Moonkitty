package com.moonkitty.Features.Visuals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.BooleanSetting;
import com.moonkitty.ButtonSetting;
import com.moonkitty.Category;
import com.moonkitty.Feature;
import com.moonkitty.NumberSetting;
import com.moonkitty.Gui.ColorPicker;

import net.minecraft.client.MinecraftClient;

public class Chams extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public MinecraftClient client;

    public int colorPlayer = 0x28FF83FF;

    public boolean isRenderPlayerEnabled() {
        return playerSetting.getValue();
    }

    private BooleanSetting playerSetting;

    public Chams() {
        this.name = "Chams";
        this.feature_id = 1;
        this.setCategory(Category.RENDER);
        this.setEnabled(true);

        playerSetting = new BooleanSetting("Players", true);
        addSetting(playerSetting);
    }

    @Override
    public void init() {
        this.client = MinecraftClient.getInstance();

        addSetting(new ButtonSetting("Player ColorPicker", () -> {
            int current = this.colorPlayer;
            client.setScreen(new ColorPicker(client.currentScreen, current, c -> {
                this.colorPlayer = c;
            }));
        }));
    }
}