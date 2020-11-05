package eu.revamp.system.commands;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.system.menus.color.NameColorMenu;
import eu.revamp.system.enums.Language;
import org.bukkit.entity.Player;

public class ColorCommand extends BaseCommand {

    @Command(name = "color", aliases = "namecolor")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        if (!player.hasPermission("revampsystem.command.color")) {
            player.sendMessage(Language.NAME_COLOR_NO_ACCESS.toString());
            return;
        }
        new NameColorMenu().open(player);
    }
}
