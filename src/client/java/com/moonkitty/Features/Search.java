package com.moonkitty.Features;

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

import com.moonkitty.Features.fakeplayer;

import com.moonkitty.Features.companion;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.registry.Registries;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;

import net.minecraft.client.MinecraftClient;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.client.render.DrawStyle;

import net.minecraft.util.Identifier;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Search extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public MinecraftClient McClient;

    private boolean renderChest = true;

    public int color = 0xFFFFDD54;

    private final Map<Long, Set<BlockPos>> chunkBlocks = new ConcurrentHashMap<>();
    private boolean needsFullUpdate = true;

    private static final int MAX_TOTAL_BLOCKS = 50000;
    private static final int MAX_BLOCKS_PER_CHUNK = 2000;
    private boolean limitReached = false;

    Block target = Blocks.BLACKSTONE;

    public Search() {
        this.name = "Search";
        this.feature_id = 58;
        this.setCategory(Category.WORLD);
        this.setEnabled(false);
    }

    public boolean getRenderChest() {
        return renderChest;
    }

    public static Block getBlockFromString(String id) {
        Identifier identifier = Identifier.of(id);
        return Registries.BLOCK.get(identifier);
    }

    @Override
    public void init() {
        this.McClient = MinecraftClient.getInstance();
        Menu menuObject = Menu.INSTANCE;

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            if (!isEnabled() || chunkBlocks.isEmpty())
                return;

            for (Set<BlockPos> blocks : chunkBlocks.values()) {
                for (BlockPos blockPos : blocks) {
                    GizmoDrawing.box(blockPos, DrawStyle.stroked(color)).ignoreOcclusion();
                }
            }

        });

    }

    @Override
    protected void onEnable() {
        needsFullUpdate = true;
        limitReached = false;
    }

    @Override
    protected void onDisable() {
        chunkBlocks.clear();
    }

    private void searchChunk(WorldChunk chunk) {

        if (limitReached) {
            return;
        }

        long chunkKey = chunk.getPos().toLong();
        Set<BlockPos> foundBlocks = new HashSet<>();

        int chunkX = chunk.getPos().x;
        int chunkZ = chunk.getPos().z;
        int worldX = chunkX << 4;
        int worldZ = chunkZ << 4;

        ChunkSection[] sections = chunk.getSectionArray();

        sectionLoop: for (int sectionIndex = 0; sectionIndex < sections.length; sectionIndex++) {
            ChunkSection section = sections[sectionIndex];

            if (section == null || section.isEmpty())
                continue;

            boolean sectionContainsTarget = false;
            try {

                sectionContainsTarget = section.hasAny(state -> state.getBlock() == target);
            } catch (Exception e) {

                sectionContainsTarget = true;
            }

            if (!sectionContainsTarget) {
                continue;
            }

            int sectionY = (McClient.world.getBottomSectionCoord() + sectionIndex) << 4;

            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < 16; y++) {
                        BlockState state = section.getBlockState(x, y, z);
                        Block block = state.getBlock();

                        if (block == target) {
                            foundBlocks.add(new BlockPos(worldX + x, sectionY + y, worldZ + z));

                            if (foundBlocks.size() >= MAX_BLOCKS_PER_CHUNK) {
                                LOGGER.warn(
                                        "[Search] Chunk exceeded block limit ({}). Target block '{}' ",
                                        MAX_BLOCKS_PER_CHUNK, Registries.BLOCK.getId(target));
                                break sectionLoop;
                            }
                        }
                    }
                }
            }
        }

        if (!foundBlocks.isEmpty()) {
            chunkBlocks.put(chunkKey, foundBlocks);

            int totalBlocks = chunkBlocks.values().stream().mapToInt(Set::size).sum();
            if (totalBlocks >= MAX_TOTAL_BLOCKS) {
                limitReached = true;
                LOGGER.error("[Search] MEMORY LIMIT REACHED! Found {} blocks stopping",
                        totalBlocks);
                LOGGER.error(
                        "[Search] Block '{}' is too many",
                        Registries.BLOCK.getId(target));
            }
        } else {
            chunkBlocks.remove(chunkKey);
        }
    }

    public void setTargetFromString(String name) {
        Block newTarget = getBlockFromString(name);
        if (newTarget != null && newTarget != Blocks.AIR) {
            target = newTarget;
            needsFullUpdate = true;
            limitReached = false; // Reset limit when changing target
            LOGGER.info("[Search] Target set to: {}", name);
        }
    }

    @Override
    public void tick(MinecraftClient client) {

        if (!this.isEnabled())
            return;

        if (client.player == null || client.world == null)
            return;

        if (needsFullUpdate) {
            chunkBlocks.clear();

            int renderDist = client.options.getClampedViewDistance();
            ChunkPos centerChunk = client.player.getChunkPos();

            for (int dx = -renderDist; dx <= renderDist; dx++) {
                for (int dz = -renderDist; dz <= renderDist; dz++) {
                    WorldChunk chunk = client.world.getChunkManager()
                            .getWorldChunk(centerChunk.x + dx, centerChunk.z + dz);

                    if (chunk != null) {
                        if (target != null) {
                            searchChunk(chunk);
                        }
                    }
                }
            }

            needsFullUpdate = false;
            return;
        }

        if (client.world.getTime() % 20 == 0) {
            ChunkPos playerChunk = client.player.getChunkPos();
            int maxDist = client.options.getClampedViewDistance() + 2;

            chunkBlocks.entrySet().removeIf(entry -> {
                long key = entry.getKey();
                int chunkX = ChunkPos.getPackedX(key);
                int chunkZ = ChunkPos.getPackedZ(key);
                int dx = chunkX - playerChunk.x;
                int dz = chunkZ - playerChunk.z;
                return Math.abs(dx) > maxDist || Math.abs(dz) > maxDist;
            });

            int renderDist = client.options.getClampedViewDistance();

            for (int dx = -renderDist; dx <= renderDist; dx++) {
                for (int dz = -renderDist; dz <= renderDist; dz++) {
                    long chunkKey = ChunkPos.toLong(playerChunk.x + dx, playerChunk.z + dz);

                    if (!chunkBlocks.containsKey(chunkKey)) {
                        WorldChunk chunk = client.world.getChunkManager()
                                .getWorldChunk(playerChunk.x + dx, playerChunk.z + dz);

                        if (chunk != null && target != null) {
                            searchChunk(chunk);
                        }
                    }
                }
            }
        }

    }
}