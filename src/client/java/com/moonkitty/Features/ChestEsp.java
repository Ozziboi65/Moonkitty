package com.moonkitty.Features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.Feature;
import com.moonkitty.Features.Menu.EspMenu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import com.moonkitty.Gui.Menu;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import com.moonkitty.Feature;
import com.moonkitty.FeatureManager;
import com.moonkitty.MoonkittyClient;
import com.moonkitty.Features.esp;
import com.moonkitty.Features.Menu.ChestEspMenu;
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
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.minecraft.client.render.DrawStyle;

import java.util.ArrayList;
import java.util.List;

public class ChestEsp extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public MinecraftClient McClient;

    private int range = 50;
    private World world;

    private boolean renderChest = true;
    private boolean renderEnderChest = true;
    private boolean renderBarrel = true;
    private boolean renderShulker = true;

    public int chestColor = 0xFFFFDD54;
    public int enderChestColor = 0xFF00FFFF;
    public int barrelColor = 0xFF00FF00;
    public int shulkerColor = 0xFF8A17FF;

    public List<ChestBlockEntity> chestList = new ArrayList<>();
    public List<EnderChestBlockEntity> enderChestList = new ArrayList<>();
    public List<BarrelBlockEntity> barrelList = new ArrayList<>();
    public List<ShulkerBoxBlockEntity> shulkerList = new ArrayList<>();

    public ChestEsp() {
        this.name = "container esp";
        this.feature_id = 58;
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
            if (!isEnabled() || chestList.isEmpty())
                return;

            for (ChestBlockEntity chest : chestList) {
                if (renderChest)
                    GizmoDrawing.box(chest.getPos(), DrawStyle.stroked(chestColor)).ignoreOcclusion();
            }

            for (EnderChestBlockEntity chest : enderChestList) {
                if (renderEnderChest)
                    GizmoDrawing.box(chest.getPos(), DrawStyle.stroked(enderChestColor)).ignoreOcclusion();
            }

            for (BarrelBlockEntity chest : barrelList) {
                if (renderBarrel)
                    GizmoDrawing.box(chest.getPos(), DrawStyle.stroked(barrelColor)).ignoreOcclusion();
            }

            for (ShulkerBoxBlockEntity chest : shulkerList) {
                if (renderShulker)
                    GizmoDrawing.box(chest.getPos(), DrawStyle.stroked(shulkerColor)).ignoreOcclusion();
            }

        });

        menuObject.registerNewFeatureButton(
                ButtonWidget.builder(
                        Text.literal("ChestEsp"),
                        btn -> {
                            MinecraftClient.getInstance().setScreen(new ChestEspMenu(Menu.INSTANCE));
                        }).dimensions(100, Menu.INSTANCE.getNextY(), 200, 20).build());
    }

    @Override
    public void tick(MinecraftClient client) {

        if (client.player == null || client.world == null)
            return;

        chestList.clear();
        enderChestList.clear();
        barrelList.clear();
        shulkerList.clear();

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

                        chestList.add(chest);

                    }
                }

                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (be instanceof EnderChestBlockEntity chest) {

                        enderChestList.add(chest);

                    }
                }

                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (be instanceof BarrelBlockEntity chest) {

                        barrelList.add(chest);

                    }
                }

                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (be instanceof ShulkerBoxBlockEntity chest) {

                        shulkerList.add(chest);

                    }
                }

            }
        }

    }
}