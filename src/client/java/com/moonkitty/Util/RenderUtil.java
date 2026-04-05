package com.moonkitty.Util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.moonkitty.Mixin.RenderLayerAccessor;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.LayeringTransform;
import net.minecraft.client.render.OutputTarget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.RenderSetup;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import com.mojang.blaze3d.pipeline.BlendFunction;

public class RenderUtil {

    private static final RenderPipeline LINES_SEE_THROUGH_PIPELINE = RenderPipeline.builder()
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withUniform("Fog", UniformType.UNIFORM_BUFFER)
            .withUniform("Globals", UniformType.UNIFORM_BUFFER)
            .withVertexShader("core/rendertype_lines")
            .withFragmentShader("core/rendertype_lines")
            .withBlend(BlendFunction.TRANSLUCENT)
            .withCull(false)
            .withVertexFormat(VertexFormats.POSITION_COLOR_NORMAL_LINE_WIDTH, VertexFormat.DrawMode.LINES)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withLocation("moonkitty/lines_see_through")
            .build();

    public static final RenderLayer LINES_SEE_THROUGH = RenderLayerAccessor.invokeOf(
            "moonkitty_lines_see_through",
            RenderSetup.builder(LINES_SEE_THROUGH_PIPELINE)
                    .layeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
                    .outputTarget(OutputTarget.ITEM_ENTITY_TARGET)
                    .build());

    public static VertexConsumer getLineConsumer(VertexConsumerProvider consumers) {
        return consumers.getBuffer(LINES_SEE_THROUGH);
    }

    public static void drawLine(VertexConsumer consumer, MatrixStack.Entry entry,
            Vec3d cam, Vec3d from, Vec3d to, int color, float lineWidth) {
        float dx = (float) (to.x - from.x);
        float dy = (float) (to.y - from.y);
        float dz = (float) (to.z - from.z);
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 0.0001f)
            return;
        float nx = dx / len;
        float ny = dy / len;
        float nz = dz / len;

