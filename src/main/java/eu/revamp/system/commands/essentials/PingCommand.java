package eu.revamp.system.commands.essentials;

import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.enums.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PingCommand extends BaseCommand {

    @Command(name = "ping", aliases = {"latency", "ms"})
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Language.PING_SELF.toString()
                        .replace("%ping%", String.valueOf(PlayerUtils.getPing(player))));
                return;
            }
            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(Language.NOT_ONLINE.toString());
                return;
            }
            player.sendMessage(Language.PING_OTHER.toString()
                    .replace("%target%", target.getDisplayName())
                    .replace("%ping%", String.valueOf(PlayerUtils.getPing(target))));
        });
    }
}
