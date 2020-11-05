package eu.revamp.system.commands.essentials.staff.teleport;

import eu.revamp.spigot.utils.generic.ConversionUtils;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportPositionCommand extends BaseCommand {

    @Command(name = "teleportposition", permission = "revampsystem.command.teleportposition", aliases = {"tppos"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 3) {
            player.sendMessage(Language.TELELPORT_TO_COORDS_USAGE.toString());
            return;
        }
        if (!ConversionUtils.isInteger(args[0])
                || !ConversionUtils.isInteger(args[1])
                || !ConversionUtils.isInteger(args[2])) {
            player.sendMessage(Language.TELEPORT_INVALID_COORD.toString());
            return;
        }

        int x = Integer.parseInt(args[0]);
        int y = Integer.parseInt(args[1]);
        int z = Integer.parseInt(args[2]);

        player.teleport(new Location(player.getWorld(), x, y, z));
        player.sendMessage(Language.TELELPORT_TO_COORDS.toString()
                .replace("%x%", String.valueOf(x))
                .replace("%y%", String.valueOf(y))
                .replace("%z%", String.valueOf(z)));
    }
}
