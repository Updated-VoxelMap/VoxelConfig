
# VoxelConfig

Shared config library and GUI framework for VoxelMap and related mods (e.g. DurabilityViewer). 
It provides everything needed to build complex settings screens and write `.properties` files.

Designed to be shaded directly into your mod so users never have to download it separately.

## Usage

In your mod's `build.gradle.kts`:

```kotlin
repositories {
    mavenLocal() // Or whatever repository you publish this to
}

dependencies {
    implementation("de.voxelmap:voxelconfig:1.0.0")
    // Use the Shadow Plugin to shade this directly into your final jar!
}
```

### Defining settings

```java
List<SettingsCategory> categories = List.of(
    new SettingsCategory("general", "mymod.settings.general", List.of(
        new SettingsGroup("mymod.settings.display", List.of(
            SettingsOption.toggle("showHud", "mymod.showHud", "mymod.showHud.tooltip",
                () -> config.showHud, v -> config.showHud = v),
            SettingsOption.slider("opacity", "mymod.opacity", "mymod.opacity.tooltip",
                () -> (double) config.opacity, v -> config.opacity = v.intValue(),
                0, 100, 1, v -> Component.literal(v.intValue() + "%"),
                () -> true, Component::empty, 0)
        ))
    ))
);
```

### Opening the config screen

```java
ConfigScreen screen = ConfigScreen.create(
    Component.translatable("mymod.settings.title"),
    categories,
    parentScreen,
    this::saveConfig
);
minecraft.setScreen(screen);
```

### Loading and saving config files

```java
ConfigFile configFile = new ConfigFile(gameDir.resolve("config/mymod.properties"));

// Load
configFile.load(reader -> {
    showHud = reader.getBoolean("Show HUD", true);
    opacity = reader.getInt("Opacity", 100, 0, 255);
    name = reader.getString("Name", "default");
});

// Save
configFile.save(writer -> {
    writer.put("Show HUD", showHud);
    writer.put("Opacity", opacity);
    writer.put("Name", name);
});
```

## Requirements

- Minecraft 26.3+
- Fabric Loader 0.19+
- Java 25+

## Credits

Original creator: [Brokkonaut](https://github.com/Brokkonaut)

Maintained by the [Updated-VoxelMap](https://github.com/Updated-VoxelMap) organization.

## License

MIT
