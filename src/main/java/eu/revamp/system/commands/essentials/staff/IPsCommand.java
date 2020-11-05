package eu.revamp.system.commands.essentials.staff;

import eu.revamp.system.punishments.player.PunishPlayerData;
import eu.revamp.system.utilities.chat.Color;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.enums.Language;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IPsCommand extends BaseCommand {

    @Command(name = "getips", inGameOnly = false, permission = "revampsystem.command.ips", aliases = "getip") @SuppressWarnings("deprecation")
    public void onCommand(CommandArgs command) {
        if (command.getSender() instanceof Player) {
            command.getSender().sendMessage(Language.FOR_CONSOLE_ONLY.toString());
            return;
        }
        Tasks.runAsync(plugin, () -> {
            CommandSender player = command.getSender();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Language.GETIP_USAGE.toString());
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[0]));
            if (target.isOnline()) {
                PunishPlayerData targetData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());

                player.sendMessage(Color.translate("&7&m-----------------------------------------"));
                player.sendMessage(Color.translate("&e" + targetData.getPlayerName() + "'s &cIPs"));
                player.sendMessage(" ");
                player.sendMessage(Color.translate("&eLast IP&7: &c" + targetData.getAddress()));
                player.sendMessage(Color.translate("&eAll recorded IPs&7:"));
                targetData.getAddresses().forEach(address -> {
                    player.sendMessage(Color.translate("&7- &c" + address));
                });
                player.sendMessage(Color.translate("&7&m-----------------------------------------"));
            } else {
                player.sendMessage(Language.LOADING_OFFLINE_DATA.toString());

                PunishPlayerData targetData = plugin.getPunishmentPlugin().getProfileManager().loadData(target.getUniqueId());

                if (targetData == null) {
                    player.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                    return;
                }
                targetData.load();

                player.sendMessage(Color.translate("&7&m-----------------------------------------"));
                player.sendMessage(Color.translate("&e" + targetData.getPlayerName() + "'s &cIPs"));
                player.sendMessage(" ");
                player.sendMessage(Color.translate("&eLast IP&7: &c" + targetData.getAddress()));
                player.sendMessage(Color.translate("&eAll recorded IPs&7:"));
                targetData.getAddresses().forEach(address -> {
                    player.sendMessage(Color.translate("&7- &c" + address));
                });
                player.sendMessage(Color.translate("&7&m-----------------------------------------"));
                plugin.getPunishmentPlugin().getProfileManager().unloadData(target.getUniqueId());
            }
        });
    }
}
