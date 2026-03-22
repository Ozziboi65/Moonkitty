package com.moonkitty.Features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.Feature;
import com.moonkitty.Mixin.CameraAccessor;
import com.moonkitty.Mixin.FreecamMixin;
import net.minecraft.client.render.Camera;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.minecraft.client.render.Camera;

import net.minecraft.client.util.InputUtil;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import org.lwjgl.glfw.GLFW;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.MinecraftClient;
import com.moonkitty.MoonkittyClient;
import com.moonkitty.Features.Menu.freecamMenu;
import com.moonkitty.Gui.Menu;

import net.minecraft.util.Identifier;

import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.ButtonWidget;

public class freecam extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public MinecraftClient McClient;

    private World world;

    public double Freecam_yaw;

    public float speed = 0.5f;

    public Vec3d Freecam_position = Vec3d.ZERO;
    public Vec3d Freecam_orginal_pos = Vec3d.ZERO;

    public boolean initialized = false;

    private boolean optimise = true;

    @Override
    protected void onEnable() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null && client.gameRenderer != null) {
            Camera camera = client.gameRenderer.getCamera();
            this.Freecam_position = camera.getCameraPos();
        }
    }

    public void setOptimise(boolean enabled) {
        if (this.optimise == enabled)
            return;
        this.optimise = enabled;
    }

    public void toggleOptimise() {
        setOptimise(!this.optimise);
    }

    public boolean GetOptimise() {
        return optimise;
    }

    public void setSpeed(float Gotspeed) {
        speed = Gotspeed;
    }

    public float GetSpeed() {
        return speed;
    }

    public freecam() {
        this.name = "Freecam";
        this.feature_id = 2;
        this.setEnabled(false);
    }

    @Override
    public void init() {
        Menu menuObject = Menu.INSTANCE;

        menuObject.registerNewFeatureButton(
                ButtonWidget.builder(
                        Text.literal("Freecam"),
                        btn -> {
                            MinecraftClient.getInstance().setScreen(new freecamMenu(Menu.INSTANCE));
                        }).dimensions(100, Menu.INSTANCE.getNextY(), 200, 20).build());
    }

    @Override
    public void tick(MinecraftClient client) {
        if (MoonkittyClient.TOGGLE_FREECAM.wasPressed()) {
            LOGGER.info("Toggle Free cam bind pressed");

            client.inGameHud.getChatHud().addMessage(
                    Text.literal("[MOONKITTY]Freecam Toggled With Keybind!"));

            this.toggle();
        }
    }

}