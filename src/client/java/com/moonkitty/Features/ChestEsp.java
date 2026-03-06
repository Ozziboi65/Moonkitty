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
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
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
    private boolean showItem;

    public int chestColor = 0xFFFFDD54;

    public List<ChestBlockEntity> chestList = new ArrayList<>();

    public ChestEsp() {
        this.name = "CHESTESP";
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
            }
        }

    }
}