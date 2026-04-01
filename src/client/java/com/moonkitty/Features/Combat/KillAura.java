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
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

public class KillAura extends Feature {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    public static MinecraftClient McClient;

    private NumberSetting reachSetting;
    private NumberSetting delaySetting;
    private BooleanSetting swingSetting;
    private BooleanSetting visibleSetting;
    private BooleanSetting readySetting;

    public float reach = 6;

    public int attackDelay = 200;
    long lastAttackTime = 0;

    public boolean swingHand;
    public boolean checkVisible;
    public boolean lookAtTarget = true;
    public boolean attackWhenReady;

    public float[] rot;

    public AbstractClientPlayerEntity currentTarget = null;

    public KillAura() {
        this.name = "Kill Aura";
        this.feature_id = 353;
        this.setCategory(Category.COMBAT);
        this.setEnabled(false);

        // Initialize settings
        reachSetting = new NumberSetting("Reach", 6.0, 3.0, 10.0, 0.5);
        delaySetting = new NumberSetting("Delay", 200.0, 0.0, 1000.0, 50.0);
        swingSetting = new BooleanSetting("Swing", false);
        visibleSetting = new BooleanSetting("Check Visible", false);
        readySetting = new BooleanSetting("Attack When Ready", true);

        addSetting(reachSetting);
        addSetting(delaySetting);
        addSetting(swingSetting);
        addSetting(visibleSetting);
        addSetting(readySetting);
    }

    public void setReach(float newReach) {
        reach = newReach;
    }

    public float getReach() {
        return reach;
    }

    public void setDelayMs(int newDelay) {
        attackDelay = newDelay;
    }

    public int getDelayMs() {
        return attackDelay;
    }

    public boolean isSwing() {
        return swingHand;
    }

    public void setSwing(boolean enabled) {
        if (this.swingHand == enabled)
            return;
        this.swingHand = enabled;
    }

    public void toggleSwing() {
        setSwing(!this.swingHand);
    }

    public boolean isVis() {
        return checkVisible;
    }

    public void setVis(boolean enabled) {
        if (this.checkVisible == enabled)
            return;
        this.checkVisible = enabled;
    }

    public void toggleVis() {
        setVis(!this.checkVisible);
    }

    @Override
    public void init() {
        this.McClient = MinecraftClient.getInstance();
        Menu menuObject = Menu.INSTANCE;

        rot = new float[2];

        KillAuraHud.init();

        this.reach = (float) ConfigUtil.getDouble("killAura.range", this.reach);
        this.attackDelay = ConfigUtil.getInt("killAura.delayMs", this.attackDelay);
        this.setEnabled(ConfigUtil.getBoolean("killAura.enabled", isEnabled()));

    }

    @Override
    public void tick(MinecraftClient client) {
        // Update values from settings
        reach = reachSetting.getValue().floatValue();
        attackDelay = delaySetting.getValue().intValue();
        swingHand = swingSetting.getValue();
        checkVisible = visibleSetting.getValue();
        attackWhenReady = readySetting.getValue();

        if (!isEnabled() || client.player == null || client.world == null)
            return;

        currentTarget = null;

        for (AbstractClientPlayerEntity player : client.world.getPlayers()) {

            if (player == client.player) {
                continue;
            }

            double dx = client.player.getX() - player.getX();
            double dy = client.player.getY() - player.getY();
            double dz = client.player.getZ() - player.getZ();

            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq > reach * reach) {
                continue;
            }

            Vec3d start = client.player.getEyePos();
            Vec3d end = player.getEyePos();

            RaycastContext context = new RaycastContext(
                    start,
                    end,
                    RaycastContext.ShapeType.OUTLINE,
                    RaycastContext.FluidHandling.NONE,
                    client.player);

            BlockHitResult hit = client.world.raycast(context);

            boolean visible = hit.getType() == HitResult.Type.MISS ||
                    hit.getPos().distanceTo(start) >= start.distanceTo(end);

            if (checkVisible && !visible) {
                continue;
            }
            currentTarget = player;

            if (lookAtTarget) {
                rot = getRotationForAim(client.player, player);
            }

            KillAuraHud.target = player;

            if (attackWhenReady) {
                float delay = client.player.getAttackCooldownProgress(0.5f);
                if (delay >= 1.0f) {
                    attackPlayer(player);
                    continue;
                }

                continue;
            }

            long now = System.currentTimeMillis();
            if (now - lastAttackTime >= attackDelay) {
                attackPlayer(player);
                lastAttackTime = now;
            }
            return;
        }
    }

    public void attackPlayer(AbstractClientPlayerEntity target) {

        if (swingHand) {
            MinecraftClient.getInstance().player.swingHand(Hand.MAIN_HAND);
        }

        MinecraftClient.getInstance().getNetworkHandler().sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
        MinecraftClient.getInstance().interactionManager.attackEntity(MinecraftClient.getInstance().player, target);

    }

    public static float[] getRotationForAim(ClientPlayerEntity player, Entity target) {
        double dx = target.getX() - player.getX();
        double dz = target.getZ() - player.getZ();
        double targetY = target.getY() + target.getHeight() * 0.5;// center
        double playerY = player.getY() + player.getEyeHeight(player.getPose());
        double dy = targetY - playerY;
        double distXZ = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
        float pitch = (float) (-Math.toDegrees(Math.atan2(dy, distXZ)));
        return new float[] { yaw, pitch };
    }

}