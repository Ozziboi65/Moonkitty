package com.moonkitty.Features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.BooleanSetting;
import com.moonkitty.Category;
import com.moonkitty.Feature;
import com.moonkitty.FeatureManager;
import com.moonkitty.Gui.Menu;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import com.moonkitty.Util.RenderUtil;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Tracer extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");

    public boolean renderPlayer = true;
    public boolean renderStash = true;
    public boolean renderChest = true;
    public boolean renderEnderChest = true;
    public boolean renderShulker = true;

    public int playerColor = 0xFFFF0000;
    public int chestColor = 0xFFFFDD54;
    public int stashColor = 0xFFff00c8;
    public int enderChestColor = 0xFF00FFFF;
    public int shulkerColor = 0xFF8A17FF;

    private BooleanSetting stashSetting;
    private BooleanSetting playerSetting;
    private BooleanSetting chestSetting;
    private BooleanSetting enderChestSetting;
    private BooleanSetting shulkerSetting;

    public Tracer() {
        this.name = "Tracers";
        this.feature_id = 85;
        this.setCategory(Category.RENDER);
        this.setEnabled(true);

        stashSetting = new BooleanSetting("Show Detected Stashes", true);
        addSetting(stashSetting);

        playerSetting = new BooleanSetting("Show Players", true);
        addSetting(playerSetting);

        chestSetting = new BooleanSetting("Show Chests", true);
        addSetting(chestSetting);

        enderChestSetting = new BooleanSetting("Show Enderchests", true);
        addSetting(enderChestSetting);

        shulkerSetting = new BooleanSetting("Show ShulkerBoxes", true);
        addSetting(shulkerSetting);
    }

    @Override
    public void tick(MinecraftClient client) {
        renderStash = stashSetting.getValue();
        renderPlayer = playerSetting.getValue();
        renderChest = chestSetting.getValue();
        renderEnderChest = enderChestSetting.getValue();
        renderShulker = shulkerSetting.getValue();
    }

    @Override
    public void init() {
        MinecraftClient client = MinecraftClient.getInstance();

        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(context -> {
            if (!isEnabled() || client.player == null || client.world == null)
                return;

            if (context.consumers() == null)
                return;

            float frameTickDelta = client.gameRenderer.getCamera().getLastTickProgress();
            Vec3d cam = client.gameRenderer.getCamera().getCameraPos();
            MatrixStack matrices = new MatrixStack();
            MatrixStack.Entry entry = matrices.peek();
            VertexConsumer consumer = RenderUtil.getLineConsumer(context.consumers());

            Vec3d eyePos = client.player.getLerpedPos(frameTickDelta).add(0, client.player.getStandingEyeHeight(), 0);
            Vec3d lookDirection = client.player.getRotationVec(frameTickDelta);
            Vec3d origin = eyePos.add(lookDirection.multiply(0.3));

            if (renderStash) {
                for (BlockPos block : FeatureManager.INSTANCE.getStashFinderFeature().tracerTargets) { // :3
                    Vec3d targetPos = new Vec3d(block.getX(), block.getY(), block.getZ());
                    RenderUtil.drawLine(consumer, entry, cam, origin, targetPos, stashColor, 6.0f);
                }
            }

            if (renderChest) {
                for (ChestBlockEntity chest : FeatureManager.INSTANCE.getChestEspFeature().chestList) {
                    RenderUtil.drawLine(consumer, entry, cam, origin, Vec3d.ofCenter(chest.getPos()), chestColor, 1.0f);
                }
            }

            if (renderShulker) {
                for (ShulkerBoxBlockEntity chest : FeatureManager.INSTANCE.getChestEspFeature().shulkerList) {
                    RenderUtil.drawLine(consumer, entry, cam, origin, Vec3d.ofCenter(chest.getPos()), shulkerColor,
                            1.0f);
                }
            }

            if (renderEnderChest) {
                for (EnderChestBlockEntity chest : FeatureManager.INSTANCE.getChestEspFeature().enderChestList) {
                    RenderUtil.drawLine(consumer, entry, cam, origin, Vec3d.ofCenter(chest.getPos()), enderChestColor,
                            1.0f);
                }
            }

            if (renderPlayer) {
                for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
                    if (player == client.player)
                        continue;
                    RenderUtil.drawLine(consumer, entry, cam, origin, player.getLerpedPos(frameTickDelta), playerColor,
                            1.0f);
                }
            }
        });
    }
}