        consumer.vertex(entry, (float) (from.x - cam.x), (float) (from.y - cam.y), (float) (from.z - cam.z))
                .color(color).normal(entry, nx, ny, nz).lineWidth(lineWidth);
        consumer.vertex(entry, (float) (to.x - cam.x), (float) (to.y - cam.y), (float) (to.z - cam.z))
                .color(color).normal(entry, nx, ny, nz).lineWidth(lineWidth);
    }

    public static void drawBoxOutline(VertexConsumer consumer, MatrixStack.Entry entry,
            Vec3d cam, BlockPos pos, int color, float lineWidth) {
        float x1 = (float) (pos.getX() - cam.x);
        float y1 = (float) (pos.getY() - cam.y);
        float z1 = (float) (pos.getZ() - cam.z);
        float x2 = x1 + 1.0f;
        float y2 = y1 + 1.0f;
        float z2 = z1 + 1.0f;

        // Bottom face (4 edges)
        lineSegment(consumer, entry, x1, y1, z1, x2, y1, z1, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z1, x2, y1, z2, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z2, x1, y1, z2, color, lineWidth);
        lineSegment(consumer, entry, x1, y1, z2, x1, y1, z1, color, lineWidth);

        // Top face (4 edges)
        lineSegment(consumer, entry, x1, y2, z1, x2, y2, z1, color, lineWidth);
        lineSegment(consumer, entry, x2, y2, z1, x2, y2, z2, color, lineWidth);
        lineSegment(consumer, entry, x2, y2, z2, x1, y2, z2, color, lineWidth);
        lineSegment(consumer, entry, x1, y2, z2, x1, y2, z1, color, lineWidth);

        // Vertical edges (4 edges)
        lineSegment(consumer, entry, x1, y1, z1, x1, y2, z1, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z1, x2, y2, z1, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z2, x2, y2, z2, color, lineWidth);
        lineSegment(consumer, entry, x1, y1, z2, x1, y2, z2, color, lineWidth);
    }

    public static void drawBoxOutline(VertexConsumer consumer, MatrixStack.Entry entry,
            Vec3d cam, BlockPos pos, float sizeX, float sizeY, float sizeZ, int color, float lineWidth) {
        float x1 = (float) (pos.getX() - cam.x);
        float y1 = (float) (pos.getY() - cam.y);
        float z1 = (float) (pos.getZ() - cam.z);
        float x2 = x1 + sizeX;
        float y2 = y1 + sizeY;
        float z2 = z1 + sizeZ;

        // Bottom face (4 edges)
        lineSegment(consumer, entry, x1, y1, z1, x2, y1, z1, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z1, x2, y1, z2, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z2, x1, y1, z2, color, lineWidth);
        lineSegment(consumer, entry, x1, y1, z2, x1, y1, z1, color, lineWidth);

        // Top face (4 edges)
        lineSegment(consumer, entry, x1, y2, z1, x2, y2, z1, color, lineWidth);
        lineSegment(consumer, entry, x2, y2, z1, x2, y2, z2, color, lineWidth);
        lineSegment(consumer, entry, x2, y2, z2, x1, y2, z2, color, lineWidth);
        lineSegment(consumer, entry, x1, y2, z2, x1, y2, z1, color, lineWidth);

        // Vertical edges (4 edges)
        lineSegment(consumer, entry, x1, y1, z1, x1, y2, z1, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z1, x2, y2, z1, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z2, x2, y2, z2, color, lineWidth);
        lineSegment(consumer, entry, x1, y1, z2, x1, y2, z2, color, lineWidth);
    }

    public static void drawBoxOutline(VertexConsumer consumer, MatrixStack.Entry entry,
            Vec3d cam, BlockPos pos, float size, int color, float lineWidth) {
        drawBoxOutline(consumer, entry, cam, pos, size, size, size, color, lineWidth);
    }

    public static void drawBoxOutline(VertexConsumer consumer, MatrixStack.Entry entry,
            Vec3d cam, Vec3d pos, float sizeX, float sizeY, float sizeZ, int color, float lineWidth) {
        float x1 = (float) (pos.x - cam.x);
        float y1 = (float) (pos.y - cam.y);
        float z1 = (float) (pos.z - cam.z);
        float x2 = x1 + sizeX;
        float y2 = y1 + sizeY;
        float z2 = z1 + sizeZ;

        // Bottom face (4 edges)
        lineSegment(consumer, entry, x1, y1, z1, x2, y1, z1, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z1, x2, y1, z2, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z2, x1, y1, z2, color, lineWidth);
        lineSegment(consumer, entry, x1, y1, z2, x1, y1, z1, color, lineWidth);

        // Top face (4 edges)
        lineSegment(consumer, entry, x1, y2, z1, x2, y2, z1, color, lineWidth);
        lineSegment(consumer, entry, x2, y2, z1, x2, y2, z2, color, lineWidth);
        lineSegment(consumer, entry, x2, y2, z2, x1, y2, z2, color, lineWidth);
        lineSegment(consumer, entry, x1, y2, z2, x1, y2, z1, color, lineWidth);

        // Vertical edges (4 edges)
        lineSegment(consumer, entry, x1, y1, z1, x1, y2, z1, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z1, x2, y2, z1, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z2, x2, y2, z2, color, lineWidth);
        lineSegment(consumer, entry, x1, y1, z2, x1, y2, z2, color, lineWidth);
    }

    public static void drawBoxOutlineCentered(VertexConsumer consumer, MatrixStack.Entry entry,
            Vec3d cam, BlockPos pos, float size, int color, float lineWidth) {
        float offset = (1.0f - size) / 2.0f;
        Vec3d centered = new Vec3d(pos.getX() + offset, pos.getY() + offset, pos.getZ() + offset);
        drawBoxOutline(consumer, entry, cam, centered, size, size, size, color, lineWidth);
    }

    private static void lineSegment(VertexConsumer consumer, MatrixStack.Entry entry,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            int color, float lineWidth) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float dz = z2 - z1;
        float len = (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (len < 0.0001f)
            return;
        float nx = dx / len;
        float ny = dy / len;
        float nz = dz / len;

        consumer.vertex(entry, x1, y1, z1).color(color).normal(entry, nx, ny, nz).lineWidth(lineWidth);
        consumer.vertex(entry, x2, y2, z2).color(color).normal(entry, nx, ny, nz).lineWidth(lineWidth);
    }
}
