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

public class esp extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public MinecraftClient McClient;

    private NumberSetting rangeSetting;
    private BooleanSetting boxSetting;
    private BooleanSetting hostileSetting;
    private BooleanSetting itemSetting;

    private int range = 50;
    private World world;

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
        this.setEnabled(true);

        boxSetting = new BooleanSetting("Box", true);
        hostileSetting = new BooleanSetting("Hostile", false);
        itemSetting = new BooleanSetting("Items", false);

        addSetting(boxSetting);
        addSetting(hostileSetting);
        addSetting(itemSetting);
    }

    public boolean getHostile() {
        return showHostile;
    }

    public boolean getOutline() {
        return outline;
    }

    public boolean getItem() {
        return showItem;
    }

    public boolean getBox() {
        return renderBox;
    }

    @Override
    public void init() {
        this.McClient = MinecraftClient.getInstance();
        Menu menuObject = Menu.INSTANCE;

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            renderBox = boxSetting.getValue();
            showHostile = hostileSetting.getValue();
            showItem = itemSetting.getValue();

            if (!isEnabled() || McClient.player == null || McClient.world == null)
                return;

            if (!getBox())
                return;

            for (AbstractClientPlayerEntity player : McClient.world.getPlayers()) {
                if (player == McClient.player)
                    continue;

                if (renderBox) {
                    GizmoDrawing.box(player.getBoundingBox(), DrawStyle.stroked(color_player, 2)).ignoreOcclusion();
                }
            }

            for (Entity entity : McClient.world.getEntities())

                if (entity instanceof HostileEntity) {

                    if (renderBox) {
                        GizmoDrawing.box(entity.getBoundingBox(), DrawStyle.stroked(color_hostile, 2))
                                .ignoreOcclusion();
                    }

                }

        });

    }

    public void setItem(boolean enabled) {
        if (this.showItem == enabled)
            return;
        this.showItem = enabled;
    }

    public void setHostile(boolean enabled) {
        if (this.showHostile == enabled)
            return;
        this.showHostile = enabled;
    }

    public void toggleHostile() {
        setHostile(!this.showHostile);
    }

    public void setBox(boolean enabled) {
        if (this.renderBox == enabled)
            return;
        this.renderBox = enabled;
    }

    public void toggleBox() {
        setBox(!this.renderBox);
    }

    public void setOutline(boolean enabled) {
        if (this.outline == enabled)
            return;
        this.outline = enabled;
    }

    public void toggleOutline() {
        setOutline(!this.outline);
    }

    public void toggleItem() {
        setItem(!this.showItem);
    }

    public void setRange(int range) {
        this.range = range;
    }

    public void setPlayerColor(int color) {
        this.color_player = color;
    }

    public void setHostileColor(int color) {
        this.color_hostile = color;
    }
}