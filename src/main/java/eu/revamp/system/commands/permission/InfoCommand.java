package eu.revamp.system.commands.permission;

import eu.revamp.spigot.utils.chat.color.Replacement;
import eu.revamp.system.api.player.GlobalPlayer;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class InfoCommand extends BaseCommand {

    @Command(name = "info", permission = "revampsystem.command.info", aliases = "seen") @SuppressWarnings("deprecation")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Language.INFO_USAGE.toString());
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[0]));
            if (target.isOnline()) {
                PlayerData targetData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());
                GlobalPlayer globalPlayer = plugin.getServerManagement().getGlobalPlayer(target.getName());

                plugin.getCoreConfig().getStringList("PLAYER_INFO_FORMAT").forEach(message -> {
                    Replacement replacement = new Replacement(message);
                    replacement.add("%player%", targetData.getPlayerName());
                    replacement.add("%lastIP%", targetData.getAddress());
                    replacement.add("%lastSeenAgo%", targetData.getLastSeenAgo());
                    replacement.add("%rank%", targetData.getHighestRank().getDisplayName());
                    replacement.add("%server%", globalPlayer != null ? globalPlayer.getServer() : Bukkit.getPlayer(target.getUniqueId()) != null ? plugin.getEssentialsManagement().getServerName() : "Offline [Unknown]");
                    replacement.add("%permissions%", eu.revamp.spigot.utils.string.StringUtils.getStringFromList(targetData.getPermissions()));
                    replacement.add("%firstJoined%", targetData.getFirstJoined());

                    player.sendMessage(replacement.toString());
                });
            } else {
                player.sendMessage(Language.LOADING_OFFLINE_DATA.toString());

                PlayerData targetData = plugin.getPlayerManagement().loadData(target.getUniqueId());
                GlobalPlayer globalPlayer = plugin.getServerManagement().getGlobalPlayer(target.getName());

                if (targetData == null) {
                    player.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                    return;
                }
                targetData.loadData();

                plugin.getCoreConfig().getStringList("PLAYER_INFO_FORMAT").forEach(message -> {
                    Replacement replacement = new Replacement(message);
                    replacement.add("%player%", targetData.getPlayerName());
                    replacement.add("%lastIP%", targetData.getAddress());
                    replacement.add("%lastSeenAgo%", targetData.getLastSeenAgo());
                    replacement.add("%rank%", targetData.getHighestRank().getDisplayName());
                    replacement.add("%server%", globalPlayer != null ? globalPlayer.getServer() : Bukkit.getPlayer(target.getUniqueId()) != null ? plugin.getEssentialsManagement().getServerName() : "Offline [Unknown]");
                    replacement.add("%permissions%", eu.revamp.spigot.utils.string.StringUtils.getStringFromList(targetData.getPermissions()));
                    replacement.add("%firstJoined%", targetData.getFirstJoined());

                    player.sendMessage(replacement.toString());
                });

                plugin.getPlayerManagement().deleteData(targetData.getUniqueId());
            }
        });
    }
}
