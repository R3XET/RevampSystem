package eu.revamp.system.commands.essentials.staff;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.enums.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HealCommand extends BaseCommand {

    @Command(name = "heal", permission = "revampsystem.command.heal")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                Tasks.run(plugin, () -> player.performCommand(command.getLabel() + " " + player.getName()));
                return;
            }
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(Language.NOT_ONLINE.toString());
                return;
            }
            target.setHealth(target.getMaxHealth());
            player.sendMessage(Language.HEAL_OTHER.toString()
                    .replace("%target%", target.getDisplayName()));
        });
    }
}
