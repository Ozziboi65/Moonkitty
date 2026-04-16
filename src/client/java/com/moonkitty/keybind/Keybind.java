package com.moonkitty.keybind;

import com.moonkitty.Feature;

public class Keybind {

    private Feature feature;

    private int bind = 88;

    public Keybind(Feature targetFeature, int keyCode) {

        this.feature = targetFeature;

        this.bind = keyCode;

    }

    public void setBind(int newKey) {

        this.bind = newKey;

    }

    public Feature getOwner() {

        return feature;

    }

    public int getBind() {

        return this.bind;

    }

}