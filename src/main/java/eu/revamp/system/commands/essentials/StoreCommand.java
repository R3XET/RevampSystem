package eu.revamp.system.commands.essentials;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.system.enums.Language;

public class StoreCommand extends BaseCommand {

    @Command(name = "store")
    public void onCommand(CommandArgs command) {
        command.getPlayer().sendMessage(Language.STORE.toString());
    }
}
