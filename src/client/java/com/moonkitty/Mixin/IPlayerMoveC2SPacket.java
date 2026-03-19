package com.moonkitty.Mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

@Mixin(PlayerMoveC2SPacket.class)
public interface IPlayerMoveC2SPacket {

    @Accessor("yaw")
    float getYaw();

    @Accessor("pitch")
    float getPitch();

    @Accessor("x")
    double getX();

    @Accessor("y")
    double getY();

    @Accessor("z")
    double getZ();

    @Accessor("onGround")
    boolean getOnGround();

    @Accessor("horizontalCollision")
    boolean getHorizontalCollision();
}