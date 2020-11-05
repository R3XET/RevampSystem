package eu.revamp.system.commands;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.system.menus.settings.SettingsMenu;

public class SettingsCommand extends BaseCommand {

    @Command(name = "settings", aliases = "options")
    public void onCommand(CommandArgs command) {
        new SettingsMenu().open(command.getPlayer());
    }
}
