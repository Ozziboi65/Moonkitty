package com.moonkitty;

import net.minecraft.client.MinecraftClient;
import java.util.ArrayList;
import java.util.List;
import com.moonkitty.keybind.*;

public class Feature {
    public int feature_id;
    public String name;
    private boolean enabled;
    private Category category;
    protected List<Setting<?>> settings = new ArrayList<>();

    public Feature() {
        this.category = Category.MISC;
        addSetting(new KeybindSetting(this));
    }

    public Feature(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public List<Setting<?>> getSettings() {
        return settings;
    }

    protected void addSetting(Setting<?> setting) {
        settings.add(setting);
    }

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

    public void onEnable() {
    }

    public void onDisable() {
    }

    protected void onInit() {
    }

    public void tick(MinecraftClient client) {
    }
}
