package com.moonkitty.visuals;

import com.moonkitty.Feature;
import com.moonkitty.Category;

import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.particle.ParticleTypes;

import com.moonkitty.Mixin.ClientPlayerInteractionManagerAccessor;
import com.moonkitty.Util.RenderUtil;;

public class BreakOverlay extends Feature {
    private MinecraftClient mcClient = null;

    public BreakOverlay() {
        this.name = "BreakOverlay";
        this.setCategory(Category.VISUAL);
        this.setEnabled(true);
    }

    BlockPos breakingPos = null;
    float breakProgress = 0;

    @Override
    public void init() {
        mcClient = MinecraftClient.getInstance();

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            if (!this.isEnabled())
                return;

            if (breakingPos == null)
                return;

            if (context.consumers() == null)
                return;

            Camera camera = mcClient.gameRenderer.getCamera();
            Vec3d cam = camera.getCameraPos();
            MatrixStack matrices = new MatrixStack();
            MatrixStack.Entry entry = matrices.peek();
            VertexConsumer consumer = RenderUtil.getLineConsumer(context.consumers());

            int alpha = 0xFF;
            int r = (int) (255 * breakProgress);
            int g = (int) (255 * (1.0f - breakProgress));
            int b = 0;

            int color = (alpha << 24) | (r << 16) | (g << 8) | b;

            RenderUtil.drawBoxOutline(consumer, entry, cam, Vec3d.ofCenter(breakingPos), breakProgress,
                    breakProgress, breakProgress, color, 3.0f);

        });
    }

    @Override
    public void tick(MinecraftClient client) {

        if (!isEnabled() || client.player == null || client.world == null)
            return;

        breakingPos = ((ClientPlayerInteractionManagerAccessor) client.interactionManager)
                .getCurrentBreakingPos();

        breakProgress = ((ClientPlayerInteractionManagerAccessor) client.interactionManager)
                .getCurrentBreakingProgress();
    }

}