package com.moonkitty.Features.Combat;

import com.moonkitty.Category;
import com.moonkitty.Feature;

import net.minecraft.client.MinecraftClient;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class CrystalAura extends Feature {

    private final int EXPLOSION_POWER = 6;
    private final float DIFF_MULTIPLIER = 1f;

    private float exposure;

    private MinecraftClient client;

    public CrystalAura() {
        this.name = "CrystalAura";
        this.setCategory(Category.COMBAT);
        this.feature_id = 45;
        this.setEnabled(false);
    }

    @Override
    public void tick(MinecraftClient client) {
        this.client = MinecraftClient.getInstance();

        if (!this.isEnabled())
            return;

        if (client.player == null || client.getNetworkHandler() == null || client.world == null)
            return;

    }

    void getDamage(float exposure, float distance) {

        Vec3d start = client.player.getEyePos();
        Vec3d end = client.player.getEyePos();



    }

}
