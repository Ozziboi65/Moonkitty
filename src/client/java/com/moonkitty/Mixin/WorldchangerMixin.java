package com.moonkitty.Mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.moonkitty.Features.worldchanger;
import com.moonkitty.FeatureManager;

import net.minecraft.world.World;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.world.ClientWorld.Properties;

@Mixin(ClientWorld.class)
public abstract class WorldchangerMixin {
    worldchanger feature = FeatureManager.INSTANCE.getWorldchangerFeature();

    @Inject(method = "setTime", at = @At("HEAD"), cancellable = true)

    private void writeTime(long time, long timeOfDay, boolean shouldTickTimeOfDay, CallbackInfo ci) {
        ClientWorld world = (ClientWorld) (Object) this;
        if (!feature.isEnabled())
            return;

        world.getLevelProperties().setTimeOfDay(feature.time);

        ci.cancel();
    }

}
