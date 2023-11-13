package xyz.srnyx.personalphantoms;

import org.bukkit.configuration.ConfigurationSection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.PluginPlatform;
import xyz.srnyx.annoyingapi.file.AnnoyingResource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class PersonalPhantoms extends AnnoyingPlugin {
    @NotNull public static final String KEY = "pp_no-phantoms";

    @Nullable public Set<String> worldsBlacklist;
    public boolean treatBlacklistAsWhitelist;

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

        reload();
    }

    @Override
    public void reload() {
        final ConfigurationSection section = new AnnoyingResource(this, "config.yml").getConfigurationSection("worlds-blacklist");
        if (section == null) {
            worldsBlacklist = null;
            return;
        }
        final List<String> list = section.getStringList("list");
        treatBlacklistAsWhitelist = section.getBoolean("treat-as-whitelist");
        worldsBlacklist = list.isEmpty() && !treatBlacklistAsWhitelist ? null : new HashSet<>(list);
    }
}
