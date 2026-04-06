package com.moonkitty.config;

import com.moonkitty.FeatureManager;
import com.moonkitty.Feature;
import com.moonkitty.Setting;
import com.moonkitty.Util.FileIO;

import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ConfigManager {

    static final String CONFIG_PATH = "moonkitty/config.json";
    public static final Logger LOGGER = LoggerFactory.getLogger("moonkitty");

    static boolean dirty = false;

    public static void saveConfigSettings() {

        JsonObject root = new JsonObject();

        for (Feature feature : FeatureManager.INSTANCE.featureList) {
            String featureNameSafe = feature.name.toLowerCase().replace(" ", ".");
            JsonObject featureObj = new JsonObject();

            featureObj.addProperty("enabled", feature.isEnabled());

            for (Setting<?> setting : feature.getSettings()) {
                String settingNameSafe = setting.getName().toLowerCase().replace(" ", ".");
                Object value = setting.getValue();

                if (value instanceof Boolean)
                    featureObj.addProperty(settingNameSafe, (Boolean) value);
                else if (value instanceof Double)
                    featureObj.addProperty(settingNameSafe, (Double) value);
                else if (value instanceof Integer)
                    featureObj.addProperty(settingNameSafe, (Integer) value);
                else if (value instanceof Float)
                    featureObj.addProperty(settingNameSafe, (Float) value);
                else if (value instanceof String)
                    featureObj.addProperty(settingNameSafe, (String) value);
            }

            root.add(featureNameSafe, featureObj);
        }

        try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(root, writer);
            LOGGER.info("Config saved! :P");
        } catch (Exception e) {
            LOGGER.error("Failed to save config: ", e);
        }
    }

    public static void getConfigSettings() {

        JsonObject root = null;

        try (InputStreamReader reader = new InputStreamReader(FileIO.InputStreamFromFile(CONFIG_PATH))) {
            root = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            LOGGER.error("Failed to read config error: ", e);
        }

        if (root == null) {
            LOGGER.warn("Config is null or empty, creating a new one! :(");
            root = new JsonObject();
            dirty = true;
        }

        for (Feature feature : FeatureManager.INSTANCE.featureList) {
            String featureNameSafe = feature.name.toLowerCase().replace(" ", ".");

            JsonObject featureObj = root.getAsJsonObject(featureNameSafe);

            if (featureObj == null) {
                featureObj = new JsonObject();
                root.add(featureNameSafe, featureObj);
                dirty = true;
            }

            if (featureObj.has("enabled")) {
                boolean configEnabled = featureObj.get("enabled").getAsBoolean();
                boolean wasEnabled = feature.isEnabled();
                feature.setEnabled(configEnabled);

                if (configEnabled && wasEnabled) {
                    feature.onEnable();
                }
            } else {
                featureObj.addProperty("enabled", feature.isEnabled());
                dirty = true;
            }

            for (Setting<?> setting : feature.getSettings()) {
                String settingNameSafe = setting.getName().toLowerCase().replace(" ", ".");

                if (!featureObj.has(settingNameSafe)) {
                    Object def = setting.getDefaultValue();

                    if (def instanceof Boolean)
                        featureObj.addProperty(settingNameSafe, (Boolean) def);
                    else if (def instanceof Double)
                        featureObj.addProperty(settingNameSafe, (Double) def);
                    else if (def instanceof Integer)
                        featureObj.addProperty(settingNameSafe, (Integer) def);
                    else if (def instanceof Float)
                        featureObj.addProperty(settingNameSafe, (Float) def);
                    else if (def instanceof String)
                        featureObj.addProperty(settingNameSafe, (String) def);

                    dirty = true;
                    continue;
                }

                Object current = setting.getValue();
                if (current instanceof Boolean) {
                    ((Setting<Boolean>) setting).setValue(featureObj.get(settingNameSafe).getAsBoolean());
                } else if (current instanceof Double) {
                    ((Setting<Double>) setting).setValue(featureObj.get(settingNameSafe).getAsDouble());
                } else if (current instanceof Integer) {
                    ((Setting<Integer>) setting).setValue(featureObj.get(settingNameSafe).getAsInt());
                } else if (current instanceof Float) {
                    ((Setting<Float>) setting).setValue(featureObj.get(settingNameSafe).getAsFloat());
                } else if (current instanceof String) {
                    ((Setting<String>) setting).setValue(featureObj.get(settingNameSafe).getAsString());
                }

            }
        }

        if (dirty) {
            try (FileWriter writer = new FileWriter(CONFIG_PATH)) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(root, writer);
                LOGGER.info("Config saved in defaults! :D");
            } catch (Exception e) {
                LOGGER.error("Failed to save config: ", e);
            }
        }

    }

}