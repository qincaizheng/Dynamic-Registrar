package com.qdd.dynamicregistrar.curio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class BackRender implements ICurioRenderer {
    public static final BackRender INSTANCE = new BackRender();
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!stack.isEmpty()) {
            LivingEntity entity = slotContext.entity();
            ItemStack chestArmor = entity.getItemBySlot(EquipmentSlot.CHEST);
            float z_axis = 0.0F;
            if (!chestArmor.isEmpty()) {
                z_axis = -0.0F;
            }

            matrixStack.pushPose();
            matrixStack.translate(0.0F, -0.25F, z_axis);
            matrixStack.mulPose(Axis.YP.rotationDegrees(180.0F));
            matrixStack.mulPose(Axis.XP.rotationDegrees(180.0F));
            matrixStack.scale(0.58F, 0.58F, 0.58F);
            if (entity.isCrouching()) {
                matrixStack.mulPose(Axis.XP.rotationDegrees(-30.0F));
                matrixStack.translate(0.0F, -0.2F, z_axis - 0.4F);
            }

            Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.HEAD, light, OverlayTexture.NO_OVERLAY, matrixStack, renderTypeBuffer, entity.level(), 0);
            matrixStack.popPose();
        }
    }
}
