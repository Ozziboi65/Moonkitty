package com.moonkitty.Features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.Feature;
import com.moonkitty.Mixin.CameraAccessor;
import com.moonkitty.Mixin.FreecamMixin;
import com.moonkitty.Util.GifLoader;

import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.minecraft.client.render.Camera;

import net.minecraft.client.util.InputUtil;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

import org.lwjgl.glfw.GLFW;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.MinecraftClient;
import com.moonkitty.MoonkittyClient;
import com.moonkitty.Features.Menu.TriggerBotMenu;
import com.moonkitty.Features.Menu.companionMenu;
import com.moonkitty.Gui.Menu;

import net.minecraft.util.Identifier;
import java.io.PrintStream;
import net.minecraft.client.render.*;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.DrawContext;

import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.NativeImage;

import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.MinecraftClient;

import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.ButtonWidget;

import com.moonkitty.Util.FileIO;

public class companion extends Feature {
    private NativeImageBackedTexture[] textures;
    private int Frame_count;
    private int tickCounter = 0;
    private int currentFrame;
    private int speed = 3; // how many ticks to show a frame for

    public String fileName = "1.gif";

    int x = 100;
    int y = 100;
    int width = 64;
    int height = 64;

    public void setX(int value) {
        x = value;
    }

    public void setY(int value) {
        y = value;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int value) {
        speed = value;
    }

    public companion() {
        this.name = "Companion";
        this.feature_id = 6;
        this.setEnabled(false);
    }

    private NativeImageBackedTexture convertFrame(BufferedImage img, int index) {
        MinecraftClient client = MinecraftClient.getInstance();
        int width = img.getWidth();
        int height = img.getHeight();

        NativeImage nativeImage = new NativeImage(width, height, false);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = img.getRGB(x, y);

                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;

                nativeImage.setColor(x, y, (a << 24) | (b << 16) | (g << 8) | r);
            }
        }

        NativeImageBackedTexture texture = new NativeImageBackedTexture(() -> "moonkitty:companion_frame_" + index,
                nativeImage);

        client.getTextureManager().registerTexture(
                Identifier.of("moonkitty:companion_frame_" + index),
                texture);

        return texture;
    }

    public void refresh() {
        loadGif();
    }

    private void loadGif() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (textures != null) {
            for (NativeImageBackedTexture texture : textures) {
                if (texture != null) {
                    texture.close();
                }
            }
        }

        try {
            InputStream inputGif = FileIO.InputStreamFromFile("moonkitty/" + fileName);

            if (inputGif == null) {
                return;
            }

            BufferedImage[] frames = GifLoader.loadGif(inputGif);

            textures = new NativeImageBackedTexture[frames.length];

            for (int i = 0; i < frames.length; i++) {
                textures[i] = convertFrame(frames[i], i);
            }

            Frame_count = textures.length;
            currentFrame = 0;
            tickCounter = 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() {
        Menu menuObject = Menu.INSTANCE;

        menuObject.registerNewFeatureButton(
                ButtonWidget.builder(
                        Text.literal("Companion"),
                        btn -> {
                            MinecraftClient.getInstance().setScreen(new companionMenu(Menu.INSTANCE));
                        }).dimensions(100, Menu.INSTANCE.getNextY(), 200, 20).build());

        HudRenderCallback.EVENT.register(
                (DrawContext drawContext, net.minecraft.client.render.RenderTickCounter tickDeltaManager) -> {
                    if (!this.isEnabled())
                        return;

                    if (textures == null || textures[currentFrame] == null)
                        return;

                    Identifier frameId = Identifier.of("moonkitty:companion_frame_" + currentFrame);

                    drawContext.drawTexture(
                            RenderPipelines.GUI_TEXTURED,
                            frameId,
                            x, y,
                            0.0f, 0.0f,
                            width, height,
                            width, height);
                });
    }

    @Override
    protected void onEnable() {
        loadGif();
    }

    @Override
    public void tick(MinecraftClient client) {
        if (textures == null)
            return;

        tickCounter++;

        if (tickCounter >= speed) {
            tickCounter = 0;
            currentFrame++;

        }

        if (currentFrame >= Frame_count) {
            currentFrame = 0;
        }

    }

}