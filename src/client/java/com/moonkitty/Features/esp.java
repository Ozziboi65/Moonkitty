package com.moonkitty.Features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.BooleanSetting;
import com.moonkitty.Category;
import com.moonkitty.Feature;
import com.moonkitty.NumberSetting;

import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import com.moonkitty.Gui.Menu;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;

import com.moonkitty.Feature;
import com.moonkitty.FeatureManager;
import com.moonkitty.Features.esp;

import com.moonkitty.Features.fakeplayer;
import com.moonkitty.Features.companion;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.client.gui.widget.ScrollableWidget;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;

public class esp extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public MinecraftClient client;

    private BooleanSetting boxSetting;
    private BooleanSetting hostileSetting;
    private BooleanSetting itemSetting;

    private boolean showHostile;
    private boolean showItem;

    public int color_player = 0xFFFF00FF;

    public int color_hostile = 0xFFFFFFFF;

    public int color_item = 0xFFFBFF00;

    public boolean renderBox = true;

    public boolean outline;

    public esp() {
        this.name = "ESP";
        this.feature_id = 1;
        this.setCategory(Category.RENDER);
        this.setEnabled(false);

        boxSetting = new BooleanSetting("Box", true);
        hostileSetting = new BooleanSetting("Hostile", false);
        itemSetting = new BooleanSetting("Items", false);

        addSetting(boxSetting);
        addSetting(hostileSetting);
        addSetting(itemSetting);
    }

    @Override
    public void init() {
        this.client = MinecraftClient.getInstance();

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            renderBox = boxSetting.getValue();
            showHostile = hostileSetting.getValue();
            showItem = itemSetting.getValue();

            if (!isEnabled() || client.player == null || client.world == null)
                return;

            for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
                if (player == client.player)
                    continue;

                if (renderBox) {
                    GizmoDrawing.box(player.getBoundingBox(), DrawStyle.stroked(color_player, 2)).ignoreOcclusion();
                }
            }

            for (Entity entity : client.world.getEntities()) {
                if (entity instanceof ItemEntity itemEntity)
                    if (showItem) {
                        GizmoDrawing.box(itemEntity.getBoundingBox(), DrawStyle.stroked(color_item, 2))
                                .ignoreOcclusion();
                    }

            }

            for (Entity entity : client.world.getEntities())
                if (entity instanceof HostileEntity) {

                    if (showHostile) {
                        GizmoDrawing.box(entity.getBoundingBox(), DrawStyle.stroked(color_hostile, 2))
                                .ignoreOcclusion();
                    }

                }

        });

    }
}