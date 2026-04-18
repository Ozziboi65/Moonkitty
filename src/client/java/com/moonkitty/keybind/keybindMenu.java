package com.moonkitty.keybind;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import com.moonkitty.Feature;

import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;

public class keybindMenu extends Screen {

    private final Screen parent;
    private final Feature feature;
    private int pendingKey = -1;

    public keybindMenu(Screen parent, Feature feature) {
        super(Text.literal("MoonKitty Keybind"));
        this.parent = parent;
        this.feature = feature;
        for (Keybind k : KeybindManager.getKeybindList()) {
            if (k.getOwner() == feature) {
                this.pendingKey = k.getBind();
                break;
            }
        }
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int cy = this.height / 2;

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Apply"), btn -> {
            applyBind();
            this.client.setScreen(parent);
        }).dimensions(cx - 110, cy + 20, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Cancel"), btn -> {
            this.client.setScreen(parent);
        }).dimensions(cx + 10, cy + 20, 100, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Remove Bind"), btn -> {
            KeybindManager.getKeybindList().removeIf(k -> k.getOwner() == feature);
            pendingKey = -1;
        }).dimensions(cx - 55, cy + 50, 110, 20).build());
    }

    private void applyBind() {
        if (pendingKey < 0)
            return;
        for (Keybind k : KeybindManager.getKeybindList()) {
            if (k.getOwner() == feature) {
                k.setBind(pendingKey);
                return;
            }
        }
        KeybindManager.registerKeybind(new Keybind(feature, pendingKey));
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int keyCode = input.key();
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.client.setScreen(parent);
            return true;
        }
        this.pendingKey = keyCode;
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int cx = this.width / 2;
        int cy = this.height / 2;

        String featureName = feature.name != null ? feature.name : feature.getClass().getSimpleName();
        context.drawCenteredTextWithShadow(textRenderer, "Keybind: " + featureName, cx, cy - 50, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, "Press a key to bind...", cx, cy - 20, 0xFFAAAAAA);

        String keyText = pendingKey >= 0 ? getKeyName(pendingKey) : "None";
        context.drawCenteredTextWithShadow(textRenderer, "Current: " + keyText, cx, cy, 0xFFFFFFFF);
    }

    public static String getKeyName(int keyCode) {
        String name = GLFW.glfwGetKeyName(keyCode, 0);
        if (name != null && !name.isEmpty())
            return name.toUpperCase();
        return switch (keyCode) {
            case GLFW.GLFW_KEY_SPACE -> "SPACE";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "LSHIFT";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RSHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "LCTRL";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "RCTRL";
            case GLFW.GLFW_KEY_LEFT_ALT -> "LALT";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "RALT";
            case GLFW.GLFW_KEY_TAB -> "TAB";
            case GLFW.GLFW_KEY_CAPS_LOCK -> "CAPS";
            case GLFW.GLFW_KEY_ENTER -> "ENTER";
            case GLFW.GLFW_KEY_BACKSPACE -> "BKSP";
            case GLFW.GLFW_KEY_DELETE -> "DEL";
            case GLFW.GLFW_KEY_INSERT -> "INS";
            case GLFW.GLFW_KEY_HOME -> "HOME";
            case GLFW.GLFW_KEY_END -> "END";
            case GLFW.GLFW_KEY_PAGE_UP -> "PGUP";
            case GLFW.GLFW_KEY_PAGE_DOWN -> "PGDN";
            case GLFW.GLFW_KEY_UP -> "UP";
            case GLFW.GLFW_KEY_DOWN -> "DOWN";
            case GLFW.GLFW_KEY_LEFT -> "LEFT";
            case GLFW.GLFW_KEY_RIGHT -> "RIGHT";
            default -> "K" + keyCode;
        };
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
