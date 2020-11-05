package eu.revamp.system.commands.rank;

import eu.revamp.system.utilities.chat.Color;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.menus.grant.GrantsMenu;
import eu.revamp.system.enums.Language;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantsCommand extends BaseCommand {

    @Command(name = "grants", permission = "revampsystem.command.grants", inGameOnly = false) @SuppressWarnings("deprecation")
    public void onCommand(CommandArgs command) {
        if (!(command.getSender() instanceof Player)) {
            String[] args = command.getArgs();
            CommandSender sender = command.getSender();

            if (args.length == 0) {
                sender.sendMessage(Language.GRANTS_USAGE.toString());
                return;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[0]));
            if (target.isOnline()) {
                PlayerData targetData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());

                targetData.getActiveGrants().forEach(grant -> sender.sendMessage(Color.translate(grant.getRank().getDisplayName() + " &7- &7(&bExpire&7: &3" + grant.getNiceExpire() + "&7) &7(&bBy&7: &3" + grant.getAddedBy() + "&7) &7(&bReason&7: &3" + grant.getReason() + "&7)")));
            } else {
                sender.sendMessage(Language.LOADING_OFFLINE_DATA.toString());
                PlayerData targetData = plugin.getPlayerManagement().loadData(target.getUniqueId());

                if (targetData == null) {
                    sender.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                    return;
                }
                targetData.loadData();

                targetData.getActiveGrants().forEach(grant -> sender.sendMessage(Color.translate(grant.getRank().getDisplayName() + " &7- &7(&bExpire&7: &3" + grant.getNiceExpire() + "&7) &7(&bBy&7: &3" + grant.getAddedBy() + "&7) &7(&bReason&7: &3" + grant.getReason() + "&7)")));
                plugin.getPlayerManagement().deleteData(targetData.getUniqueId());
            }
            return;
        }
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Language.GRANTS_USAGE.toString());
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[0]));
            if (target.isOnline()) {
                PlayerData targetData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());

                new GrantsMenu(targetData).open(player);
            } else {
                player.sendMessage(Language.LOADING_OFFLINE_DATA.toString());
                PlayerData targetData = plugin.getPlayerManagement().loadData(target.getUniqueId());

                if (targetData == null) {
                    player.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                    return;
                }
                targetData.loadData();

                new GrantsMenu(targetData).open(player);
            }
        });
    }
}
