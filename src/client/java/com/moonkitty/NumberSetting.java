package com.moonkitty;

public class NumberSetting extends Setting<Double> {
    private double min;
    private double max;
    private double increment;

    public NumberSetting(String name, double defaultValue, double min, double max, double increment) {
        super(name, defaultValue);
        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getIncrement() {
        return increment;
    }

    public void increment() {
        setValue(Math.min(value + increment, max));
    }

    public void decrement() {
        setValue(Math.max(value - increment, min));
    }

    @Override
    public void setValue(Double value) {
        this.value = Math.max(min, Math.min(max, value));
    }

    public void setValueFromPercentage(double percentage) {
        percentage = Math.max(0.0, Math.min(1.0, percentage));
        double range = max - min;
        double newValue = min + (range * percentage);

        // Round to nearest increment
        newValue = Math.round(newValue / increment) * increment;
        setValue(newValue);
    }

    public double getPercentage() {
        return (value - min) / (max - min);
    }

    @Override
    public String getDisplayValue() {
        if (increment >= 1) {
            return String.format("%.0f", value);
        } else if (increment >= 0.1) {
            return String.format("%.1f", value);
        } else {
            return String.format("%.2f", value);
        }
    }
}
