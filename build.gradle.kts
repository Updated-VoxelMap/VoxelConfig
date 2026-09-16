plugins {
    id("java")
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT"
    id("maven-publish")
}

val minecraftVersion = "26.3"
val fabricVersion = "0.19.5"

group = "de.voxelmap"
version = "1.0.2"

java {
    withSourcesJar()
    withJavadocJar()
}

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
        maven {
            name = "iani"

            val releasesUrl = "https://www.iani.de/nexus/content/repositories/releases/"
            val snapshotsUrl = "https://www.iani.de/nexus/content/repositories/snapshots/"

            url = uri(
                if (version.toString().endsWith("-SNAPSHOT"))
                    snapshotsUrl
                else
                    releasesUrl
            )
        }
    }
}
