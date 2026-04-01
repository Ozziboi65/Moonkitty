package com.moonkitty.Features.Combat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.BooleanSetting;
import com.moonkitty.Category;
import com.moonkitty.Feature;
import com.moonkitty.MoonkittyClient;
import com.moonkitty.NumberSetting;
import com.moonkitty.Gui.Menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MaceItem;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.AbstractClientPlayerEntity;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.AWTException;

import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.entity.Entity;

public class MaceDamage extends Feature {
    MinecraftClient client = MinecraftClient.getInstance();

    public int reach = 5;

    public int attackDelay = 200;
    long lastAttackTime = 0;

    private final float BLOCK_PER_STEP = 1.0f;
    private int steps = 8;

    Vec3d orginalPos = new Vec3d(0, 0, 0);

    private NumberSetting blockCountSetting;

    public MaceDamage() {
        this.name = "Mace Damage";
        this.setCategory(Category.COMBAT);
        this.feature_id = 45;
        this.setEnabled(false);

        blockCountSetting = new NumberSetting("Spoof By (blocks)", 8.0, 1.0, 55.0, 1.0);
        addSetting(blockCountSetting);
    }

    @Override
    public void init() {
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public void tick(MinecraftClient client) {
        if (!isEnabled() || client.player == null || client.world == null)
            return;

        steps = blockCountSetting.getValue().intValue();
        ItemStack itemHolding = client.player.getMainHandStack();

        if (!(itemHolding.getItem() instanceof MaceItem)) {
            return;
        }

        long now = System.currentTimeMillis();

        if (!(now - lastAttackTime >= attackDelay)) {
            return;
        }

        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
            if (player == client.player) {
                continue;
            }

            double dx = client.player.getX() - player.getX();
            double dy = client.player.getY() - player.getY();
            double dz = client.player.getZ() - player.getZ();

            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq > reach * reach) {
                continue;
            }

            orginalPos = new Vec3d(client.player.getX(), client.player.getY(), client.player.getZ());
            Vec3d pos = orginalPos;

            for (int i = 0; i < steps; i++) {
                pos = pos.add(0, BLOCK_PER_STEP, 0);
                client.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        pos,
                        false,
                        false));
            }

            for (int i = 0; i < steps - 1; i++) {
                pos = pos.add(0, -BLOCK_PER_STEP, 0);
                client.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                        pos,
                        false,
                        false));
            }

            client.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
            client.getNetworkHandler().sendPacket(
                    PlayerInteractEntityC2SPacket.attack(player, client.player.isSneaking()));

            client.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(
                    orginalPos,
                    true,
                    false));

            lastAttackTime = now;
            break;
        }
    }
}
