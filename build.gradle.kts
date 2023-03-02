version = "1.3.0"
group = "xyz.srnyx"

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

repositories {
    mavenCentral() // org.spigotmc:spigot-api
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // org.spigotmc:spigot-api
    maven("https://oss.sonatype.org/content/repositories/snapshots/") // org.spigotmc:spigot-api
    maven("https://jitpack.io") // xyz.srnyx:annoying-api
}

dependencies {
    compileOnly("org.spigotmc", "spigot-api", "1.13-R0.1-SNAPSHOT")
    implementation("xyz.srnyx", "annoying-api", "2.0.5")
    compileOnly("org.jetbrains:annotations:23.1.0")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    // Make 'gradle build' run 'gradle shadowJar'
    build {
        dependsOn("shadowJar")
    }

    // Remove '-all' from the JAR file name and relocate the AnnoyingAPI package
    shadowJar {
        archiveClassifier.set("")
        relocate("xyz.srnyx.annoyingapi", "xyz.srnyx.personalphantoms.annoyingapi")
        doLast {
			// Delete all folders in the build directory besides libs
			file("build").listFiles()?.forEach {
				if (it.isDirectory && it.name != "libs") it.deleteRecursively()
			}

			// Move JAR file(s) to build folder then delete libs folder
			copy {
				from("build/libs")
				into("build")
			}
			delete("build/libs")
        }
    }

    // Text encoding
    compileJava {
        options.encoding = "UTF-8"
    }

    // Replace '${name}' and '${version}'
    processResources {
        inputs.property("name", project.name)
        inputs.property("version", version)
        filesMatching("**/plugin.yml") {
            expand("name" to project.name, "version" to version)
        }
    }

    // Disable unnecessary tasks
    classes { enabled = false }
    jar { enabled = false }
    compileTestJava { enabled = false }
    processTestResources { enabled = false }
    testClasses { enabled = false }
    test { enabled = false }
    check { enabled = false }
}
