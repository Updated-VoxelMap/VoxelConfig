package de.tobi.voxelconfig;

import java.util.List;

/**
 * Interface for mods to provide their settings categories.
 *
 * <p>Example usage:
 * <pre>{@code
 * public class MyModConfig implements ConfigProvider {
 *     @Override
 *     public List<SettingsCategory> categories() {
 *         return List.of(
 *             new SettingsCategory("general", "mymod.settings.general", List.of(
 *                 new SettingsGroup("mymod.settings.display", List.of(
 *                     SettingsOption.toggle("showHud", "mymod.showHud", "mymod.showHud.tooltip",
 *                         () -> showHud, v -> showHud = v)
 *                 ))
 *             ))
 *         );
 *     }
 * }
 * }</pre>
 */
public interface ConfigProvider {
    /**
     * Returns the list of settings categories for this configuration.
     */
    List<SettingsCategory> categories();
}
