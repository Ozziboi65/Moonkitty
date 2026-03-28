package com.moonkitty;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;

import java.io.IOException;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.Gui.Hud;
import com.moonkitty.Gui.Menu;
import com.moonkitty.Gui.ClickGui;
import com.moonkitty.Util.ConfigUtil;
import com.moonkitty.Util.FileIO;

import com.moonkitty.Features.esp;
import com.moonkitty.Feature;

import com.moonkitty.FeatureManager;
import com.moonkitty.bot.BotActions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

public class MoonkittyClient implements ClientModInitializer {
        public static final String MOD_ID = "moonkitty";
        public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
        public MinecraftClient mcClient;

        public static final KeyBinding.Category MOONKITTY_CATEGORY = KeyBinding.Category.create(
                        Identifier.of("moonkitty", "main"));

        public static final KeyBinding OPEN_GUI = KeyBindingHelper.registerKeyBinding(
                        new KeyBinding("key.moonkitty.open_gui", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_INSERT,
                                        MOONKITTY_CATEGORY));

        public static final KeyBinding TOGGLE_FREECAM = KeyBindingHelper.registerKeyBinding(
                        new KeyBinding("key.moonkitty.toggleFreecam", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G,
                                        MoonkittyClient.MOONKITTY_CATEGORY));

        public static final KeyBinding TOGGLE_BLINK = KeyBindingHelper.registerKeyBinding(
                        new KeyBinding("key.moonkitty.blink", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_Z,
                                        MoonkittyClient.MOONKITTY_CATEGORY));

        @Override
        public void onInitializeClient() {
                LOGGER.info("Meow :3 ");
                mcClient = MinecraftClient.getInstance();

                new Menu(null);

                if (mcClient == null) {
                        LOGGER.warn("Mc Instance is null, maybe not loaded? :(");
                        mcClient = MinecraftClient.getInstance();
                }

                FeatureManager.INSTANCE.Init();

                FileIO.ExtractFromJar("assets/moonkitty/gif/1.gif", "moonkitty/1.gif");

                ConfigUtil.init();

                Hud.init();

                ClientTickEvents.END_CLIENT_TICK.register(client -> {

                        if (OPEN_GUI.wasPressed()) {
                                LOGGER.info("Menu Key Triggered");

                                mcClient.setScreen(
                                                new ClickGui());
                        }

                        FeatureManager.INSTANCE.tick(client);
                });

        }

}