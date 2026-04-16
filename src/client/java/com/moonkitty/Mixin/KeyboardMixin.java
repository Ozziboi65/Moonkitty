package com.moonkitty.Mixin;

import com.moonkitty.keybind.Keybind;
import com.moonkitty.keybind.KeybindManager;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class KeyboardMixin {

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKey(long window, int action, KeyInput input, CallbackInfo ci) {
        if (action == 1 && MinecraftClient.getInstance().currentScreen == null) {
            for (Keybind k : KeybindManager.getKeybindList()) {
                if (k.getBind() == input.key()) {
                    k.getOwner().toggle();
                }
            }
        }
    }
}