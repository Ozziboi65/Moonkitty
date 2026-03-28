package com.moonkitty;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name, Boolean defaultValue) {
        super(name, defaultValue);
    }

    public void toggle() {
        this.value = !this.value;
    }

    @Override
    public String getDisplayValue() {
        return value ? "ON" : "OFF";
    }
}
