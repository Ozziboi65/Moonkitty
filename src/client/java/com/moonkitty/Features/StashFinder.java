package com.moonkitty.Features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.Feature;
import com.moonkitty.Features.Menu.StashFinderMenu;

import net.minecraft.block.entity.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

import com.moonkitty.Gui.Menu;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;

import com.moonkitty.Features.StashFinderChunk;
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

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.gametest.v1.screenshot.TestScreenshotComparisonOptions;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.client.toast.AdvancementToast;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class StashFinder extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public MinecraftClient mcClient;

    private final Set<ChunkPos> scannedChunks = new HashSet<>();
    public List<BlockPos> tracerTargets = new ArrayList<>();

    public float detectSoundVolume = 67;
    public int minBlockEntityCount = 6;

    public StashFinder() {
        this.name = "StashFinder";
        this.feature_id = 647;
        this.setEnabled(true);
    }

    public void clearAllCache() {
        LOGGER.info("Clearing the stashfinder cache :3");
        scannedChunks.clear();
        tracerTargets.clear();
    }

    @Override
    public void init() {
        this.mcClient = MinecraftClient.getInstance();
        Menu menuObject = Menu.INSTANCE;

        menuObject.registerNewFeatureButton(
                ButtonWidget.builder(
                        Text.literal("StashFinder"),
                        btn -> {
                            MinecraftClient.getInstance().setScreen(new StashFinderMenu(Menu.INSTANCE));
                        }).dimensions(100, Menu.INSTANCE.getNextY(), 200, 20).build());

        ClientChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> {
            ChunkPos unloadedChunk = chunk.getPos();
            scannedChunks.remove(unloadedChunk);

            tracerTargets.removeIf(blockPos -> new ChunkPos(blockPos).equals(unloadedChunk));
        });

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            if (!isEnabled() || mcClient.player == null || mcClient.world == null)
                return;

            float frameTickDelta = mcClient.gameRenderer.getCamera().getLastTickProgress();

            Vec3d origin = mcClient.player.getLerpedPos(frameTickDelta);
            origin.add(0, 0.9, 0);

            for (BlockPos block : tracerTargets) {

                Vec3d targetPos = new Vec3d(block.getX(), block.getY(), block.getZ());

                GizmoDrawing.line(origin, targetPos, 0xFF00ffe5, 4).ignoreOcclusion();
            }
        });

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

                int blockCount = 0;
                int totalY = 0;

                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (be instanceof ChestBlockEntity
                            || be instanceof BarrelBlockEntity
                            || be instanceof TrappedChestBlockEntity
                            || be instanceof FurnaceBlockEntity
                            || be instanceof HopperBlockEntity) {
                        blockCount++;
                        totalY += be.getPos().getY();
                    }
                }

                if (blockCount >= minBlockEntityCount) {

                    int avgY = totalY / blockCount;
                    tracerTargets.add(cp.getBlockPos(8, avgY, 8));

                    client.getSoundManager().play(
                            new PositionedSoundInstance(
                                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                                    SoundCategory.PLAYERS,
                                    detectSoundVolume,
                                    1.0f,
                                    net.minecraft.util.math.random.Random.create(),
                                    client.player.getBlockPos()));

                    client.getToastManager().add(
                            SystemToast.create(
                                    client,
                                    SystemToast.Type.NARRATOR_TOGGLE,
                                    Text.literal("[STASH FINDER] SUS Chunk found"),
                                    Text.literal("BlockEntity Count: " + blockCount)));

                    client.inGameHud.getChatHud().addMessage(
                            Text.literal("[MOONKITTY] [STASH FINDER] sus Chunk found, chunkPos: " + cp
                                    + " ,BlockEntity Count: " + blockCount));

                    System.out.println("Chunk " + cp + " blockEntity count: " + blockCount);
                }

                scannedChunks.add(cp);
            }
        }

    }

}