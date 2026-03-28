package com.moonkitty.Features;

import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.moonkitty.ButtonSetting;
import com.moonkitty.Category;
import com.moonkitty.Feature;
import com.moonkitty.Gui.Menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.ButtonWidget;

public class fakeplayer extends Feature {

    public fakeplayer() {
        this.name = "FakePlayer";
        this.feature_id = 88;
        this.setCategory(Category.MISC);

        addSetting(new ButtonSetting("Spawn", () -> {
            spawnPlayer();
        }));

    }

    @Override
    public void init() {
        Menu menuObject = Menu.INSTANCE;

    }

    public void spawnPlayer() {
        MinecraftClient client = MinecraftClient.getInstance();

        ClientWorld world = client.world;

        if (world == null)
            return;

        GameProfile profile = new GameProfile(UUID.randomUUID(), "Femboy <3");

        OtherClientPlayerEntity fakePlayer = new OtherClientPlayerEntity(world, profile);

        Vec3d pos = new Vec3d(
                client.player.getX(),
                client.player.getY(),
                client.player.getZ());

        OtherClientPlayerEntity fake = new OtherClientPlayerEntity(world, profile);

        fake.refreshPositionAndAngles(
                pos.x,
                pos.y,
                pos.z,
                client.player.getYaw(),
                client.player.getPitch());

        world.addEntity(fake);

    }
}