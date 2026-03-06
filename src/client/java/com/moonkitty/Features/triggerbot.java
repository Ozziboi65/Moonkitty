package com.moonkitty.Features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.Feature;
import com.moonkitty.MoonkittyClient;
import com.moonkitty.Features.Menu.TriggerBotMenu;
import com.moonkitty.Gui.Menu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import net.minecraft.text.Text;
import net.minecraft.client.gui.widget.ButtonWidget;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.AWTException;

import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.entity.Entity;

public class triggerbot extends Feature {
    MinecraftClient client = MinecraftClient.getInstance();

    int attackDelay = 5;
    long lastAttackTime = 0;

    Robot robot;

    boolean simClick;

    public void setAttackDelayMs(int delay) {
        attackDelay = delay;
    }

    public int getAttackDelayMs() {
        return attackDelay;
    }

    public triggerbot() {
        this.name = "TRIGGERBOT";
        this.feature_id = 45;
        this.setEnabled(true);
    }

    public void setEnabledSimClick(boolean enabled) {
        if (this.simClick == enabled)
            return;
        this.simClick = enabled;
    }

    public void toggleSimClick() {
        setEnabledSimClick(!this.simClick);
    }

    public boolean getSimClick() {
        return simClick;
    }

    @Override
    public void init() {
        this.client = MinecraftClient.getInstance();
        Menu menuObject = Menu.INSTANCE;

        try {
            robot = new Robot();
        } catch (AWTException e) {
            e.printStackTrace();
        }

        menuObject.registerNewFeatureButton(
                ButtonWidget.builder(
                        Text.literal("Triggerbot"),
                        btn -> {
                            MinecraftClient.getInstance().setScreen(new TriggerBotMenu(Menu.INSTANCE));
                        }).dimensions(100, Menu.INSTANCE.getNextY(), 200, 20).build());
    }

    @Override
    public void tick(MinecraftClient client) {

        if (!this.isEnabled())
            return;

        HitResult target = client.crosshairTarget;

        long now = System.currentTimeMillis();

        if (!(now - lastAttackTime >= attackDelay))
            return;

        if (target == null)
            return;

        if (target.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityResult = (EntityHitResult) target;

            if (entityResult.getEntity() instanceof PlayerEntity player) {
                String playerName = player.getName().getString();

                if (simClick) {
                    robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
                }

                client.getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                client.getNetworkHandler().sendPacket(
                        PlayerInteractEntityC2SPacket.attack(player, client.player.isSneaking()));

                lastAttackTime = now;
            }
        }

    }
}