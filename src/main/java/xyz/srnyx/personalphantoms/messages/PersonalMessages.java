package xyz.srnyx.personalphantoms.messages;

import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Include;
import eu.okaeri.configs.annotation.IncludePosition;
import org.jetbrains.annotations.NotNull;
import xyz.srnyx.annoyingapi.AnnoyingPlugin;
import xyz.srnyx.annoyingapi.file.okaeri.SubConfig;
import xyz.srnyx.annoyingapi.message.AnnoyingMessages;
import xyz.srnyx.annoyingapi.message.json.message.JsonChatMessage;


@Include(value = AnnoyingMessages.class, position = IncludePosition.BEFORE)
public class PersonalMessages extends AnnoyingMessages {
    public PersonalMessages(@NotNull AnnoyingPlugin annoyingPlugin) {
        super(annoyingPlugin);
    }

    @Comment
    @NotNull public JsonChatMessage reload = defaultMessage("%prefix%Reloaded the plugin!@@%command%@@%command%");

    @Comment
    @NotNull public NoPhantoms nophantoms = new NoPhantoms(this);

    @Comment
    @NotNull public Get get = new Get(this);

    public static class NoPhantoms extends SubConfig<PersonalMessages, PersonalMessages> {
        public NoPhantoms(@NotNull PersonalMessages annoyingMessages) {
            super(annoyingMessages);
        }

        @Comment("Placeholders: %cooldown==time%")
        @NotNull public JsonChatMessage cooldown = getRoot().defaultMessage("%prefix%%pe%You can't use this command for another %se%%cooldown==m'm' s's'%%pe%!@@%p%%command%@@%command%");

        @Comment
        @Comment("Placeholders: %status==boolean%")
        @NotNull public JsonChatMessage self = getRoot().defaultMessage("%prefix%You have %s%%status==enabled//disabled%%p% phantoms@@%p%%command%@@%command%");

        @Comment
        @Comment("Placeholders: %status==boolean%, %target%")
        @NotNull public JsonChatMessage toggler = getRoot().defaultMessage("%prefix%You have %s%%status==enabled//disabled%%p% phantoms for %s%%target%@@%p%%command%@@%command%");

        @Comment
        @Comment("Placeholders: %toggler%, %status==boolean%")
        @NotNull public JsonChatMessage other = getRoot().defaultMessage("%prefix%%s%%toggler%%p% has %s%%status==enabled//disabled%%p% phantoms for you@@%p%%command%@@%command%");
    }

    public static class Get extends SubConfig<PersonalMessages, PersonalMessages> {
        public Get(@NotNull PersonalMessages annoyingMessages) {
            super(annoyingMessages);
        }

        @Comment("Placeholders: %phantoms==boolean%")
        @NotNull public JsonChatMessage self = getRoot().defaultMessage("%prefix%%p%You have phantoms %s%%status==enabled//disabled%@@%p%%command%@@%command%");

        @Comment
        @Comment("Placeholders: %phantoms==boolean%, %target%")
        @NotNull public JsonChatMessage other = getRoot().defaultMessage("%prefix%%s%%target%%p% has phantoms %s%%status==enabled//disabled%@@%p%%command%@@%command%");
    }
}
