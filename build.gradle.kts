description = "PersonalPhantoms"
version = "1.2.0"
group = "xyz.srnyx"

plugins {
    java
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // org.spigotmc:spigot-api
    maven("https://oss.sonatype.org/content/repositories/snapshots/") // org.spigotmc:spigot-api
    mavenCentral() // org.spigotmc:spigot-api
}

dependencies {
    compileOnly("org.spigotmc", "spigot-api", "1.13-R0.1-SNAPSHOT")
    compileOnly("org.jetbrains", "annotations", "23.0.0")
    implementation("org.apache.commons", "commons-lang3", "3.12.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    shadowJar {
        archiveClassifier.set("")
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    processResources {
        inputs.property("version", project.version)
        filesMatching("**/plugin.yml") {
            expand("version" to project.version)
        }
    }
}