package com.moonkitty.Mixin;

import com.llamalad7.mixinextras.sugar.Local;

import net.minecraft.item.BlockItem;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.moonkitty.Features.FastUse;
import com.moonkitty.Feature;
import com.moonkitty.FeatureManager;

@Mixin(MinecraftClient.class)
public class FastUseMixin {
    @Shadow
    private int itemUseCooldown;

    FastUse feature = FeatureManager.INSTANCE.getFastUseFeature();

    @Inject(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isItemEnabled(Lnet/minecraft/resource/featuretoggle/FeatureSet;)Z"))
    private void onDoItemUseHand(CallbackInfo ci, @Local ItemStack itemStack) {
        if (feature.isEnabled()) {
            ItemStack itemHolding = FeatureManager.INSTANCE.getFastUseFeature().client.player.getMainHandStack();

            if (feature.block) {
                if (!(itemHolding.getItem() instanceof BlockItem)) {
                    return;
                }
                itemUseCooldown = FeatureManager.INSTANCE.getFastUseFeature().getSpeed();
            } else if (feature.crystal) {
                if (!(itemHolding.getItem() instanceof EndCrystalItem)) {
                    return;
                }
                itemUseCooldown = FeatureManager.INSTANCE.getFastUseFeature().getSpeed();

            }
        }
    }

}