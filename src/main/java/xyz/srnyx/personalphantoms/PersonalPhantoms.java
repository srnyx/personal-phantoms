package xyz.srnyx.personalphantoms;

import org.jetbrains.annotations.NotNull;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.PluginPlatform;


public class PersonalPhantoms extends AnnoyingPlugin {
    @NotNull public static final String KEY = "pp_no-phantoms";

    public PersonalPhantoms() {
        options
                .pluginOptions(pluginOptions -> pluginOptions.updatePlatforms(
                        PluginPlatform.modrinth("personal-phantoms"),
                        PluginPlatform.hangar(this, "srnyx"),
                        PluginPlatform.spigot("106381")))
                .bStatsOptions(bStatsOptions -> bStatsOptions.id(18328))
                .registrationOptions
                .automaticRegistration(automaticRegistration -> automaticRegistration.packages(
                        "xyz.srnyx.personalphantoms.commands",
                        "xyz.srnyx.personalphantoms.listeners"))
                .papiExpansionToRegister(() -> new PersonalPlaceholders(this));
    }
}
