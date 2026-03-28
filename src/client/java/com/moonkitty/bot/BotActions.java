package com.moonkitty.bot;

import com.moonkitty.Feature;
import net.minecraft.client.MinecraftClient;

import net.minecraft.network.packet.c2s.play.*;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;

public class BotActions extends Feature {
    static private MinecraftClient client = MinecraftClient.getInstance();

    @Override
    public void init() {
        client = MinecraftClient.getInstance();
    }

    public static void silentInteractWithBlockPos(BlockPos targetpos) {
        if (client == null || client.getNetworkHandler() == null || client.world == null) {
            return;
        }

        BlockState state = client.world.getBlockState(targetpos);
        Direction face = Direction.NORTH;

        if (state.contains(Properties.FACING)) {
            face = state.get(Properties.FACING);
        } else if (state.contains(Properties.HORIZONTAL_FACING)) {
            face = state.get(Properties.HORIZONTAL_FACING);
        }

        Vec3d hitVec = Vec3d.ofCenter(targetpos);
        BlockHitResult hitResult = new BlockHitResult(hitVec, face, targetpos, false);

        PlayerInteractBlockC2SPacket packet = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hitResult, 0);

        client.getNetworkHandler().sendPacket(packet);
    }
}