package eu.revamp.system.commands.essentials.staff.teleport;

import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TeleportWorldCommand extends BaseCommand {

    @Command(name = "teleportworld", permission = "revampsystem.command.teleportworld", aliases = {"tpworld"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(Language.TELEPORT_WORLD_USAGE.toString());
            return;
        }
        World world = Bukkit.getWorld(args[0]);
        if (world == null) {
            player.sendMessage(Language.TELEPORT_WORLD_INVALID.toString());
            return;
        }
        if (world.getName().equalsIgnoreCase(player.getWorld().getName())) {
            player.sendMessage(Language.TELEPORT_WORLD_ALREADY_IN_WORLD.toString());
            return;
        }
        player.teleport(new Location(world, 0, world.getHighestBlockYAt(0, 0) + 30, 0));
        player.sendMessage(Language.TELEPORT_WORLD_SUCESS.toString()
                .replace("%world%", world.getName()));

    }
}
