plugins {
    java
    id("xyz.srnyx.gradle-galaxy") version "c265727"
    id("com.gradleup.shadow") version "9.6.1"
    id("me.modmuss50.mod-publish-plugin") version "675051c"
    id("io.papermc.hangar-publish-plugin") version "0.1.4"
    id("xyz.jpenilla.run-paper") version "3.1.0"
}

group = "xyz.srnyx"
description = "Per-player phantom spawning/control"

galaxy {
    minecraft {
        spigotAPI("1.13")
        annoyingAPI("45ae893")

        folia = true

        dependency {
            optional {
                repositories.add(PLACEHOLDER_API)
                group = "me.clip"
                artifact = "placeholderapi"
                version = "2.12.2"

                pluginYml = "PlaceholderAPI"
                modrinth = "placeholderapi"
                hangar = "PlaceholderAPI"
            }
        }

        pluginYml {
            developerData(SRNYX)
            permissionPrefix = "pp"

            command("nophantoms") {
                aliases.add("phantoms")
                description = "Main command for Personal Phantoms"

                permission("nophantoms") {
                    default = TRUE
                }
            }

            permission("reload") {
                description = "Allows the player to use /nophantoms reload"
            }
            permission("nophantoms.others") {
                description = "Allows the player to use /nophantoms on other players"
            }
            permission("nophantoms.bypass") {
                description = "Allows the player to bypass the /nophantoms command cooldown"
            }
            permission("nophantoms.cooldown.#") {
                description = "Set a custom cooldown for the /nophantoms command, replace # with the cooldown time (seconds)"
                default = FALSE
            }
        }

        platformPublishing {
            github("srnyx/personal-phantoms")
            modrinth("lzjYdd5h")
            hangar("PersonalPhantoms")
            spigot("106381")
            curseforge("719645")

            projectData("personal-phantoms")
        }
    }
}
