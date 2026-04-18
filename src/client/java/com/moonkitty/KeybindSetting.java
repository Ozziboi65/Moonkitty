package com.moonkitty;

import com.moonkitty.keybind.Keybind;
import com.moonkitty.keybind.KeybindManager;
import com.moonkitty.keybind.keybindMenu;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

public class KeybindSetting extends ButtonSetting {

    private final Feature feature;

    public KeybindSetting(Feature feature) {
        super("Bind", () -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            mc.setScreen(new keybindMenu(mc.currentScreen, feature));
        });
        this.feature = feature;
    }

    @Override
    public String getName() {
        return "Bind";
    }

    public String getButtonLabel() {
        for (Keybind k : KeybindManager.getKeybindList()) {
            if (k.getOwner() == feature) {
                int code = k.getBind();
                String name = GLFW.glfwGetKeyName(code, 0);
                if (name != null && !name.isEmpty())
                    return "Bind: [" + name.toUpperCase() + "]";
                return "Bind: [K" + code + "]";
            }
        }
        return "Bind: [-]";
    }

    @Override
    public String getDisplayValue() {
        return "";
    }

    public int getKeycode() {
        for (Keybind k : KeybindManager.getKeybindList()) {
            if (k.getOwner() == feature)
                return k.getBind();
        }
        return -1;
    }

    public void setKeycode(int keyCode) {
        for (Keybind k : KeybindManager.getKeybindList()) {
            if (k.getOwner() == feature) {
                k.setBind(keyCode);
                return;
            }
        }
        KeybindManager.registerKeybind(new Keybind(feature, keyCode));
    }
}
