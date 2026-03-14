package com.moonkitty.Util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.loader.api.FabricLoader;

import net.fabricmc.loader.api.FabricLoader;

public class ConfigUtil {

    public static String path = "config/config.json";

    public static final String MOD_ID = "moonkitty";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static Path CONFIG_PATH = null;

    public static JsonObject config;

    public static void init() {
        try {
            LOGGER.info("Beginning init of config");

            ensureConfigExists();

            config = loadConfig();
            LOGGER.info("Config loaded: " + (config == null ? "null" : "ok"));

            JsonObject defaults = createDefaults();
            mergeDefaults(config, defaults);
            saveConfig(config);
        } catch (IOException e) {
            LOGGER.error("Error while loading config", e);
        }
    }

    public static JsonObject getConfig() {
        if (config == null) {
            try {
                config = loadConfig();
            } catch (IOException e) {
                LOGGER.error("Error loading config", e);
                config = new JsonObject();
            }
        }
        return config;
    }

    private static JsonObject createDefaults() {
        JsonObject defaults = new JsonObject();

        JsonObject killAura = new JsonObject();

        JsonObject triggerbot = new JsonObject();

        killAura.addProperty("enabled", false);
        killAura.addProperty("range", 4.5);
        killAura.addProperty("delayMs", 200);
        defaults.add("killAura", killAura);

        triggerbot.addProperty("enabled", false);
        triggerbot.addProperty("range", 4.5);
        triggerbot.addProperty("delayMs", 200);
        defaults.add("killAura", triggerbot);

        return defaults;
    }

    private static void mergeDefaults(JsonObject target, JsonObject defaults) {
        for (Map.Entry<String, JsonElement> entry : defaults.entrySet()) {
            String key = entry.getKey();
            JsonElement val = entry.getValue();
            if (!target.has(key)) {
                target.add(key, val);
            } else if (val.isJsonObject() && target.get(key).isJsonObject()) {
                mergeDefaults(target.getAsJsonObject(key), val.getAsJsonObject());
            }
        }
    }

    private static JsonElement getByPath(String path) {
        if (path == null || path.isEmpty())
            return null;
        String[] parts = path.split("\\\\.");
        JsonElement cur = getConfig();
        for (String p : parts) {
            if (cur == null || !cur.isJsonObject())
                return null;
            JsonObject obj = cur.getAsJsonObject();
            if (!obj.has(p))
                return null;
            cur = obj.get(p);
        }
        return cur;
    }

    public static boolean getBoolean(String path, boolean def) {
        JsonElement el = getByPath(path);
        try {
            return el != null && !el.isJsonNull() ? el.getAsBoolean() : def;
        } catch (Exception e) {
            return def;
        }
    }

    public static int getInt(String path, int def) {
        JsonElement el = getByPath(path);
        try {
            return el != null && !el.isJsonNull() ? el.getAsInt() : def;
        } catch (Exception e) {
            return def;
        }
    }

    public static double getDouble(String path, double def) {
        JsonElement el = getByPath(path);
        try {
            return el != null && !el.isJsonNull() ? el.getAsDouble() : def;
        } catch (Exception e) {
            return def;
        }
    }

    public static String getString(String path, String def) {
        JsonElement el = getByPath(path);
        try {
            return el != null && !el.isJsonNull() ? el.getAsString() : def;
        } catch (Exception e) {
            return def;
        }
    }

    public static JsonObject loadConfig() throws IOException {
        Path cfg = getConfigPath();
        LOGGER.info("Loading config from " + cfg);

        if (!Files.exists(cfg)) {
            LOGGER.warn("Config file does not exist: " + cfg);
            return new JsonObject();
        }

        String json = Files.readString(cfg);
        if (json == null || json.trim().isEmpty()) {
            LOGGER.warn("Config file is empty");
            return new JsonObject();
        }

        JsonObject obj = GSON.fromJson(json, JsonObject.class);
        return obj == null ? new JsonObject() : obj;
    }

    public static void saveConfig(JsonObject config) throws IOException {
        Path cfg = getConfigPath();
        LOGGER.info("Saving config to " + cfg);

        Path parent = cfg.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
            LOGGER.info("Created config directory: " + parent);
        }

        Files.writeString(cfg, GSON.toJson(config));
    }

    private static void ensureConfigExists() throws IOException {
        Path cfg = getConfigPath();
        Path parent = cfg.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
            LOGGER.info("Created config directory: " + parent);
        }

        if (!Files.exists(cfg)) {
            JsonObject defaultConfig = new JsonObject();
            Files.writeString(cfg, GSON.toJson(defaultConfig));
            LOGGER.info("Created default config file: " + cfg);
        }
    }

    private static Path getConfigPath() {
        if (CONFIG_PATH != null)
            return CONFIG_PATH;
        try {
            try {
                Path gameDir = FabricLoader.getInstance().getGameDir();
                CONFIG_PATH = gameDir.resolve("moonkitty").resolve(MOD_ID).resolve("config.json");

            } catch (Throwable t) {
                Path cwd = Path.of(System.getProperty("user.dir"));
                CONFIG_PATH = cwd.resolve("config").resolve(MOD_ID).resolve("config.json");
            }
        } catch (Exception e) {
            CONFIG_PATH = Path.of(path);
        }
        return CONFIG_PATH;
    }

}