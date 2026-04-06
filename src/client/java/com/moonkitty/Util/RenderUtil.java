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
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import com.mojang.blaze3d.pipeline.BlendFunction;
import java.util.List;

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

    private static final RenderPipeline CHAMS_PIPELINE = RenderPipeline.builder()
            .withUniform("DynamicTransforms", UniformType.UNIFORM_BUFFER)
            .withUniform("Projection", UniformType.UNIFORM_BUFFER)
            .withVertexShader("core/moonkitty_chams")
            .withFragmentShader("core/moonkitty_chams")
            .withBlend(BlendFunction.TRANSLUCENT)
            .withCull(false)
            .withVertexFormat(VertexFormats.POSITION_COLOR_TEXTURE_OVERLAY_LIGHT_NORMAL,
                    VertexFormat.DrawMode.QUADS)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(true)
            .withLocation("moonkitty/chams")
            .build();

    public static final RenderLayer CHAMS_LAYER = RenderLayerAccessor.invokeOf(
            "moonkitty_chams",
            RenderSetup.builder(CHAMS_PIPELINE)
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
        float lx = (float) (pos.getX() - cam.x);
        float ly = (float) (pos.getY() - cam.y);
        float lz = (float) (pos.getZ() - cam.z);

        // Apply the pose matrix once per distinct corner instead of once per vertex.
        // base = M*(lx,ly,lz)+t; dX/dY/dZ = columns 0/1/2 of M (unit-step deltas).
        var pm = entry.getPositionMatrix();
        float baseX = pm.m00() * lx + pm.m10() * ly + pm.m20() * lz + pm.m30();
        float baseY = pm.m01() * lx + pm.m11() * ly + pm.m21() * lz + pm.m31();
        float baseZ = pm.m02() * lx + pm.m12() * ly + pm.m22() * lz + pm.m32();
        float dxX = pm.m00(), dxY = pm.m01(), dxZ = pm.m02();
        float dyX = pm.m10(), dyY = pm.m11(), dyZ = pm.m12();
        float dzX = pm.m20(), dzY = pm.m21(), dzZ = pm.m22();

        // 8 corners: p{x}{y}{z} — 0=low, 1=high
        float p000x = baseX, p000y = baseY, p000z = baseZ;
        float p100x = baseX + dxX, p100y = baseY + dxY, p100z = baseZ + dxZ;
        float p010x = baseX + dyX, p010y = baseY + dyY, p010z = baseZ + dyZ;
        float p110x = baseX + dxX + dyX, p110y = baseY + dxY + dyY, p110z = baseZ + dxZ + dyZ;
        float p001x = baseX + dzX, p001y = baseY + dzY, p001z = baseZ + dzZ;
        float p101x = baseX + dxX + dzX, p101y = baseY + dxY + dzY, p101z = baseZ + dxZ + dzZ;
        float p011x = baseX + dyX + dzX, p011y = baseY + dyY + dzY, p011z = baseZ + dyZ + dzZ;
        float p111x = baseX + dxX + dyX + dzX, p111y = baseY + dxY + dyY + dzY, p111z = baseZ + dxZ + dyZ + dzZ;

        // Apply the normal matrix once to the 5 distinct axis-aligned normals.
        // For axis-aligned n, M3*n selects a column of M3: (1,0,0)->col0,
        // (0,1,0)->col1, (0,0,1)->col2.
        var nm = entry.getNormalMatrix();
        float pxNx = nm.m00, pxNy = nm.m01, pxNz = nm.m02; // ( 1, 0, 0)
        float nxNx = -nm.m00, nxNy = -nm.m01, nxNz = -nm.m02; // (-1, 0, 0)
        float pzNx = nm.m20, pzNy = nm.m21, pzNz = nm.m22; // ( 0, 0, 1)
        float nzNx = -nm.m20, nzNy = -nm.m21, nzNz = -nm.m22; // ( 0, 0,-1)
        float pyNx = nm.m10, pyNy = nm.m11, pyNz = nm.m12; // ( 0, 1, 0)

        // Bottom face (4 edges)
        consumer.vertex(p000x, p000y, p000z).color(color).normal(pxNx, pxNy, pxNz).lineWidth(lineWidth);
        consumer.vertex(p100x, p100y, p100z).color(color).normal(pxNx, pxNy, pxNz).lineWidth(lineWidth);
        consumer.vertex(p100x, p100y, p100z).color(color).normal(pzNx, pzNy, pzNz).lineWidth(lineWidth);
        consumer.vertex(p101x, p101y, p101z).color(color).normal(pzNx, pzNy, pzNz).lineWidth(lineWidth);
        consumer.vertex(p101x, p101y, p101z).color(color).normal(nxNx, nxNy, nxNz).lineWidth(lineWidth);
        consumer.vertex(p001x, p001y, p001z).color(color).normal(nxNx, nxNy, nxNz).lineWidth(lineWidth);
        consumer.vertex(p001x, p001y, p001z).color(color).normal(nzNx, nzNy, nzNz).lineWidth(lineWidth);
        consumer.vertex(p000x, p000y, p000z).color(color).normal(nzNx, nzNy, nzNz).lineWidth(lineWidth);

        // Top face (4 edges)
        consumer.vertex(p010x, p010y, p010z).color(color).normal(pxNx, pxNy, pxNz).lineWidth(lineWidth);
        consumer.vertex(p110x, p110y, p110z).color(color).normal(pxNx, pxNy, pxNz).lineWidth(lineWidth);
        consumer.vertex(p110x, p110y, p110z).color(color).normal(pzNx, pzNy, pzNz).lineWidth(lineWidth);
        consumer.vertex(p111x, p111y, p111z).color(color).normal(pzNx, pzNy, pzNz).lineWidth(lineWidth);
        consumer.vertex(p111x, p111y, p111z).color(color).normal(nxNx, nxNy, nxNz).lineWidth(lineWidth);
        consumer.vertex(p011x, p011y, p011z).color(color).normal(nxNx, nxNy, nxNz).lineWidth(lineWidth);
        consumer.vertex(p011x, p011y, p011z).color(color).normal(nzNx, nzNy, nzNz).lineWidth(lineWidth);
        consumer.vertex(p010x, p010y, p010z).color(color).normal(nzNx, nzNy, nzNz).lineWidth(lineWidth);

        // Vertical edges (4 edges)
        consumer.vertex(p000x, p000y, p000z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
        consumer.vertex(p010x, p010y, p010z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
        consumer.vertex(p100x, p100y, p100z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
        consumer.vertex(p110x, p110y, p110z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
        consumer.vertex(p101x, p101y, p101z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
        consumer.vertex(p111x, p111y, p111z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
        consumer.vertex(p001x, p001y, p001z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
        consumer.vertex(p011x, p011y, p011z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
    }

    /**
     * Draws outlines for many BlockPos boxes sharing the same
     * entry/cam/color/lineWidth.
     * Extracts the matrix components once up-front instead of once per box.
     */
    public static void drawBoxOutlines(VertexConsumer consumer, MatrixStack.Entry entry,
            Vec3d cam, List<BlockPos> positions, int color, float lineWidth) {
        if (positions.isEmpty())
            return;
        // Dispatch to the concrete type so the JIT can devirtualize and inline
        // the vertex/color/normal/lineWidth call chain (INVOKEVIRTUAL vs
        // INVOKEINTERFACE).
        if (consumer instanceof BufferBuilder bb) {
            drawBoxOutlinesImpl(bb, entry, cam, positions, color, lineWidth);
        } else {
            drawBoxOutlinesImpl(consumer, entry, cam, positions, color, lineWidth);
        }
    }

    /**
     * Identity-matrix fast path: no MatrixStack.Entry required.
     * Vertex positions are simply block-coordinates minus camera position.
     * Normals are axis-aligned constants. Eliminates all matrix getter calls
     * and multiply arithmetic — use this whenever rendering in world-space
     * with no additional transform (e.g. ChestEsp).
     */
    public static void drawBoxOutlines(VertexConsumer consumer, Vec3d cam,
            List<BlockPos> positions, int color, float lineWidth) {
        if (positions.isEmpty())
            return;
        if (consumer instanceof BufferBuilder bb) {
            drawBoxOutlinesWorld(bb, cam, positions, color, lineWidth);
        } else {
            drawBoxOutlinesWorld(consumer, cam, positions, color, lineWidth);
        }
    }

    private static void drawBoxOutlinesWorld(BufferBuilder consumer, Vec3d cam,
            List<BlockPos> positions, int color, float lineWidth) {
        float camX = (float) cam.x, camY = (float) cam.y, camZ = (float) cam.z;
        for (int i = 0, n = positions.size(); i < n; i++) {
            BlockPos pos = positions.get(i);
            float x0 = pos.getX() - camX, y0 = pos.getY() - camY, z0 = pos.getZ() - camZ;
            float x1 = x0 + 1f, y1 = y0 + 1f, z1 = z0 + 1f;
            consumer.vertex(x0, y0, z0).color(color).normal(1, 0, 0).lineWidth(lineWidth);
            consumer.vertex(x1, y0, z0).color(color).normal(1, 0, 0).lineWidth(lineWidth);
            consumer.vertex(x1, y0, z0).color(color).normal(0, 0, 1).lineWidth(lineWidth);
            consumer.vertex(x1, y0, z1).color(color).normal(0, 0, 1).lineWidth(lineWidth);
            consumer.vertex(x1, y0, z1).color(color).normal(-1, 0, 0).lineWidth(lineWidth);
            consumer.vertex(x0, y0, z1).color(color).normal(-1, 0, 0).lineWidth(lineWidth);
            consumer.vertex(x0, y0, z1).color(color).normal(0, 0, -1).lineWidth(lineWidth);
            consumer.vertex(x0, y0, z0).color(color).normal(0, 0, -1).lineWidth(lineWidth);
            consumer.vertex(x0, y1, z0).color(color).normal(1, 0, 0).lineWidth(lineWidth);
            consumer.vertex(x1, y1, z0).color(color).normal(1, 0, 0).lineWidth(lineWidth);
            consumer.vertex(x1, y1, z0).color(color).normal(0, 0, 1).lineWidth(lineWidth);
            consumer.vertex(x1, y1, z1).color(color).normal(0, 0, 1).lineWidth(lineWidth);
            consumer.vertex(x1, y1, z1).color(color).normal(-1, 0, 0).lineWidth(lineWidth);
            consumer.vertex(x0, y1, z1).color(color).normal(-1, 0, 0).lineWidth(lineWidth);
            consumer.vertex(x0, y1, z1).color(color).normal(0, 0, -1).lineWidth(lineWidth);
            consumer.vertex(x0, y1, z0).color(color).normal(0, 0, -1).lineWidth(lineWidth);
            consumer.vertex(x0, y0, z0).color(color).normal(0, 1, 0).lineWidth(lineWidth);
            consumer.vertex(x0, y1, z0).color(color).normal(0, 1, 0).lineWidth(lineWidth);
            consumer.vertex(x1, y0, z0).color(color).normal(0, 1, 0).lineWidth(lineWidth);
            consumer.vertex(x1, y1, z0).color(color).normal(0, 1, 0).lineWidth(lineWidth);
            consumer.vertex(x1, y0, z1).color(color).normal(0, 1, 0).lineWidth(lineWidth);
            consumer.vertex(x1, y1, z1).color(color).normal(0, 1, 0).lineWidth(lineWidth);
            consumer.vertex(x0, y0, z1).color(color).normal(0, 1, 0).lineWidth(lineWidth);
            consumer.vertex(x0, y1, z1).color(color).normal(0, 1, 0).lineWidth(lineWidth);
        }
    }

    private static void drawBoxOutlinesWorld(VertexConsumer consumer, Vec3d cam,
            List<BlockPos> positions, int color, float lineWidth) {
        float camX = (float) cam.x, camY = (float) cam.y, camZ = (float) cam.z;
        for (int i = 0, n = positions.size(); i < n; i++) {
            BlockPos pos = positions.get(i);
            float x0 = pos.getX() - camX, y0 = pos.getY() - camY, z0 = pos.getZ() - camZ;
            float x1 = x0 + 1f, y1 = y0 + 1f, z1 = z0 + 1f;
            consumer.vertex(x0, y0, z0).color(color).normal(1, 0, 0).lineWidth(lineWidth);
            consumer.vertex(x1, y0, z0).color(color).normal(1, 0, 0).lineWidth(lineWidth);
            consumer.vertex(x1, y0, z0).color(color).normal(0, 0, 1).lineWidth(lineWidth);
            consumer.vertex(x1, y0, z1).color(color).normal(0, 0, 1).lineWidth(lineWidth);
            consumer.vertex(x1, y0, z1).color(color).normal(-1, 0, 0).lineWidth(lineWidth);
            consumer.vertex(x0, y0, z1).color(color).normal(-1, 0, 0).lineWidth(lineWidth);
            consumer.vertex(x0, y0, z1).color(color).normal(0, 0, -1).lineWidth(lineWidth);
            consumer.vertex(x0, y0, z0).color(color).normal(0, 0, -1).lineWidth(lineWidth);
            consumer.vertex(x0, y1, z0).color(color).normal(1, 0, 0).lineWidth(lineWidth);
            consumer.vertex(x1, y1, z0).color(color).normal(1, 0, 0).lineWidth(lineWidth);
            consumer.vertex(x1, y1, z0).color(color).normal(0, 0, 1).lineWidth(lineWidth);
            consumer.vertex(x1, y1, z1).color(color).normal(0, 0, 1).lineWidth(lineWidth);
            consumer.vertex(x1, y1, z1).color(color).normal(-1, 0, 0).lineWidth(lineWidth);
            consumer.vertex(x0, y1, z1).color(color).normal(-1, 0, 0).lineWidth(lineWidth);
            consumer.vertex(x0, y1, z1).color(color).normal(0, 0, -1).lineWidth(lineWidth);
            consumer.vertex(x0, y1, z0).color(color).normal(0, 0, -1).lineWidth(lineWidth);
            consumer.vertex(x0, y0, z0).color(color).normal(0, 1, 0).lineWidth(lineWidth);
            consumer.vertex(x0, y1, z0).color(color).normal(0, 1, 0).lineWidth(lineWidth);
            consumer.vertex(x1, y0, z0).color(color).normal(0, 1, 0).lineWidth(lineWidth);
            consumer.vertex(x1, y1, z0).color(color).normal(0, 1, 0).lineWidth(lineWidth);
            consumer.vertex(x1, y0, z1).color(color).normal(0, 1, 0).lineWidth(lineWidth);
            consumer.vertex(x1, y1, z1).color(color).normal(0, 1, 0).lineWidth(lineWidth);
            consumer.vertex(x0, y0, z1).color(color).normal(0, 1, 0).lineWidth(lineWidth);
            consumer.vertex(x0, y1, z1).color(color).normal(0, 1, 0).lineWidth(lineWidth);
        }
    }

    private static void drawBoxOutlinesImpl(BufferBuilder consumer, MatrixStack.Entry entry,
            Vec3d cam, List<BlockPos> positions, int color, float lineWidth) {
        var pm = entry.getPositionMatrix();
        float t00 = pm.m00(), t01 = pm.m01(), t02 = pm.m02();
        float t10 = pm.m10(), t11 = pm.m11(), t12 = pm.m12();
        float t20 = pm.m20(), t21 = pm.m21(), t22 = pm.m22();
        float t30 = pm.m30(), t31 = pm.m31(), t32 = pm.m32();
        var nm = entry.getNormalMatrix();
        float pxNx = nm.m00, pxNy = nm.m01, pxNz = nm.m02;
        float nxNx = -nm.m00, nxNy = -nm.m01, nxNz = -nm.m02;
        float pzNx = nm.m20, pzNy = nm.m21, pzNz = nm.m22;
        float nzNx = -nm.m20, nzNy = -nm.m21, nzNz = -nm.m22;
        float pyNx = nm.m10, pyNy = nm.m11, pyNz = nm.m12;
        float camX = (float) cam.x, camY = (float) cam.y, camZ = (float) cam.z;
        for (int i = 0, n = positions.size(); i < n; i++) {
            BlockPos pos = positions.get(i);
            float lx = pos.getX() - camX;
            float ly = pos.getY() - camY;
            float lz = pos.getZ() - camZ;
            float bX = t00 * lx + t10 * ly + t20 * lz + t30;
            float bY = t01 * lx + t11 * ly + t21 * lz + t31;
            float bZ = t02 * lx + t12 * ly + t22 * lz + t32;
            float p000x = bX, p000y = bY, p000z = bZ;
            float p100x = bX + t00, p100y = bY + t01, p100z = bZ + t02;
            float p010x = bX + t10, p010y = bY + t11, p010z = bZ + t12;
            float p110x = bX + t00 + t10, p110y = bY + t01 + t11, p110z = bZ + t02 + t12;
            float p001x = bX + t20, p001y = bY + t21, p001z = bZ + t22;
            float p101x = bX + t00 + t20, p101y = bY + t01 + t21, p101z = bZ + t02 + t22;
            float p011x = bX + t10 + t20, p011y = bY + t11 + t21, p011z = bZ + t12 + t22;
            float p111x = bX + t00 + t10 + t20, p111y = bY + t01 + t11 + t21, p111z = bZ + t02 + t12 + t22;
            consumer.vertex(p000x, p000y, p000z).color(color).normal(pxNx, pxNy, pxNz).lineWidth(lineWidth);
            consumer.vertex(p100x, p100y, p100z).color(color).normal(pxNx, pxNy, pxNz).lineWidth(lineWidth);
            consumer.vertex(p100x, p100y, p100z).color(color).normal(pzNx, pzNy, pzNz).lineWidth(lineWidth);
            consumer.vertex(p101x, p101y, p101z).color(color).normal(pzNx, pzNy, pzNz).lineWidth(lineWidth);
            consumer.vertex(p101x, p101y, p101z).color(color).normal(nxNx, nxNy, nxNz).lineWidth(lineWidth);
            consumer.vertex(p001x, p001y, p001z).color(color).normal(nxNx, nxNy, nxNz).lineWidth(lineWidth);
            consumer.vertex(p001x, p001y, p001z).color(color).normal(nzNx, nzNy, nzNz).lineWidth(lineWidth);
            consumer.vertex(p000x, p000y, p000z).color(color).normal(nzNx, nzNy, nzNz).lineWidth(lineWidth);
            consumer.vertex(p010x, p010y, p010z).color(color).normal(pxNx, pxNy, pxNz).lineWidth(lineWidth);
            consumer.vertex(p110x, p110y, p110z).color(color).normal(pxNx, pxNy, pxNz).lineWidth(lineWidth);
            consumer.vertex(p110x, p110y, p110z).color(color).normal(pzNx, pzNy, pzNz).lineWidth(lineWidth);
            consumer.vertex(p111x, p111y, p111z).color(color).normal(pzNx, pzNy, pzNz).lineWidth(lineWidth);
            consumer.vertex(p111x, p111y, p111z).color(color).normal(nxNx, nxNy, nxNz).lineWidth(lineWidth);
            consumer.vertex(p011x, p011y, p011z).color(color).normal(nxNx, nxNy, nxNz).lineWidth(lineWidth);
            consumer.vertex(p011x, p011y, p011z).color(color).normal(nzNx, nzNy, nzNz).lineWidth(lineWidth);
            consumer.vertex(p010x, p010y, p010z).color(color).normal(nzNx, nzNy, nzNz).lineWidth(lineWidth);
            consumer.vertex(p000x, p000y, p000z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
            consumer.vertex(p010x, p010y, p010z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
            consumer.vertex(p100x, p100y, p100z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
            consumer.vertex(p110x, p110y, p110z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
            consumer.vertex(p101x, p101y, p101z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
            consumer.vertex(p111x, p111y, p111z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
            consumer.vertex(p001x, p001y, p001z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
            consumer.vertex(p011x, p011y, p011z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
        }
    }

    private static void drawBoxOutlinesImpl(VertexConsumer consumer, MatrixStack.Entry entry,
            Vec3d cam, List<BlockPos> positions, int color, float lineWidth) {
        var pm = entry.getPositionMatrix();
        float t00 = pm.m00(), t01 = pm.m01(), t02 = pm.m02();
        float t10 = pm.m10(), t11 = pm.m11(), t12 = pm.m12();
        float t20 = pm.m20(), t21 = pm.m21(), t22 = pm.m22();
        float t30 = pm.m30(), t31 = pm.m31(), t32 = pm.m32();
        var nm = entry.getNormalMatrix();
        float pxNx = nm.m00, pxNy = nm.m01, pxNz = nm.m02;
        float nxNx = -nm.m00, nxNy = -nm.m01, nxNz = -nm.m02;
        float pzNx = nm.m20, pzNy = nm.m21, pzNz = nm.m22;
        float nzNx = -nm.m20, nzNy = -nm.m21, nzNz = -nm.m22;
        float pyNx = nm.m10, pyNy = nm.m11, pyNz = nm.m12;
        float camX = (float) cam.x, camY = (float) cam.y, camZ = (float) cam.z;
        for (int i = 0, n = positions.size(); i < n; i++) {
            BlockPos pos = positions.get(i);
            float lx = pos.getX() - camX;
            float ly = pos.getY() - camY;
            float lz = pos.getZ() - camZ;
            float bX = t00 * lx + t10 * ly + t20 * lz + t30;
            float bY = t01 * lx + t11 * ly + t21 * lz + t31;
            float bZ = t02 * lx + t12 * ly + t22 * lz + t32;
            float p000x = bX, p000y = bY, p000z = bZ;
            float p100x = bX + t00, p100y = bY + t01, p100z = bZ + t02;
            float p010x = bX + t10, p010y = bY + t11, p010z = bZ + t12;
            float p110x = bX + t00 + t10, p110y = bY + t01 + t11, p110z = bZ + t02 + t12;
            float p001x = bX + t20, p001y = bY + t21, p001z = bZ + t22;
            float p101x = bX + t00 + t20, p101y = bY + t01 + t21, p101z = bZ + t02 + t22;
            float p011x = bX + t10 + t20, p011y = bY + t11 + t21, p011z = bZ + t12 + t22;
            float p111x = bX + t00 + t10 + t20, p111y = bY + t01 + t11 + t21, p111z = bZ + t02 + t12 + t22;
            consumer.vertex(p000x, p000y, p000z).color(color).normal(pxNx, pxNy, pxNz).lineWidth(lineWidth);
            consumer.vertex(p100x, p100y, p100z).color(color).normal(pxNx, pxNy, pxNz).lineWidth(lineWidth);
            consumer.vertex(p100x, p100y, p100z).color(color).normal(pzNx, pzNy, pzNz).lineWidth(lineWidth);
            consumer.vertex(p101x, p101y, p101z).color(color).normal(pzNx, pzNy, pzNz).lineWidth(lineWidth);
            consumer.vertex(p101x, p101y, p101z).color(color).normal(nxNx, nxNy, nxNz).lineWidth(lineWidth);
            consumer.vertex(p001x, p001y, p001z).color(color).normal(nxNx, nxNy, nxNz).lineWidth(lineWidth);
            consumer.vertex(p001x, p001y, p001z).color(color).normal(nzNx, nzNy, nzNz).lineWidth(lineWidth);
            consumer.vertex(p000x, p000y, p000z).color(color).normal(nzNx, nzNy, nzNz).lineWidth(lineWidth);
            consumer.vertex(p010x, p010y, p010z).color(color).normal(pxNx, pxNy, pxNz).lineWidth(lineWidth);
            consumer.vertex(p110x, p110y, p110z).color(color).normal(pxNx, pxNy, pxNz).lineWidth(lineWidth);
            consumer.vertex(p110x, p110y, p110z).color(color).normal(pzNx, pzNy, pzNz).lineWidth(lineWidth);
            consumer.vertex(p111x, p111y, p111z).color(color).normal(pzNx, pzNy, pzNz).lineWidth(lineWidth);
            consumer.vertex(p111x, p111y, p111z).color(color).normal(nxNx, nxNy, nxNz).lineWidth(lineWidth);
            consumer.vertex(p011x, p011y, p011z).color(color).normal(nxNx, nxNy, nxNz).lineWidth(lineWidth);
            consumer.vertex(p011x, p011y, p011z).color(color).normal(nzNx, nzNy, nzNz).lineWidth(lineWidth);
            consumer.vertex(p010x, p010y, p010z).color(color).normal(nzNx, nzNy, nzNz).lineWidth(lineWidth);
            consumer.vertex(p000x, p000y, p000z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
            consumer.vertex(p010x, p010y, p010z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
            consumer.vertex(p100x, p100y, p100z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
            consumer.vertex(p110x, p110y, p110z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
            consumer.vertex(p101x, p101y, p101z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
            consumer.vertex(p111x, p111y, p111z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
            consumer.vertex(p001x, p001y, p001z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
            consumer.vertex(p011x, p011y, p011z).color(color).normal(pyNx, pyNy, pyNz).lineWidth(lineWidth);
        }
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
        lineSegment(consumer, entry, x1, y1, z1, x2, y1, z1, 1, 0, 0, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z1, x2, y1, z2, 0, 0, 1, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z2, x1, y1, z2, -1, 0, 0, color, lineWidth);
        lineSegment(consumer, entry, x1, y1, z2, x1, y1, z1, 0, 0, -1, color, lineWidth);

        // Top face (4 edges)
        lineSegment(consumer, entry, x1, y2, z1, x2, y2, z1, 1, 0, 0, color, lineWidth);
        lineSegment(consumer, entry, x2, y2, z1, x2, y2, z2, 0, 0, 1, color, lineWidth);
        lineSegment(consumer, entry, x2, y2, z2, x1, y2, z2, -1, 0, 0, color, lineWidth);
        lineSegment(consumer, entry, x1, y2, z2, x1, y2, z1, 0, 0, -1, color, lineWidth);

        // Vertical edges (4 edges)
        lineSegment(consumer, entry, x1, y1, z1, x1, y2, z1, 0, 1, 0, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z1, x2, y2, z1, 0, 1, 0, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z2, x2, y2, z2, 0, 1, 0, color, lineWidth);
        lineSegment(consumer, entry, x1, y1, z2, x1, y2, z2, 0, 1, 0, color, lineWidth);
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
        lineSegment(consumer, entry, x1, y1, z1, x2, y1, z1, 1, 0, 0, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z1, x2, y1, z2, 0, 0, 1, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z2, x1, y1, z2, -1, 0, 0, color, lineWidth);
        lineSegment(consumer, entry, x1, y1, z2, x1, y1, z1, 0, 0, -1, color, lineWidth);

        // Top face (4 edges)
        lineSegment(consumer, entry, x1, y2, z1, x2, y2, z1, 1, 0, 0, color, lineWidth);
        lineSegment(consumer, entry, x2, y2, z1, x2, y2, z2, 0, 0, 1, color, lineWidth);
        lineSegment(consumer, entry, x2, y2, z2, x1, y2, z2, -1, 0, 0, color, lineWidth);
        lineSegment(consumer, entry, x1, y2, z2, x1, y2, z1, 0, 0, -1, color, lineWidth);

        // Vertical edges (4 edges)
        lineSegment(consumer, entry, x1, y1, z1, x1, y2, z1, 0, 1, 0, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z1, x2, y2, z1, 0, 1, 0, color, lineWidth);
        lineSegment(consumer, entry, x2, y1, z2, x2, y2, z2, 0, 1, 0, color, lineWidth);
        lineSegment(consumer, entry, x1, y1, z2, x1, y2, z2, 0, 1, 0, color, lineWidth);
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

    private static void lineSegment(VertexConsumer consumer, MatrixStack.Entry entry,
            float x1, float y1, float z1,
            float x2, float y2, float z2,
            float nx, float ny, float nz,
            int color, float lineWidth) {
        consumer.vertex(entry, x1, y1, z1).color(color).normal(entry, nx, ny, nz).lineWidth(lineWidth);
        consumer.vertex(entry, x2, y2, z2).color(color).normal(entry, nx, ny, nz).lineWidth(lineWidth);
    }
}
