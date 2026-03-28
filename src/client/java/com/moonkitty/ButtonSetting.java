package com.moonkitty;

public class ButtonSetting extends Setting<Void> {
    private final Runnable action;

    public ButtonSetting(String name, Runnable action) {
        super(name, null);
        this.action = action;
    }

    public void execute() {
        if (action != null) {
            action.run();
        }
    }

    @Override
    public String getDisplayValue() {
        return "Click";
    }
}
