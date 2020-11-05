package eu.revamp.system.commands.essentials.staff;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.enums.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FeedCommand extends BaseCommand {

    @Command(name = "feed", permission = "revampsystem.command.feed")
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
            target.setFoodLevel(20);
            target.setSaturation(20);
            player.sendMessage(Language.FEED_OTHER.toString()
                    .replace("%target%", target.getDisplayName()));
        });
    }
}
