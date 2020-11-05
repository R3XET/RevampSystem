package eu.revamp.system.commands.essentials.gamemode;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class GamemodeSurvivalCommand extends BaseCommand {

    @Command(name = "gms", permission = "revampsystem.command.gamemode", aliases = "gm0")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.performCommand("gamemode survival");
        } else {
            player.performCommand("gamemode " + args[0] + " survival");
        }
    }
}
