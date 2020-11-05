package eu.revamp.system.commands.essentials.staff;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import org.bukkit.entity.Player;

public class CraftCommand extends BaseCommand {

    @Command(name = "craft", permission = "revampsystem.command.craft")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();

            player.openWorkbench(player.getLocation(), true);
        });
    }
}
