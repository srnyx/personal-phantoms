package xyz.srnyx.personalphantoms.messages;

import org.jetbrains.annotations.NotNull;
import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.file.okaeri.ConfigBuilder;
import xyz.srnyx.annoyingapi.message.MessagesProvider;
import xyz.srnyx.personalphantoms.PersonalPhantoms;


public class PersonalMessagesProvider extends MessagesProvider {
    @NotNull private final PersonalPhantoms plugin;

    public PersonalMessagesProvider(@NotNull PersonalPhantoms plugin) {
        this.plugin = plugin;

        defaults
                .prefix("&5&lPHANTOMS &8&l| &d")
                .p("&d")
                .s("&5");
    }

    @Override @NotNull
    public AnnoyingPlugin getAnnoyingPlugin() {
        return plugin;
    }

    @Override
    public void mutateBuilder(@NotNull ConfigBuilder builder) {
        builder.config(new PersonalMessages(plugin));
    }

    @Override @NotNull
    public PersonalMessages get() {
        return (PersonalMessages) messages;
    }
}
