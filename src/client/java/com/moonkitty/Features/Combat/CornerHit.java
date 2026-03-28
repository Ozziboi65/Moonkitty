package com.moonkitty.Features.Combat;

import com.moonkitty.BooleanSetting;
import com.moonkitty.Feature;
import com.moonkitty.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;

import com.moonkitty.Category;

public class CornerHit extends Feature {
    public CornerHit() {
        this.name = "CornerHit";
        this.feature_id = 353;
        this.setCategory(Category.COMBAT);
        this.setEnabled(false);
    }

    @Override
    public void tick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null)
            return;

        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {

            if (player == client.player) {
                continue;
            }

            double dx = client.player.getX() - player.getX();
            double dy = client.player.getY() - player.getY();
            double dz = client.player.getZ() - player.getZ();

            double distSq = dx * dx + dy * dy + dz * dz;

            // if (distSq > reach * reach) {
            // continue;
            // }
        }
    }
}
