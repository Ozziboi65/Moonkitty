package com.moonkitty.visuals.Particles;

import com.moonkitty.Feature;
import com.moonkitty.Category;

import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;

import java.util.Random;

public class ButterFlies extends Feature {
    Random random = new Random();

    private final int PARTICLE_SPAWN_COUNT = 20;

    public ButterFlies() {
        this.name = "ButterFlies";
        this.setCategory(Category.VISUAL);
        this.setEnabled(true);
    }

    public void tick(MinecraftClient client) {

        if (!isEnabled() || client.player == null || client.world == null)
            return;

        for (int i = 0; i < PARTICLE_SPAWN_COUNT; i++) {
            Vec3d pos = new Vec3d(
                    client.player.getX() + (random.nextDouble() - 0.5) * 12,
                    client.player.getY() + (random.nextDouble() - 0.5) * 12,
                    client.player.getZ() + (random.nextDouble() - 0.5) * 12);

            client.world.addParticleClient(
                    ParticleTypes.FIREFLY,
                    pos.x, pos.y, pos.z,
                    0, 0, 0);

        }

    }

}