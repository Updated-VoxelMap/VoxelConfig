package de.voxelmap.voxelconfig;

import java.util.List;
import net.minecraft.network.chat.Component;

/**
 * Represents a top-level settings category displayed as a tab in the config screen.
 */
public record SettingsCategory(String id, Component title, List<SettingsGroup> groups) {
    public SettingsCategory(String id, String titleKey, List<SettingsGroup> groups) {
        this(id, Component.translatable(titleKey), List.copyOf(groups));
    }
}
