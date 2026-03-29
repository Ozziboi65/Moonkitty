package com.moonkitty.Mixin;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.moonkitty.Features.FastUse;
import com.moonkitty.FeatureManager;

@Mixin(MinecraftClient.class)
public class FastUseMixin {
    @Shadow
    private int itemUseCooldown;

    @Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemEnabled(Lnet/minecraft/resource/featuretoggle/FeatureSet;)Z"))
    private void onDoItemUseHand(CallbackInfo ci, @Local ItemStack itemStack) {

        if (FeatureManager.INSTANCE.getFastUseFeature().isEnabled()) {

            if (FeatureManager.INSTANCE.getFastUseFeature().onlyBlock) {

                ItemStack itemHolding = FeatureManager.INSTANCE.getFastUseFeature().client.player.getMainHandStack();
                if (!(itemHolding.getItem() instanceof BlockItem)) {
                    return;
                }
                itemUseCooldown = FeatureManager.INSTANCE.getFastUseFeature().getSpeed();
            }

            if (!FeatureManager.INSTANCE.getFastUseFeature().onlyBlock) {
                itemUseCooldown = FeatureManager.INSTANCE.getFastUseFeature().getSpeed();
            }
        }
    }

}