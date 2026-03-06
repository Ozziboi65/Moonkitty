package com.moonkitty.Mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(net.minecraft.entity.Entity.class)
public class ClientMixin {

    @Inject(at = @At("HEAD"), method = "isGlowing", cancellable = true)
    private void isGlowing(CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (!((Object) this instanceof net.minecraft.entity.player.PlayerEntity))
            return;
        if (client.player == null)
            return;
        if ((Object) this == client.player)
            return;

        cir.setReturnValue(true);
    }
}