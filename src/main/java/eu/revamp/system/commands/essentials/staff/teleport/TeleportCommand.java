package eu.revamp.system.commands.essentials.staff.teleport;

import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeleportCommand extends BaseCommand {

    @Command(name = "teleport", permission = "revampsystem.command.teleport", aliases = {"tp"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(Language.TELEPORT_USAGE.toString());
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Language.NOT_ONLINE.toString());
            return;
        }

        player.teleport(target);
        player.sendMessage(Language.TELELPORT_TO_TARGET.toString()
                .replace("%target%", target.getDisplayName()));
    }
}
