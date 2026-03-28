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

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;

import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;

import net.minecraft.client.MinecraftClient;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;

import com.moonkitty.Util.RenderUtil;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.List;

public class ChestEsp extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public MinecraftClient McClient;

    private boolean renderChest = true;
    private boolean renderEnderChest = true;
    private boolean renderBarrel = true;
    private boolean renderShulker = true;

    public int chestColor = 0xFFFFDD54;
    public int enderChestColor = 0xFF00FFFF;
    public int barrelColor = 0xFF00FF00;
    public int shulkerColor = 0xFF8A17FF;

    private int scanTime = 0;
    public int scanInterval = 20;

    public List<ChestBlockEntity> chestList = new ArrayList<>();
    public List<EnderChestBlockEntity> enderChestList = new ArrayList<>();
    public List<BarrelBlockEntity> barrelList = new ArrayList<>();
    public List<ShulkerBoxBlockEntity> shulkerList = new ArrayList<>();

    public ChestEsp() {
        this.name = "container esp";
        this.feature_id = 58;
        this.setCategory(Category.RENDER);
        this.setEnabled(true);
    }

    public boolean getRenderChest() {
        return renderChest;
    }

    public void setRenderChest(boolean enabled) {
        if (this.renderChest == enabled)
            return;
        this.renderChest = enabled;
    }

    public void toggleRenderChest() {
        setRenderChest(!this.renderChest);
    }

    public boolean getRenderEnderChest() {
        return renderEnderChest;
    }

    public void setRenderEnderChest(boolean enabled) {
        if (this.renderEnderChest == enabled)
            return;
        this.renderEnderChest = enabled;
    }

    public void toggleRenderEnderChest() {
        setRenderEnderChest(!this.renderEnderChest);
    }

    public boolean getRenderBarrel() {
        return renderBarrel;
    }

    public void setRenderBarrel(boolean enabled) {
        if (this.renderBarrel == enabled)
            return;
        this.renderBarrel = enabled;
    }

    public void toggleRenderBarrel() {
        setRenderBarrel(!this.renderBarrel);
    }

    public boolean getRenderShulker() {
        return renderShulker;
    }

    public void setRenderShulker(boolean enabled) {
        if (this.renderShulker == enabled)
            return;
        this.renderShulker = enabled;
    }

    public void toggleRenderShulker() {
        setRenderShulker(!this.renderShulker);
    }

    public void setChestColor(int color) {
        this.chestColor = color;
    }

    @Override
    public void init() {
        this.McClient = MinecraftClient.getInstance();
        Menu menuObject = Menu.INSTANCE;

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            if (!isEnabled())
                return;

            if (context.consumers() == null)
                return;

            Camera camera = McClient.gameRenderer.getCamera();
            Vec3d cam = camera.getCameraPos();
            MatrixStack matrices = new MatrixStack();
            MatrixStack.Entry entry = matrices.peek();
            VertexConsumer consumer = RenderUtil.getLineConsumer(context.consumers());

            if (renderChest) {
                for (ChestBlockEntity chest : chestList) {
                    RenderUtil.drawBoxOutline(consumer, entry, cam, chest.getPos(), chestColor, 1.0f);
                }
            }

            if (renderEnderChest) {
                for (EnderChestBlockEntity chest : enderChestList) {
                    RenderUtil.drawBoxOutline(consumer, entry, cam, chest.getPos(), enderChestColor, 1.0f);
                }
            }

            if (renderBarrel) {
                for (BarrelBlockEntity chest : barrelList) {
                    RenderUtil.drawBoxOutline(consumer, entry, cam, chest.getPos(), barrelColor, 1.0f);
                }
            }

            if (renderShulker) {
                for (ShulkerBoxBlockEntity chest : shulkerList) {
                    RenderUtil.drawBoxOutline(consumer, entry, cam, chest.getPos(), shulkerColor, 1.0f);
                }
            }
        });
    }

    @Override
    public void tick(MinecraftClient client) {

        if (client.player == null || client.world == null)
            return;

        if (!this.isEnabled())
            return;

        scanTime++;

        if (scanTime < scanInterval)
            return;

        List<ChestBlockEntity> newChests = new ArrayList<>();
        List<EnderChestBlockEntity> newEnderChests = new ArrayList<>();
        List<BarrelBlockEntity> newBarrels = new ArrayList<>();
        List<ShulkerBoxBlockEntity> newShulkers = new ArrayList<>();

        int renderDist = client.options.getClampedViewDistance();
        ChunkPos center = client.player.getChunkPos();

        for (int dx = -renderDist; dx <= renderDist; dx++) {
            for (int dz = -renderDist; dz <= renderDist; dz++) {
                WorldChunk chunk = client.world.getChunkManager()
                        .getWorldChunk(center.x + dx, center.z + dz);
                if (chunk == null)
                    continue;

                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (be instanceof ChestBlockEntity chest) {
                        newChests.add(chest);
                    } else if (be instanceof ShulkerBoxBlockEntity shulker) {
                        newShulkers.add(shulker);
                    } else if (be instanceof BarrelBlockEntity barrel) {
                        newBarrels.add(barrel);
                    } else if (be instanceof EnderChestBlockEntity enderChest) {
                        newEnderChests.add(enderChest);
                    }
                }
            }
        }

        chestList = newChests;
        enderChestList = newEnderChests;
        barrelList = newBarrels;
        shulkerList = newShulkers;

        scanTime = 0;

    }
}