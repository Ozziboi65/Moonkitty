package com.moonkitty.Mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayerInteractionManager.class)
public interface ClientPlayerInteractionManagerAccessor {

    @Accessor("currentBreakingPos")
    BlockPos getCurrentBreakingPos();

    @Accessor("currentBreakingProgress")
    float getCurrentBreakingProgress();

    @Accessor("breakingBlock")
    boolean isBreakingBlock();
}