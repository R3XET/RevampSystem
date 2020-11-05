package eu.revamp.system.commands.permission;

import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.database.redis.other.bson.JsonChain;
import eu.revamp.system.database.redis.payload.action.JedisAction;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetPermissionCommand extends BaseCommand {

    @Command(name = "setpermission", permission = "revampsystem.command.setpermission", aliases = {"setperm"}, inGameOnly = false) @SuppressWarnings("deprecation")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            CommandSender player = command.getSender();
            String[] args = command.getArgs();

            if (args.length < 3) {
                player.sendMessage(Language.SETPERMISSION_USAGE.toString());
                return;
            }
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(plugin.getPlayerManagement().getFixedName(args[0]));
            if (offlinePlayer.isOnline()) {
                Player target = offlinePlayer.getPlayer();
                PlayerData targetData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());

                this.setPermission(player, targetData, args[1], args[2].equalsIgnoreCase("true"));
                targetData.loadAttachments(target);
            } else {
                player.sendMessage(Language.LOADING_OFFLINE_DATA.toString());
                PlayerData targetData = plugin.getPlayerManagement().loadData(offlinePlayer.getUniqueId());

                if (targetData == null) {
                    player.sendMessage(Language.DOESNT_HAVE_DATA.toString());
                    return;
                }
                this.setPermission(player, targetData, args[1], args[2].equalsIgnoreCase("true"));
                plugin.getPlayerManagement().deleteData(targetData.getUniqueId());
            }
        });
    }

    private void setPermission(CommandSender sender, PlayerData targetData, String permission, boolean set) {
        if (set) {
            if (targetData.hasPermission(permission)) {
                sender.sendMessage(Language.PLAYER_ALREADY_HAVE_PERMISSION.toString()
                        .replace("%player%", targetData.getPlayerName())
                        .replace("%permission%", permission));
                return;
            }
            targetData.getPermissions().add(permission);
            sender.sendMessage(Language.PLAYER_PERMISSION_SET.toString()
                    .replace("%player%", targetData.getPlayerName())
                    .replace("%permission%", permission));
        } else {
            if (!targetData.hasPermission(permission)) {
                sender.sendMessage(Language.PLAYER_NOT_HAVE_PERMISSION.toString()
                        .replace("%player%", targetData.getPlayerName())
                        .replace("%permission%", permission));
                return;
            }
            targetData.getPermissions().remove(permission);
            sender.sendMessage(Language.PLAYER_PERMISSION_REMOVED.toString()
                    .replace("%player%", targetData.getPlayerName())
                    .replace("%permission%", permission));
        }
        targetData.saveData();
        Player target = Bukkit.getPlayer(targetData.getPlayerName());
        if (target == null) {
            plugin.getRedisData().write(JedisAction.PLAYER_PERMISSIONS_UPDATE,
                    new JsonChain().addProperty("name", targetData.getPlayerName())
                            .addProperty("permissions", eu.revamp.spigot.utils.string.StringUtils.getStringFromList(targetData.getPermissions())).get());
        }
    }
}
