package eu.revamp.system.commands.essentials;

import eu.revamp.spigot.utils.date.DateUtils;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

public class PlaytimeCommand extends BaseCommand {

    @Command(name = "playtime", permission = "revampsystem.command.playtime")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                long ticks = player.getStatistic(Statistic.PLAY_ONE_TICK);
                player.sendMessage(Language.PLAYTIME_SELF.toString()
                        .replace("%playtime%", DateUtils.formatTimeMillis(ticks * 50L)));
                return;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(Language.NOT_ONLINE.toString());
                return;
            }

            long ticks = target.getStatistic(Statistic.PLAY_ONE_TICK);
            player.sendMessage(Language.PLAYTIME_OTHER.toString()
                    .replace("%playtime%", DateUtils.formatTimeMillis(ticks * 50L))
                    .replace("%player%", target.getName()));
        });
    }
}
