package com.moonkitty.Features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.Feature;

import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import com.moonkitty.Gui.Menu;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import com.moonkitty.Category;
import com.moonkitty.Feature;
import com.moonkitty.FeatureManager;
import com.moonkitty.NumberSetting;
import com.moonkitty.Features.esp;
import com.moonkitty.Features.fakeplayer;
import com.moonkitty.Features.companion;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;

public class BoatFly extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public MinecraftClient McClient;

    private NumberSetting fallSetting;
    private NumberSetting speedSetting;

    public BoatFly() {
        this.name = "boatfly";
        this.feature_id = 76;
        this.setCategory(Category.MOVEMENT);
        this.setEnabled(false);

        fallSetting = new NumberSetting("Fall (m/s)", 1.0, 0.0, 10.0, 0.1);
        addSetting(fallSetting);

        speedSetting = new NumberSetting("Speed (m/s)", 3.0, 1.0, 10.0, 0.3);
        addSetting(speedSetting);
    }

    public double speed = 1.0;

    public double fallSpeed = 0.05;

    public double targetY = Double.NaN;

    @Override
    public void init() {
        this.McClient = MinecraftClient.getInstance();
        Menu menuObject = Menu.INSTANCE;
    }

    @Override
    public void tick(MinecraftClient client) {
        fallSpeed = fallSetting.getValue().floatValue();
        speed = speedSetting.getValue().floatValue();
    }

}