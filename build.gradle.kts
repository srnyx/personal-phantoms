import xyz.srnyx.gradlegalaxy.enums.Repository
import xyz.srnyx.gradlegalaxy.enums.repository
import xyz.srnyx.gradlegalaxy.utility.setupAnnoyingAPI
import xyz.srnyx.gradlegalaxy.utility.spigotAPI


plugins {
    java
    id("xyz.srnyx.gradle-galaxy") version "1.3.3"
    id("com.gradleup.shadow") version "8.3.6"
}

setupAnnoyingAPI("0d3c33c14c", "xyz.srnyx", "2.1.2", "Plugin used for per-player phantom spawning/control")
spigotAPI("1.13")

repository(Repository.PLACEHOLDER_API)
dependencies.compileOnly("me.clip", "placeholderapi", "2.11.6")
