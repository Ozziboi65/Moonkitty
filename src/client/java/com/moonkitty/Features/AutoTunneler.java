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

public class AutoTunneler extends Feature {
    MinecraftClient client = MinecraftClient.getInstance();

    private BooleanSetting walkSetting;
    private BooleanSetting mineSetting;

    boolean autoWalk;
    boolean autoMine;

    public AutoTunneler() {
        this.name = "AutoTunneler";
        this.setCategory(Category.DONUT);
        this.feature_id = 45;
        this.setEnabled(false);

        walkSetting = new BooleanSetting("Walk", true);
        addSetting(walkSetting);

        mineSetting = new BooleanSetting("Mine", true);
        addSetting(mineSetting);
    }

    @Override
    public void init() {
        this.client = MinecraftClient.getInstance();
    }

    public void onDisable() {
        client.options.forwardKey.setPressed(false);
        client.options.attackKey.setPressed(false);
    }

    @Override
    public void tick(MinecraftClient client) {

        if (!this.isEnabled())
            return;

        if (client.player == null || client.getNetworkHandler() == null || client.world == null)
            return;

        autoWalk = walkSetting.getValue();
        autoMine = mineSetting.getValue();

        if (autoWalk) {
            client.options.forwardKey.setPressed(true);
        }

        if (autoMine) {
            client.options.attackKey.setPressed(true);
        }
    }
}