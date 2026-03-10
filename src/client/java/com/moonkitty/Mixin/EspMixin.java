package com.moonkitty.Mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import com.moonkitty.Features.esp;
import com.moonkitty.FeatureManager;

@Mixin(net.minecraft.entity.Entity.class)
public class EspMixin {
    esp esp_feature = FeatureManager.INSTANCE.getEspFeature();

    @Inject(at = @At("HEAD"), method = "isGlowing", cancellable = true)

    private void isGlowing(CallbackInfoReturnable<Boolean> cir) {

        if (esp_feature.isEnabled() && esp_feature.getOutline()) {
            MinecraftClient client = MinecraftClient.getInstance();

            boolean isPlayer = (Object) this instanceof PlayerEntity;
            boolean isHostile = (Object) this instanceof HostileEntity;
            Boolean isItem = (Object) this instanceof ItemEntity;

            if (isPlayer) {
                cir.setReturnValue(true);
                return;
            }

            if (isHostile && esp_feature.getHostile()) {
                cir.setReturnValue(true);
                return;
            }

            if (isItem && esp_feature.getItem()) {
                cir.setReturnValue(true);
                return;
            }

            return;

        }

    }

    @Inject(at = @At("HEAD"), method = "getTeamColorValue", cancellable = true)
    private void getTeamColorValue(CallbackInfoReturnable<Integer> cir) {
        Entity entity = (Entity) (Object) this;

        if (entity instanceof PlayerEntity)
            cir.setReturnValue(esp_feature.color_player);

        if (entity instanceof HostileEntity)
            cir.setReturnValue(esp_feature.color_hostile);

        if (entity instanceof ItemEntity)
            cir.setReturnValue(esp_feature.color_item);

    }

}
