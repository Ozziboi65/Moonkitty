package com.moonkitty;

import net.minecraft.client.MinecraftClient;

import com.moonkitty.Feature;

import com.moonkitty.Features.esp;
import com.moonkitty.Features.freecam;
import com.moonkitty.Features.fakeplayer;
import com.moonkitty.Features.Tracer;
import com.moonkitty.Features.worldchanger;
import com.moonkitty.Features.companion;
import com.moonkitty.Features.blink;
import com.moonkitty.Features.triggerbot;
import com.moonkitty.Features.ChestEsp;
import com.moonkitty.Features.FastUse;
import com.moonkitty.Features.BoatFly;
import com.moonkitty.Features.AutoTotem;
import com.moonkitty.Features.Combat.KillAura;
import com.moonkitty.Features.Combat.MaceAura;
import com.moonkitty.Features.Search;
import com.moonkitty.Features.Combat.Criticals;
import com.moonkitty.Features.Combat.StrafeAura;
import com.moonkitty.Features.StashFinder;
import com.moonkitty.Features.Scaffold;
import com.moonkitty.Features.AutoTunneler;
import com.moonkitty.Features.Combat.TrapAura;
import com.moonkitty.Features.Combat.CrystalAura;
import com.moonkitty.Features.InventoryTweaks;
import com.moonkitty.Features.Flight;
import com.moonkitty.visuals.Particles.ButterFlies;
import com.moonkitty.visuals.BreakOverlay;

import java.util.ArrayList;
import java.util.List;

public class FeatureManager {
    private final esp espFeature;
    private final freecam freecamFeature;
    private final fakeplayer fakeplayerFeature;
    private final Tracer tracerFeature;
    private final worldchanger worldchangerFeature;
    private final companion companionFeature;
    private final blink blinkFeature;
    private final triggerbot triggerbotFeature;
    private final ChestEsp chestespFeature;
    private final BoatFly boatflyFeature;
    private final AutoTotem autototemFeature;
    private final KillAura killAuraFeature;
    private final Search searchFeature;
    private final Criticals criticalsFeature;
    private final StrafeAura strafeFeature;
    private final StashFinder stashFinderFeature;
    private final Scaffold scaffoldFeature;
    private final AutoTunneler autoTunnelerFeature;
    private final TrapAura trapAuraFeature;
    private final CrystalAura crystalAuraFeature;
    private final InventoryTweaks inventoryTweaksFeature;
    private final FastUse fastUseFeature;
    private final Flight flightFeature;
    private final ButterFlies butterFliesFeature;
    private final MaceAura maceAuraFeature;
    private final BreakOverlay breakOverlayFeature;
    MinecraftClient client;

    public static final FeatureManager INSTANCE = new FeatureManager();

    public List<Feature> featureList = new ArrayList<>();

    public FeatureManager() {
        this.espFeature = new esp();
        this.freecamFeature = new freecam();
        this.fakeplayerFeature = new fakeplayer();
        this.tracerFeature = new Tracer();
        this.worldchangerFeature = new worldchanger();
        this.companionFeature = new companion();
        this.blinkFeature = new blink();
        this.triggerbotFeature = new triggerbot();
        this.chestespFeature = new ChestEsp();
        this.boatflyFeature = new BoatFly();
        this.autototemFeature = new AutoTotem();
        this.killAuraFeature = new KillAura();
        this.searchFeature = new Search();
        this.criticalsFeature = new Criticals();
        this.strafeFeature = new StrafeAura();
        this.stashFinderFeature = new StashFinder();
        this.scaffoldFeature = new Scaffold();
        this.autoTunnelerFeature = new AutoTunneler();
        this.trapAuraFeature = new TrapAura();
        this.crystalAuraFeature = new CrystalAura();
        this.inventoryTweaksFeature = new InventoryTweaks();
        this.fastUseFeature = new FastUse();
        this.flightFeature = new Flight();
        this.butterFliesFeature = new ButterFlies();
        this.maceAuraFeature = new MaceAura();
        this.breakOverlayFeature = new BreakOverlay();

        featureList.add(espFeature);
        featureList.add(freecamFeature);
        featureList.add(fakeplayerFeature);
        featureList.add(tracerFeature);
        featureList.add(worldchangerFeature);
        featureList.add(companionFeature);
        featureList.add(blinkFeature);
        featureList.add(triggerbotFeature);
        featureList.add(chestespFeature);
        featureList.add(boatflyFeature);
        featureList.add(autototemFeature);
        featureList.add(killAuraFeature);
        featureList.add(searchFeature);
        featureList.add(criticalsFeature);
        featureList.add(strafeFeature);
        featureList.add(stashFinderFeature);
        featureList.add(scaffoldFeature);
        featureList.add(autoTunnelerFeature);
        featureList.add(trapAuraFeature);
        featureList.add(crystalAuraFeature);
        featureList.add(inventoryTweaksFeature);
        featureList.add(fastUseFeature);
        featureList.add(flightFeature);
        featureList.add(butterFliesFeature);
        featureList.add(maceAuraFeature);
        featureList.add(breakOverlayFeature);
    }

    public esp getEspFeature() {
        return espFeature;
    }

    public Flight getFlightFeature() {
        return flightFeature;
    }

    public InventoryTweaks getInventoryTweaksFeature() {
        return inventoryTweaksFeature;
    }

    public FastUse getFastUseFeature() {
        return fastUseFeature;
    }

    public BreakOverlay getBreakOverlayFeature() {
        return breakOverlayFeature;
    }

    public StashFinder getStashFinderFeature() {
        return stashFinderFeature;
    }

    public Scaffold getScaffoldFeature() {
        return scaffoldFeature;
    }

    public AutoTunneler getAutoTunnelerFeature() {
        return autoTunnelerFeature;
    }

    public TrapAura getTrapAuraFeature() {
        return trapAuraFeature;
    }

    public Criticals getCritsFeature() {
        return criticalsFeature;
    }

    public freecam getFreecamFeature() {
        return freecamFeature;
    }

    public Tracer getTracerFeature() {
        return tracerFeature;
    }

    public companion getCompanionFeature() {
        return companionFeature;
    }

    public worldchanger getWorldchangerFeature() {
        return worldchangerFeature;
    }

    public StrafeAura getStrafeAuraFeature() {
        return strafeFeature;
    }

    public blink getBlinkFeature() {
        return blinkFeature;
    }

    public fakeplayer getFakeplayerFeature() {
        return fakeplayerFeature;
    }

    public ChestEsp getChestEspFeature() {
        return chestespFeature;
    }

    public triggerbot getTriggerbotFeature() {
        return triggerbotFeature;
    }

    public BoatFly getBoatFlyFeature() {
        return boatflyFeature;
    }

    public KillAura getKillAuraFeature() {
        return killAuraFeature;
    }

    public Search getSearchFeature() {
        return searchFeature;
    }

    public AutoTotem getAutoTotemFeature() {
        return autototemFeature;
    }

    public void registerFeature(Feature feature) {
        featureList.add(feature);
    }

    public void tick(MinecraftClient client) {

        for (Feature feature : featureList) {
            feature.tick(client);
        }
    }

    public void Init() {

        for (Feature feature : featureList) {
            feature.init();
            feature.onInit();
        }
    }
}
