package de.tobi.voxelconfig;

import java.util.List;
import net.minecraft.network.chat.Component;

/**
 * A group of related settings within a category, rendered with a header.
 */
public record SettingsGroup(Component title, List<SettingsOption<?>> options) {
    public SettingsGroup(String titleKey, List<SettingsOption<?>> options) {
        this(Component.translatable(titleKey), List.copyOf(options));
    }
}
