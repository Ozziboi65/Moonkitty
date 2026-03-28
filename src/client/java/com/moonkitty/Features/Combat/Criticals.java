package com.moonkitty.Features.Combat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.Category;
import com.moonkitty.Feature;

import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import com.moonkitty.Gui.Menu;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import com.moonkitty.Feature;
import com.moonkitty.FeatureManager;
import com.moonkitty.MoonkittyClient;
import com.moonkitty.Features.esp;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;

import net.minecraft.client.MinecraftClient;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.client.render.DrawStyle;

import java.util.ArrayList;
import java.util.List;

public class Criticals extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public MinecraftClient McClient;

    public Double spoofY = 0.0625;

    public Criticals() {
        this.name = "Criticals";
        this.feature_id = 5758;
        this.setCategory(Category.COMBAT);
        this.setEnabled(false);
    }

    public void sendFakePacket(double offsetY) {
        if (McClient == null)
            McClient = MinecraftClient.getInstance();

        if (McClient.player == null || McClient.player.networkHandler == null)
            return;

        double x = McClient.player.getX();
        double y = McClient.player.getY();
        double z = McClient.player.getZ();

        McClient.player.networkHandler.sendPacket(
                new PlayerMoveC2SPacket.PositionAndOnGround(x, y + offsetY, z, false,
                        McClient.player.horizontalCollision));

    }

    public boolean canCrit() {
        if (McClient == null)
            McClient = MinecraftClient.getInstance();

        if (McClient.player == null)
            return false;

        return McClient.player.isOnGround()
                && !McClient.player.isSubmergedInWater()
                && !McClient.player.isInLava()
                && !McClient.player.isClimbing();
    }

    @Override
    public void init() {
        this.McClient = MinecraftClient.getInstance();
        Menu menuObject = Menu.INSTANCE;

    }

    @Override
    public void tick(MinecraftClient client) {

    }
}