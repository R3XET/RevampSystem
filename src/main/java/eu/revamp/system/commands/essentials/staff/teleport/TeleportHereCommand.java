package eu.revamp.system.commands.essentials.staff.teleport;

import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TeleportHereCommand extends BaseCommand {

    @Command(name = "teleporthere", permission = "revampsystem.command.teleporthere", aliases = {"tphere"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(Language.TELEPORT_HERE_USAGE.toString());
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Language.NOT_ONLINE.toString());
            return;
        }

        target.teleport(player);
        player.sendMessage(Language.TELEPORT_HERE_TO_SENDER.toString()
                .replace("%target%", target.getDisplayName()));
        //Added config option
        if (plugin.getCoreConfig().getBoolean("teleport-commands-send-teleported-message-to-target")){
            target.sendMessage(Language.TELEPORT_HERE_TO_TARGET.toString()
                    .replace("%player%", player.getDisplayName()));
        }

    }
}
