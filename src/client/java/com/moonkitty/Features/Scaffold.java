package com.moonkitty.Features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.BooleanSetting;
import com.moonkitty.Category;
import com.moonkitty.Feature;
import com.moonkitty.MoonkittyClient;
import com.moonkitty.NumberSetting;
import com.moonkitty.Gui.Menu;
import com.moonkitty.bot.BotActions;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.AWTException;

import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;

public class Scaffold extends Feature {
    MinecraftClient client = MinecraftClient.getInstance();

    public Scaffold() {
        this.name = "Scaffold";
        this.setCategory(Category.MOVEMENT);
        this.feature_id = 45;
        this.setEnabled(false);
    }

    @Override
    public void init() {
        this.client = MinecraftClient.getInstance();
    }

    @Override
    public void tick(MinecraftClient client) {

        if (!this.isEnabled())
            return;

        if (client.player == null || client.getNetworkHandler() == null || client.world == null)
            return;

        ItemStack itemHolding = client.player.getMainHandStack();

        if (!(itemHolding.getItem() instanceof BlockItem)) {
            return;
        }

        BlockPos placePos = client.player.getBlockPos().down();
        Vec3d hitVec = Vec3d.ofCenter(placePos);
        BlockHitResult hitResult = new BlockHitResult(hitVec, Direction.DOWN, placePos, false);
        PlayerInteractBlockC2SPacket packet = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hitResult, 0);
        client.getNetworkHandler().sendPacket(packet);
    }
}