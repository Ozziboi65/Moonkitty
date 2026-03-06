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
import com.moonkitty.Features.BoatFly;

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
    MinecraftClient client;

    // singleton optional:
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
    }

    public esp getEspFeature() {
        return espFeature;
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
