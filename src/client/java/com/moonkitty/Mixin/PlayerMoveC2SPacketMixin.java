package com.moonkitty.Mixin;

import com.moonkitty.accessor.IPlayerMoveC2SPacketMutable;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerMoveC2SPacket.class)
public class PlayerMoveC2SPacketMixin implements IPlayerMoveC2SPacketMutable {
    @Shadow
    @Mutable
    protected double y;

    @Override
    @Unique
    public void moonkitty$setY(double y) {
        this.y = y;
    }
}