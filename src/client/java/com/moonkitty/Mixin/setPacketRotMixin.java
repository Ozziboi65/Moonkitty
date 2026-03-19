package com.moonkitty.Mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.network.packet.Packet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import com.moonkitty.Features.Combat.KillAura;
import com.moonkitty.FeatureManager;
import com.moonkitty.Mixin.IPlayerMoveC2SPacket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mixin(ClientCommonNetworkHandler.class)
public class setPacketRotMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    KillAura feature = FeatureManager.INSTANCE.getKillAuraFeature();

    @ModifyVariable(method = "sendPacket", at = @At("HEAD"), argsOnly = true)
    private Packet<?> modifyPacket(Packet<?> packet) {
        if (!feature.isEnabled())
            return packet;

        if (feature.currentTarget == null)
            return packet;

        if (!(packet instanceof PlayerMoveC2SPacket move))
            return packet;

        IPlayerMoveC2SPacket iMove = (IPlayerMoveC2SPacket) move;
        PlayerMoveC2SPacket newPacket;

        if (packet instanceof PlayerMoveC2SPacket.PositionAndOnGround) {
            newPacket = new PlayerMoveC2SPacket.Full(
                    iMove.getX(),
                    iMove.getY(),
                    iMove.getZ(),
                    feature.rot[0],
                    feature.rot[1],
                    iMove.getOnGround(),
                    iMove.getHorizontalCollision());
        } else if (packet instanceof PlayerMoveC2SPacket.Full) {
            newPacket = new PlayerMoveC2SPacket.Full(
                    iMove.getX(),
                    iMove.getY(),
                    iMove.getZ(),
                    feature.rot[0],
                    feature.rot[1],
                    iMove.getOnGround(),
                    iMove.getHorizontalCollision());
        } else if (packet instanceof PlayerMoveC2SPacket.LookAndOnGround) {
            newPacket = new PlayerMoveC2SPacket.LookAndOnGround(
                    feature.rot[0],
                    feature.rot[1],
                    iMove.getOnGround(),
                    iMove.getHorizontalCollision());
        } else {
            return packet;
        }

        return newPacket;
    }

}
