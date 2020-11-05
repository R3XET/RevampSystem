package eu.revamp.system.commands.essentials.staff;

import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.menus.ReportsMenu;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class ReportsCommand extends BaseCommand {

    @Command(name = "reports", permission = "revampsystem.command.reports") @SuppressWarnings("deprecation")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Language.REPORTS_USAGE.toString());
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);
            if (target.isOnline()) {
                PlayerData targetData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());
                if (targetData.getReports().size() == 0) {
                    player.sendMessage(Language.REPORT_DOESNT_HAVE.toString().replace("%player%", targetData.getPlayerName()));
                    return;
                }
                new ReportsMenu(targetData).open(player);
            } else {
                player.sendMessage(Language.LOADING_OFFLINE_DATA.toString());

                PlayerData targetData = plugin.getPlayerManagement().loadData(target.getUniqueId());

                if (targetData == null) {
                    player.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                    return;
                }
                targetData.loadData();
                if (targetData.getReports().size() == 0) {
                    player.sendMessage(Language.REPORT_DOESNT_HAVE.toString().replace("%player%", targetData.getPlayerName()));
                    plugin.getPlayerManagement().deleteData(targetData.getUniqueId());
                    return;
                }
                new ReportsMenu(targetData).open(player);
            }
        });
    }
}
