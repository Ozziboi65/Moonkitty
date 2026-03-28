package com.moonkitty.Features.Combat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moonkitty.BooleanSetting;
import com.moonkitty.ButtonSetting;
import com.moonkitty.Category;
import com.moonkitty.Feature;
import com.moonkitty.NumberSetting;
import com.moonkitty.Gui.Menu;
import com.moonkitty.Util.ConfigUtil;
import com.moonkitty.Features.Combat.KillAuraHud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.util.math.Direction;

public class TrapAura extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public static MinecraftClient client;

    public float reach = 6;

    public int attackDelay = 200;
    long lastAttackTime = 0;

    public TrapAura() {
        this.name = "Trap Aura";
        this.feature_id = 353;
        this.setCategory(Category.COMBAT);
        this.setEnabled(false);
    }

    Vec3i[] TRAP_POSITIONS = {

            new Vec3i(1, 0, 0),
            new Vec3i(-1, 0, 0),
            new Vec3i(0, 0, 1),
            new Vec3i(0, 0, -1),

            new Vec3i(1, 1, 0),
            new Vec3i(-1, 1, 0),
            new Vec3i(0, 1, 1),
            new Vec3i(0, 1, -1),

            new Vec3i(0, 2, 0),
    };

    @Override
    public void init() {
        this.client = MinecraftClient.getInstance();
        Menu menuObject = Menu.INSTANCE;

        KillAuraHud.init();

        this.reach = (float) ConfigUtil.getDouble("killAura.range", this.reach);
        this.attackDelay = ConfigUtil.getInt("killAura.delayMs", this.attackDelay);
        this.setEnabled(ConfigUtil.getBoolean("killAura.enabled", isEnabled()));

    }

    @Override
    public void tick(MinecraftClient client) {

        if (!isEnabled() || client.player == null || client.world == null)
            return;

        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {

            if (player == client.player)
                continue;

            double dx = client.player.getX() - player.getX();
            double dy = client.player.getY() - player.getY();
            double dz = client.player.getZ() - player.getZ();

            if (dx * dx + dy * dy + dz * dz > reach * reach)
                continue;

            BlockPos targetPlayerPos = BlockPos.ofFloored(player.getX(), player.getY(), player.getZ());

            ItemStack itemHolding = client.player.getMainHandStack();

            if (!(itemHolding.getItem() instanceof BlockItem)) {
                return;
            }

            for (Vec3i offset : TRAP_POSITIONS) {
                BlockPos placePos = targetPlayerPos.add(offset);

                if (!client.world.getBlockState(placePos).isAir())
                    continue;

                for (Direction face : Direction.values()) {
                    BlockPos neighbor = placePos.offset(face);
                    if (client.world.getBlockState(neighbor).isAir())
                        continue;

                    placeBlock(placePos);
                    break;
                }
            }
        }
    }

    public static void placeBlock(BlockPos targetPos) {

        Vec3d hitVec = Vec3d.ofCenter(targetPos);

        BlockHitResult hitResult = new BlockHitResult(hitVec, Direction.UP, targetPos, false);

        PlayerInteractBlockC2SPacket packet = new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, hitResult, 0);
        client.getNetworkHandler().sendPacket(packet);
    }

}