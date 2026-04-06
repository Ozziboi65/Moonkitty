package com.moonkitty.Mixin;

import net.minecraft.client.model.Model;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import com.moonkitty.Util.RenderUtil;
import com.moonkitty.Features.Visuals.Chams;
import com.moonkitty.FeatureManager;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public abstract class ChamsMixin<S extends LivingEntityRenderState> {

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/RenderLayer;IIILnet/minecraft/client/texture/Sprite;ILnet/minecraft/client/render/command/ModelCommandRenderer$CrumblingOverlayCommand;)V"))
    private void redirectSubmitModel(
            OrderedRenderCommandQueue queue,
            Model model,
            Object state,
            MatrixStack matrices,
            RenderLayer renderLayer,
            int light, int overlay, int color,
            Sprite sprite, int outlineColor,
            ModelCommandRenderer.CrumblingOverlayCommand crumbling) {

        Chams chamsFeature = FeatureManager.INSTANCE.getChamsFeature();
        if (chamsFeature != null && chamsFeature.isEnabled() && chamsFeature.isRenderPlayerEnabled()
                && state instanceof PlayerEntityRenderState) {
            queue.getBatchingQueue(Integer.MAX_VALUE).submitModel(model, state, matrices, RenderUtil.CHAMS_LAYER, light,
                    overlay,
                    chamsFeature.colorPlayer, sprite, outlineColor, crumbling);
        } else {
            queue.submitModel(model, state, matrices, renderLayer, light, overlay, color, sprite, outlineColor,
                    crumbling);
        }
    }
}