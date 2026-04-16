package com.moonkitty.keybind;

import com.moonkitty.FeatureManager;
import com.moonkitty.Feature;
import com.moonkitty.Setting;
import com.moonkitty.Util.FileIO;

import java.io.InputStreamReader;
import com.moonkitty.keybind.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

public class KeybindManager {
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");
    private static List<Keybind> bindList = new ArrayList<>();

    public static List<Keybind> getKeybindList() {
        return bindList;
    }

    public static void registerKeybind(Keybind keybind) {
        bindList.add(keybind);
        LOGGER.info("registered new bind :D, object: {}", keybind);
    }

    public static Feature getFeatureByKeyCode(int code) {
        for (Keybind keybind : bindList) {
            if (keybind.getBind() == code) {
                return keybind.getOwner();
            }
        }
        return null;
    }
}