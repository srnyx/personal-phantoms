import xyz.srnyx.gradlegalaxy.enums.Repository
import xyz.srnyx.gradlegalaxy.enums.repository
import xyz.srnyx.gradlegalaxy.utility.setupAnnoyingAPI
import xyz.srnyx.gradlegalaxy.utility.spigotAPI


plugins {
    java
    id("xyz.srnyx.gradle-galaxy") version "1.3.2"
    id("com.gradleup.shadow") version "8.3.5"
}

setupAnnoyingAPI("5.1.5", "xyz.srnyx", "2.1.0", "Plugin used for per-player phantom spawning/control")
spigotAPI("1.13")

repository(Repository.PLACEHOLDER_API)
dependencies.compileOnly("me.clip", "placeholderapi", "2.11.6")
