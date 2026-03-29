package com.moonkitty.Mixin;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.moonkitty.Features.InventoryTweaks;
import com.moonkitty.FeatureManager;

@Mixin(HandledScreen.class)
public abstract class ChestScreenMixin extends Screen {

    @Shadow
    protected int x;
    @Shadow
    protected int y;
    @Shadow
    protected int backgroundWidth;
    @Shadow
    protected int backgroundHeight;

    protected ChestScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addButton(CallbackInfo ci) {
        if ((Object) this instanceof GenericContainerScreen) {
            if (FeatureManager.INSTANCE.getInventoryTweaksFeature().isEnabled()) {
                int buttonWidth = 80;
                int buttonHeight = 20;
                int buttonX = this.x + this.backgroundWidth - buttonWidth;
                int buttonY = this.y - buttonHeight - 4;
                this.addDrawableChild(ButtonWidget.builder(
                        Text.literal("Steal All"),
                        button -> InventoryTweaks.stealAll()).dimensions(buttonX, buttonY, buttonWidth, buttonHeight)
                        .build());

                int buttonX2 = this.x;
                int buttonY2 = buttonY;
                /*
                 * this.addDrawableChild(ButtonWidget.builder(
                 * Text.literal("Store All"),
                 * button -> InventoryTweaks.storeAll())
                 * .dimensions(buttonX2, buttonY2, buttonWidth, buttonHeight)
                 * .build());
                 * 
                 */
            }
        }
    }
}