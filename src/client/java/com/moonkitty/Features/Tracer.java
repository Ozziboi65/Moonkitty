package com.moonkitty.Features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.Feature;
import com.moonkitty.Gui.Menu;
import com.moonkitty.Features.Menu.tracerMenu;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.debug.gizmo.GizmoDrawing;

public class Tracer extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");

    public boolean renderPlayer = true;

    public int playerColor = 0xFFFF0000;

    public Tracer() {
        this.name = "Tracer";
        this.feature_id = 85;
        this.setEnabled(true);
    }

    @Override
    public void init() {
        MinecraftClient client = MinecraftClient.getInstance();

        Menu.INSTANCE.registerNewFeatureButton(
                ButtonWidget.builder(
                        Text.literal("Tracers"),
                        btn -> client.setScreen(new tracerMenu(Menu.INSTANCE)))
                        .dimensions(100, Menu.INSTANCE.getNextY(), 200, 20).build());

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            if (!isEnabled() || client.player == null || client.world == null)
                return;

            float frameTickDelta = client.gameRenderer.getCamera().getLastTickProgress();

            Vec3d origin = client.player.getLerpedPos(frameTickDelta);
            origin.add(0, 0.9, 0);

            for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
                if (player == client.player)
                    continue;
                if (renderPlayer)
                    GizmoDrawing.line(origin, player.getLerpedPos(frameTickDelta), playerColor).ignoreOcclusion();
            }
        });
    }
}