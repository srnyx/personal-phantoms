package xyz.srnyx.personalphantoms;

import xyz.srnyx.annoyingapi.AnnoyingPlugin;


public class PersonalPhantoms extends AnnoyingPlugin {
    public PersonalPhantoms() {
        super();
        options.listenersToRegister.add(new MobListener(this));
        options.commandsToRegister.add(new NoPhantomsCommand(this));
    }
}
