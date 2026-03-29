package com.moonkitty.Features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.BooleanSetting;
import com.moonkitty.Category;
import com.moonkitty.Feature;
import com.moonkitty.FeatureManager;
import com.moonkitty.NumberSetting;
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

public class FastUse extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public int speed = 1;
    public boolean onlyBlock;

    private NumberSetting speedSetting;
    private BooleanSetting onlyBlockSetting;

    public MinecraftClient client;

    public int getSpeed() {
        return speed;
    }

    @Override
    public void init() {
        this.client = MinecraftClient.getInstance();
    }

    public FastUse() {
        this.name = "FastUse";
        this.feature_id = 85;
        this.setCategory(Category.PLAYER);
        this.setEnabled(false);

        speedSetting = new NumberSetting("Speed", 1.0, 0.0, 10.0, 1.0);
        addSetting(speedSetting);

        onlyBlockSetting = new BooleanSetting("Only Blocks", true);
        addSetting(onlyBlockSetting);
    }

    @Override
    public void tick(MinecraftClient client) {
        if (!this.isEnabled())
            return;

        speed = speedSetting.getValue().intValue();
        onlyBlock = onlyBlockSetting.getValue();
    }
}