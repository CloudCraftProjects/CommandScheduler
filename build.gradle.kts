plugins {
    id("java-library")
    id("maven-publish")

    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "3.0.2"
    id("com.gradleup.shadow") version "8.3.9"
}

group = "dev.booky"
version = "1.0.0-SNAPSHOT"

val plugin: Configuration by configurations.creating {
    isTransitive = false
}

repositories {
    maven("https://repo.cloudcraftmc.de/public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")

    val cloudcore = "1.1.0-SNAPSHOT"
    compileOnlyApi("dev.booky:cloudcore:$cloudcore")

    // testserver dependency plugins (maven)
    plugin("dev.booky:cloudcore:$cloudcore:all")
}

java {
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
        vendor = JvmVendorSpec.ADOPTIUM
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        artifactId = project.name.lowercase()
        from(components["java"])
    }
}

bukkit {
    main = "$group.cmdscheduler.CmdSchedulerMain"
    apiVersion = "1.21.9"
    authors = listOf("booky10")
    depend = listOf("CloudCore")
}

tasks {
    runServer {
        minecraftVersion("1.21.10")
        pluginJars.from(plugin.resolve())
    }

    withType<Jar> {
        // no spigot mappings are used, disable useless remapping step
        manifest.attributes("paperweight-mappings-namespace" to "mojang")
    }
}
