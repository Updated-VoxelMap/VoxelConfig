plugins {
    id("java")
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
    id("maven-publish")
}

val minecraftVersion = "26.3-rc-2"
val fabricVersion = "0.19.5"

group = "de.tobi"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    compileOnly("net.fabricmc:fabric-loader:${fabricVersion}")
}

java.toolchain.languageVersion = JavaLanguageVersion.of(25)

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(25)
}

loom {
    // No access widener needed
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            artifactId = "voxelconfig"
            from(components["java"])
        }
    }
    repositories {
        maven("file://${System.getenv("local_maven")}")
    }
}
