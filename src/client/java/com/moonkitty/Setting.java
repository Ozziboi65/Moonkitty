package com.moonkitty;

public abstract class Setting<T> {
    protected String name;
    protected T value;
    protected T defaultValue;

    public Setting(String name, T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public String getName() {
        return name;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void reset() {
        this.value = this.defaultValue;
    }

    public abstract String getDisplayValue();
}
