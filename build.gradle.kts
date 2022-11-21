description = "NAME"
version = "0.0.1"
group = "PATH"

repositories {
//    maven("LINK")
}

dependencies {
//    compileOnly("PATH:NAME:VERSION")
}

plugins {
    java
}

tasks {
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