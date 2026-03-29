package com.moonkitty.Features;

import com.moonkitty.Feature;

import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;

import com.moonkitty.Category;

public class InventoryTweaks extends Feature {

    static MinecraftClient client = null;

    private static final int INVENTORY_MAX_SLOT = 35;

    public static int delayMs = 40;
    private static long lastMoveTime = 0;

    public InventoryTweaks() {
        this.name = "Inventory Tweaks";
        this.setCategory(Category.RENDER);
        this.feature_id = 45;

    }

    @Override
    public void init() {
        this.client = MinecraftClient.getInstance();
    }

    private static void clickSlot(int slot, int button, SlotActionType actionType) {
        if (client.player == null || client.interactionManager == null)
            return;

        client.interactionManager.clickSlot(
                client.player.currentScreenHandler.syncId,
                slot,
                button,
                actionType,
                client.player);
    }

    public static void stealAll() {
        if (client.player != null
                && client.player.currentScreenHandler instanceof GenericContainerScreenHandler handler) {
            for (int i = 0; i < handler.getRows() * 9; i++) {
                ItemStack stack = handler.getSlot(i).getStack();
                clickSlot(i, 0, SlotActionType.QUICK_MOVE);

            }
        }
    }

    public static void storeAll() {
        Inventory inventory = client.player.getInventory();

        for (int i = 9; i < INVENTORY_MAX_SLOT; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                clickSlot(i, 0, SlotActionType.QUICK_MOVE);
            }
        }
    }

}