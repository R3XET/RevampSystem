package eu.revamp.system.commands.permission;

import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class BlacklistedPermissionsCommand extends BaseCommand {

    @Command(name = "blacklistedpermissions", permission = "revampsystem.command.blacklistedpermissions", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            CommandSender sender = command.getSender();
            String[] args = command.getArgs();

            if (args.length != 2) {
                sender.sendMessage(Language.BLACKLISTED_PERMISSIONS_USAGE.toString());
                return;
            }
            if (args[0].equalsIgnoreCase("add")) {
                if (plugin.getCoreConfig().getStringList("BLACKLISTED_PERMISSIONS").stream().map(String::toLowerCase).collect(Collectors.toList()).contains(args[1].toLowerCase())) {
                    sender.sendMessage(Language.BLACKLISTED_PERMISSIONS_ALREADY_ADDED.toString());
                    return;
                }
                List<String> current = plugin.getCoreConfig().getStringList("BLACKLISTED_PERMISSIONS");
                current.add(args[1].toLowerCase());
                plugin.getCoreConfig().set("BLACKLISTED_PERMISSIONS", current);
                plugin.getCoreConfig().save();

                sender.sendMessage(Language.BLACKLISTED_PERMISSIONS_ADDED.toString()
                        .replace("%permission%", args[1]));

                for (Player online : PlayerUtils.getOnlinePlayers()) {
                    PlayerData playerData = plugin.getPlayerManagement().getPlayerData(online.getUniqueId());
                    if (playerData != null) {
                        playerData.loadAttachments(online);
                    }
                }
                return;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (!plugin.getCoreConfig().getStringList("BLACKLISTED_PERMISSIONS").stream().map(String::toLowerCase).collect(Collectors.toList()).contains(args[1].toLowerCase())) {
                    sender.sendMessage(Language.BLACKLISTED_PERMISSIONS_DONT_EXISTS.toString());
                    return;
                }
                List<String> current = plugin.getCoreConfig().getStringList("BLACKLISTED_PERMISSIONS");
                current.remove(args[1].toLowerCase());
                plugin.getCoreConfig().set("BLACKLISTED_PERMISSIONS", current);
                plugin.getCoreConfig().save();

                sender.sendMessage(Language.BLACKLISTED_PERMISSIONS_REMOVED.toString()
                        .replace("%permission%", args[1]));

                for (Player online : PlayerUtils.getOnlinePlayers()) {
                    PlayerData playerData = plugin.getPlayerManagement().getPlayerData(online.getUniqueId());
                    if (playerData != null) {
                        playerData.loadAttachments(online);
                    }
                }
                return;
            }
            sender.sendMessage(Language.BLACKLISTED_PERMISSIONS_USAGE.toString());
        });
    }
}
