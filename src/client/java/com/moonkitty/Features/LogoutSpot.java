package com.moonkitty.Features;

import com.moonkitty.Feature;

import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.DrawStyle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.debug.gizmo.GizmoDrawing;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.moonkitty.Category;

public class LogoutSpot extends Feature {

    static MinecraftClient client = null;

    public List<Entity> loggedEntities = new ArrayList<>();
    private final Map<UUID, Entity> pendingLogout = new HashMap<>();

    public LogoutSpot() {
        this.name = "logoutESP";
        this.setCategory(Category.WORLD);
    }

    public int color = 0xFFFF0000;
    private final int LINE_THICKNESS = 3;

    @Override
    public void tick(MinecraftClient mcClient) {
        if (!isEnabled() || pendingLogout.isEmpty())
            return;

        if (mcClient.getNetworkHandler() == null) {
            pendingLogout.clear();
            return;
        }

        Iterator<Map.Entry<UUID, Entity>> it = pendingLogout.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Entity> entry = it.next();
            if (mcClient.getNetworkHandler().getPlayerListEntry(entry.getKey()) == null) {
                loggedEntities.add(entry.getValue());
            }
            it.remove();
        }
    }

    @Override
    public void init() {
        this.client = MinecraftClient.getInstance();

        ClientEntityEvents.ENTITY_UNLOAD.register((entity, world) -> {
            if (!isEnabled())
                return;

            if (entity instanceof OtherClientPlayerEntity) {
                pendingLogout.put(entity.getUuid(), entity);
            }
        });

        ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!isEnabled())
                return;

            if (entity instanceof OtherClientPlayerEntity) {
                pendingLogout.remove(entity.getUuid());
                loggedEntities.removeIf(storedEntity -> storedEntity.getUuid().equals(entity.getUuid()));
            }
        });

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            if (!isEnabled())
                return;

            if (client.player == null || client.world == null)
                return;

            for (Entity entity : loggedEntities) {
                GizmoDrawing.box(entity.getBoundingBox(), DrawStyle.stroked(color, LINE_THICKNESS)).ignoreOcclusion();
                GizmoDrawing.blockLabel("combatLog", entity.getBlockPos(), 5,
                        0xFFFF0000, 1.0f);
                GizmoDrawing.blockLabel(entity.getStringifiedName(), entity.getBlockPos(), 1,
                        0xFFFFFFFF, 1.5f);
            }
        });
    }

}