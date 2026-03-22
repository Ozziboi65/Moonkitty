package com.moonkitty.Features;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.Feature;

import net.minecraft.client.MinecraftClient;
import net.minecraft.world.World;
import com.moonkitty.Gui.Menu;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import com.moonkitty.Feature;
import com.moonkitty.FeatureManager;
import com.moonkitty.Features.Menu.AutoTotemMenu;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.util.Window;
import net.minecraft.item.Items;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.text.Text;

import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoTotem extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public MinecraftClient McClient;

    long lastMoveTime = 0;

    long detectionTime = 0;

    int pendingHandlerSlot = -1;

    int delay = 200;

    public boolean disableInContainers = true;

    public AutoTotem() {
        this.name = "AutoTotem";
        this.feature_id = 4567;
        this.setEnabled(true);
    }

    public boolean getDisableContainer() {
        return disableInContainers;
    }

    public void setContainer(boolean enabled) {
        if (this.disableInContainers == enabled)
            return;
        this.disableInContainers = enabled;
    }

    public void toggleContainer() {
        setContainer(!this.disableInContainers);
    }

    @Override
    public void init() {
        this.McClient = MinecraftClient.getInstance();
        Menu menuObject = Menu.INSTANCE;

        menuObject.registerNewFeatureButton(
                ButtonWidget.builder(
                        Text.literal("Auto Totem"),
                        btn -> {
                            MinecraftClient.getInstance().setScreen(new AutoTotemMenu(Menu.INSTANCE));
                        }).dimensions(100, Menu.INSTANCE.getNextY(), 200, 20).build());
    }

    @Override
    public void tick(MinecraftClient client) {
        if (client.player == null)
            return;

        if (!this.isEnabled())
            return;

        if (client.player.getOffHandStack().getItem() == Items.TOTEM_OF_UNDYING)
            return;

        if (client.currentScreen instanceof HandledScreen<?> && disableInContainers) {
            return;
        }

        long now = System.currentTimeMillis();
        int invSlot = -1;

        for (int i = 0; i < client.player.getInventory().size(); i++) {
            if (client.player.getInventory().getStack(i).getItem() == Items.TOTEM_OF_UNDYING) {
                invSlot = i;
                break;
            }
        }

        if (invSlot != -1) {
            int handlerSlot = -1;
            for (int i = 0; i < client.player.currentScreenHandler.slots.size(); i++) {
                net.minecraft.screen.slot.Slot s = client.player.currentScreenHandler.slots.get(i);
                if (s.inventory == client.player.getInventory() && s.getIndex() == invSlot) {
                    handlerSlot = i;
                    break;
                }
            }
            if (handlerSlot == -1) {

                pendingHandlerSlot = -1;
                detectionTime = 0;
            } else {

                if (pendingHandlerSlot == -1 || pendingHandlerSlot != handlerSlot) {
                    pendingHandlerSlot = handlerSlot;
                    detectionTime = now;
                    return;
                }

                if (now - detectionTime >= delay) {
                    client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, handlerSlot, 0,
                            SlotActionType.PICKUP, client.player);
                    client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, 45, 0,
                            SlotActionType.PICKUP, client.player);
                    client.interactionManager.clickSlot(client.player.currentScreenHandler.syncId, handlerSlot, 0,
                            SlotActionType.PICKUP, client.player);
                    lastMoveTime = now;
                    pendingHandlerSlot = -1;
                    detectionTime = 0;
                }
            }
        }
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int wantedDelay) {
        delay = wantedDelay;
    }

}