package eu.revamp.system.commands.essentials;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.system.enums.Language;

public class TeamspeakCommand extends BaseCommand {

    @Command(name = "teamspeak", aliases = "ts")
    public void onCommand(CommandArgs command) {
        command.getPlayer().sendMessage(Language.TEAMSPEAK.toString());
    }
}
