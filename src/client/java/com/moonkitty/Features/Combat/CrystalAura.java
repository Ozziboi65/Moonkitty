package com.moonkitty.Features.Combat;

import com.moonkitty.Category;
import com.moonkitty.Feature;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;

import net.minecraft.block.BlockState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.chunk.WorldChunk;

import java.util.HashMap;

public class CrystalAura extends Feature {

    private final int EXPLOSION_POWER = 6;
    private final float DIFF_MULTIPLIER = 1f;

    private float exposure;

    private MinecraftClient client;

    public float breakRange = 5;
    public float targetRange = 12;

    public float maxDamageSelf = 4;
    public float minDamageToTarget = 4;

    public CrystalAura() {
        this.name = "CrystalAura";
        this.setCategory(Category.COMBAT);
        this.feature_id = 45;
        this.setEnabled(false);
    }

    @Override
    public void tick(MinecraftClient client) {
        this.client = MinecraftClient.getInstance();

        if (!this.isEnabled())
            return;

        if (client.player == null || client.getNetworkHandler() == null || client.world == null)
            return;

        for (Entity entity : client.world.getEntities()) {
            if (entity instanceof EndCrystalEntity crystal) {
                double distance = client.player.distanceTo(crystal);
                if (distance > breakRange)
                    continue;

                Vec3d explosionPos = crystal.getBlockPos().toCenterPos();
                float selfDamage = getDamage(client.player, explosionPos);
                if (selfDamage > maxDamageSelf)
                    continue;

                for (AbstractClientPlayerEntity player : client.world.getPlayers()) {
                    if (player == client.player)
                        continue;
                    if (player.distanceTo(crystal) > targetRange)
                        continue;

                    float targetDamage = getDamage(player, explosionPos);
                    float targetHealth = player.getHealth() + player.getAbsorptionAmount();
                    if (targetDamage >= minDamageToTarget || targetDamage >= targetHealth) {

                        MinecraftClient.getInstance().interactionManager
                                .attackEntity(MinecraftClient.getInstance().player, crystal);
                    }
                }
            }
        }

    }

    float getDamage(Entity target, Vec3d explosionPos) {
        double distance = Math.sqrt(target.squaredDistanceTo(explosionPos));
        if (distance >= 12.0)
            return 0;

        float exposure = getExposure(target, explosionPos);
        double impact = (1.0 - distance / 12.0) * exposure;
        float damage = (float) (Math.floor(impact * impact + impact) * 42 + 1);

        if (target instanceof LivingEntity living) {
            damage = applyArmorReduction(living, damage);
            damage = applyProtectionReduction(living, damage);
        }

        return Math.max(0, damage);
    }

    private float applyArmorReduction(LivingEntity entity, float damage) {
        float armor = (float) entity.getAttributeValue(EntityAttributes.ARMOR);
        float toughness = (float) entity.getAttributeValue(EntityAttributes.ARMOR_TOUGHNESS);
        float reduction = Math.min(20f, Math.max(armor / 5f, armor - damage / (2f + toughness / 4f)));
        return damage * (1f - reduction / 25f);
    }

    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
    };

    private float applyProtectionReduction(LivingEntity entity, float damage) {
        int epf = 0;
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            ItemStack stack = entity.getEquippedStack(slot);
            if (stack.isEmpty())
                continue;
            ItemEnchantmentsComponent enchantments = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS,
                    ItemEnchantmentsComponent.DEFAULT);
            for (RegistryEntry<Enchantment> entry : enchantments.getEnchantments()) {
                RegistryKey<Enchantment> key = entry.getKey().orElse(null);
                if (key == null)
                    continue;
                int level = enchantments.getLevel(entry);
                if (key == Enchantments.PROTECTION) {
                    epf += level;
                } else if (key == Enchantments.BLAST_PROTECTION) {
                    epf += level * 2;
                }
            }
        }
        epf = Math.min(20, epf);
        return damage * (1f - epf / 25f);
    }

    float getExposure(Entity target, Vec3d explosionPos) {
        Box box = target.getBoundingBox();
        double stepX = 1.0 / 6.0;
        double stepY = 1.0 / 6.0;
        double stepZ = 1.0 / 6.0;

        int exposed = 0;
        int total = 0;

        HashMap<BlockPos, Boolean> blockCache = new HashMap<>();

        for (double dx = 0; dx <= 1.0; dx += stepX) {
            for (double dy = 0; dy <= 1.0; dy += stepY) {
                for (double dz = 0; dz <= 1.0; dz += stepZ) {
                    double px = MathHelper.lerp(dx, box.minX, box.maxX);
                    double py = MathHelper.lerp(dy, box.minY, box.maxY);
                    double pz = MathHelper.lerp(dz, box.minZ, box.maxZ);

                    if (!rayHitsBlock(px, py, pz, explosionPos.x, explosionPos.y, explosionPos.z, blockCache)) {
                        exposed++;
                    }
                    total++;
                }
            }
        }

        return (float) exposed / total;
    }

    private boolean rayHitsBlock(double x1, double y1, double z1,
            double x2, double y2, double z2,
            HashMap<BlockPos, Boolean> cache) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double dz = z2 - z1;
        double len = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (len < 1e-7)
            return false;

        dx /= len;
        dy /= len;
        dz /= len;

        double x = x1, y = y1, z = z1;
        double step = 0.3;

        while (true) {
            x += dx * step;
            y += dy * step;
            z += dz * step;

            double traveled = (x - x1) * dx + (y - y1) * dy + (z - z1) * dz;
            if (traveled >= len)
                return false;

            BlockPos pos = new BlockPos((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z));
            boolean opaque = cache.computeIfAbsent(pos, p -> {
                BlockState state = client.world.getBlockState(p);
                return state.isOpaqueFullCube();
            });

            if (opaque)
                return true;
        }
    }

}
