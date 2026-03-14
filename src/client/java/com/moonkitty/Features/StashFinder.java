package com.moonkitty.Features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.Feature;
import com.moonkitty.Features.Menu.EspMenu;

import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import com.moonkitty.Gui.Menu;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;

import com.moonkitty.Feature;
import com.moonkitty.FeatureManager;
import com.moonkitty.Features.esp;
import com.moonkitty.Features.Menu.freecamMenu;
import com.moonkitty.Features.Menu.EspMenu;
import com.moonkitty.Features.fakeplayer;
import com.moonkitty.Features.Menu.worldchangerMenu;
import com.moonkitty.Features.companion;
import com.moonkitty.Features.Menu.companionMenu;
import com.moonkitty.Features.Menu.BlinkMenu;
import com.moonkitty.Features.Menu.TriggerBotMenu;

import com.moonkitty.Features.Menu.EspMenu;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StashFinder extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public MinecraftClient McClient;

    private World world;

    private final Set<ChunkPos> scannedChunks = new HashSet<>();

    public StashFinder() {
        this.name = "StashFinder";
        this.feature_id = 647;
        this.setEnabled(true);
    }

    @Override
    public void init() {
        this.McClient = MinecraftClient.getInstance();
        Menu menuObject = Menu.INSTANCE;

        menuObject.registerNewFeatureButton(
                ButtonWidget.builder(
                        Text.literal("StashFinder"),
                        btn -> {
                            MinecraftClient.getInstance().setScreen(new EspMenu(Menu.INSTANCE));
                        }).dimensions(100, Menu.INSTANCE.getNextY(), 200, 20).build());
    }

    @Override
    public void tick(MinecraftClient client) {

        if (client.player == null || client.world == null)
            return;

        int renderDist = client.options.getClampedViewDistance();
        ChunkPos center = client.player.getChunkPos();

        for (int dx = -renderDist; dx <= renderDist; dx++) {
            for (int dz = -renderDist; dz <= renderDist; dz++) {
                WorldChunk chunk = client.world.getChunkManager()
                        .getWorldChunk(center.x + dx, center.z + dz);

                ChunkPos cp = new ChunkPos(center.x + dx, center.z + dz);

                if (scannedChunks.contains(cp))
                    continue;

                if (chunk == null)
                    continue;

                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (be instanceof ChestBlockEntity chest) {

                    }

                }

            }
        }

    }

}