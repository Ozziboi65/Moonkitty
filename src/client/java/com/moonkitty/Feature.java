package com.moonkitty;

import net.minecraft.client.MinecraftClient;

public class Feature {
    public int feature_id;
    public String name;
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this.enabled == enabled)
            return;
        this.enabled = enabled;
        if (enabled)
            onEnable();
        else
            onDisable();
    }

    public void toggle() {
        setEnabled(!this.enabled);
    }

    protected void init() {
    }

    protected void onEnable() {
    }

    protected void onDisable() {
    }

    protected void onInit() {
    }

    public void tick(MinecraftClient client) {
    }
}
