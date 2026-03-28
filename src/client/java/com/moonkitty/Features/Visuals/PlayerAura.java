package com.moonkitty.Features.Visuals;

import com.moonkitty.Category;
import com.moonkitty.Feature;
import com.moonkitty.Gui.Menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;

import java.util.Random;

import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import com.moonkitty.Gui.Menu;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class PlayerAura extends Feature {
    MinecraftClient client = MinecraftClient.getInstance();
    Random random = new Random();

    String identifier = "minecraft:heart";

    public PlayerAura() {
        this.name = "Particle Aura";
        this.feature_id = 76;
        this.setCategory(Category.RENDER);
        this.setEnabled(true);
    }

    public void setIdentifier(String newidentifier) {
        identifier = newidentifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void init() {
        Menu menuObject = Menu.INSTANCE;

    }

    @Override
    public void tick(MinecraftClient client) {
        if (client.player == null || client.world == null)
            return;

        if (!this.isEnabled())
            return;

        Identifier id = Identifier.tryParse(identifier);
        if (id == null)
            return;

        if (!Registries.PARTICLE_TYPE.containsId(id))
            return;

        ParticleType<?> particle = Registries.PARTICLE_TYPE.get(id);

        client.world.addParticleClient((ParticleEffect) particle,
                client.player.getX() + (random.nextDouble() - 0.5) * 2,
                client.player.getY() + (random.nextDouble() - 0.5) * 2,
                client.player.getZ() + (random.nextDouble() - 0.5) * 2,
                0, 0, 0);

    }

}