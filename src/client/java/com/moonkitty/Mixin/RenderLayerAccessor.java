package com.moonkitty.Mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderSetup;

@Mixin(RenderLayer.class)
public interface RenderLayerAccessor {

    @Invoker("of")
    static RenderLayer invokeOf(String name, RenderSetup setup) {
        throw new AssertionError();
    }
}
