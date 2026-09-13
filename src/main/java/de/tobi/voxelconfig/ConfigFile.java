package de.tobi.voxelconfig;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple configuration file supporting VoxelMap-style {@code Key:Value} format.
 *
 * <p>Example usage:
 * <pre>{@code
 * ConfigFile config = new ConfigFile(gameDir.resolve("config/mymod.properties"));
 *
 * // Loading
 * config.load(reader -> {
 *     showHud = reader.getBoolean("Show HUD", true);
 *     opacity = reader.getInt("Opacity", 100, 0, 255);
 * });
 *
 * // Saving
 * config.save(writer -> {
 *     writer.put("Show HUD", showHud);
 *     writer.put("Opacity", opacity);
 * });
 * }</pre>
 */
public final class ConfigFile {
    private static final Logger LOGGER = LoggerFactory.getLogger("VoxelConfig");
    private final Path path;

    public ConfigFile(Path path) {
        this.path = path;
    }

    /**
     * Loads the config file and passes a {@link Reader} to the consumer.
     * If the file does not exist, the consumer receives an empty reader.
     */
    public void load(Consumer<Reader> consumer) {
        Map<String, String> entries = new LinkedHashMap<>();
        if (Files.exists(path)) {
            try (BufferedReader in = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                String line;
                while ((line = in.readLine()) != null) {
                    int sep = line.indexOf(':');
                    if (sep > 0) {
                        entries.put(line.substring(0, sep), line.substring(sep + 1));
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Failed to load config from {}", path, e);
            }
        }
        consumer.accept(new Reader(entries));
    }

    /**
     * Saves the config file, creating parent directories if needed.
     */
    public void save(Consumer<Writer> consumer) {
        try {
            Files.createDirectories(path.getParent());
            try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8))) {
                consumer.accept(new Writer(out));
            }
        } catch (IOException e) {
            LOGGER.error("Failed to save config to {}", path, e);
        }
    }

    /**
     * Reader for accessing parsed config values with type-safe defaults.
     */
    public static final class Reader {
        private final Map<String, String> entries;

        Reader(Map<String, String> entries) {
            this.entries = entries;
        }

        /** Returns the raw string value, or {@code defaultValue} if the key is absent. */
        public String getString(String key, String defaultValue) {
            return entries.getOrDefault(key, defaultValue);
        }

        /** Returns a boolean value, or {@code defaultValue} if the key is absent. */
        public boolean getBoolean(String key, boolean defaultValue) {
            String v = entries.get(key);
            return v != null ? Boolean.parseBoolean(v) : defaultValue;
        }

        /** Returns an int value clamped to [{@code min}, {@code max}], or {@code defaultValue} if absent. */
        public int getInt(String key, int defaultValue, int min, int max) {
            String v = entries.get(key);
            if (v == null) return defaultValue;
            try {
                return Math.clamp(Integer.parseInt(v), min, max);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }

        /** Returns a float value clamped to [{@code min}, {@code max}], or {@code defaultValue} if absent. */
        public float getFloat(String key, float defaultValue, float min, float max) {
            String v = entries.get(key);
            if (v == null) return defaultValue;
            try {
                return Math.clamp(Float.parseFloat(v), min, max);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }

        /** Returns whether a key exists in the config file. */
        public boolean has(String key) {
            return entries.containsKey(key);
        }
    }

    /**
     * Writer for outputting config key-value pairs.
     */
    public static final class Writer {
        private final PrintWriter out;

        Writer(PrintWriter out) {
            this.out = out;
        }

        /** Writes a string value. */
        public void put(String key, String value) {
            out.println(key + ":" + value);
        }

        /** Writes a boolean value. */
        public void put(String key, boolean value) {
            out.println(key + ":" + value);
        }

        /** Writes an int value. */
        public void put(String key, int value) {
            out.println(key + ":" + value);
        }

        /** Writes a float value. */
        public void put(String key, float value) {
            out.println(key + ":" + value);
        }

        /** Writes a double value. */
        public void put(String key, double value) {
            out.println(key + ":" + value);
        }

        /** Writes a blank comment/separator line. */
        public void comment(String comment) {
            out.println("# " + comment);
        }

        /** Writes a blank line. */
        public void blank() {
            out.println();
        }
    }
}
