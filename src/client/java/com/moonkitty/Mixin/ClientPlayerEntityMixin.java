package com.moonkitty.Mixin;

import com.moonkitty.accessor.IClientPlayerEntityAccessor;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin implements IClientPlayerEntityAccessor {
    @Shadow
    private int ticksSinceLastPositionPacketSent;

    @Override
    public void moonkitty$setTicksSinceLastPositionPacketSent(int value) {
        this.ticksSinceLastPositionPacketSent = value;
    }
}
