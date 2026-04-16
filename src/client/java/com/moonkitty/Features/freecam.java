package com.moonkitty.Features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.BooleanSetting;
import com.moonkitty.Category;
import com.moonkitty.keybind.Keybind;
import com.moonkitty.keybind.KeybindManager;
import com.moonkitty.Feature;
import com.moonkitty.NumberSetting;
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
import net.minecraft.client.option.Perspective;
import net.minecraft.client.MinecraftClient;
import com.moonkitty.MoonkittyClient;
import com.moonkitty.Gui.Menu;

import net.minecraft.util.Identifier;

import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.ButtonWidget;

public class freecam extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public MinecraftClient client;

    private NumberSetting speedSetting;
    private BooleanSetting optimiseSetting;
    private BooleanSetting cancelPacketSetting;

    private World world;

    public double Freecam_yaw;

    public float speed = 0.5f;

    public Vec3d Freecam_position = Vec3d.ZERO;
    public Vec3d Freecam_orginal_pos = Vec3d.ZERO;

    public boolean initialized = false;
    private boolean optimise = true;
    public boolean cancelPackets = true;

    @Override
    public void onEnable() {
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
        this.setCategory(Category.MOVEMENT);
        this.setEnabled(false);

        speedSetting = new NumberSetting("Speed", 0.5, 0.1, 5.0, 0.1);
        optimiseSetting = new BooleanSetting("Optimise", true);
        cancelPacketSetting = new BooleanSetting("Cancel Packets", false);
        Keybind bind = new Keybind(this, GLFW.GLFW_KEY_G);
        KeybindManager.registerKeybind(bind);

        addSetting(speedSetting);
        addSetting(optimiseSetting);
        addSetting(cancelPacketSetting);
    }

    @Override
    public void init() {
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public void toggle() {
        setEnabled(!this.isEnabled());
        if (this.isEnabled()) {
            client.options.setPerspective(Perspective.THIRD_PERSON_BACK);
        }
        if (!this.isEnabled()) {
            client.options.setPerspective(Perspective.FIRST_PERSON);
        }
    }

    @Override
    public void tick(MinecraftClient client) {
        speed = speedSetting.getValue().floatValue();
        optimise = optimiseSetting.getValue();
        cancelPackets = cancelPacketSetting.getValue();
    }

}