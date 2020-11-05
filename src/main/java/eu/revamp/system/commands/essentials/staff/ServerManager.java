package eu.revamp.system.commands.essentials.staff;

import eu.revamp.spigot.utils.chat.color.CCUtils;
import eu.revamp.spigot.utils.chat.color.Replacement;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.api.ServerData;
import eu.revamp.system.database.redis.other.bson.JsonChain;
import eu.revamp.system.database.redis.payload.action.JedisAction;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.system.utilities.server.TPSUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;

public class ServerManager extends BaseCommand {

    @Command(name = "servermanager", permission = "revampsystem.command.servermanager", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            CommandSender sender = command.getSender();
            String[] args = command.getArgs();

            if (args.length == 0) {
                sender.sendMessage(Language.SERVER_MANAGER_USAGE.toString());
                return;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("listservers")) {
                sender.sendMessage(Language.SERVER_MANAGER_SERVER_LIST.toString()
                        .replace("%servers%", this.getServers()));
                return;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("info")) {
                ServerData serverData = plugin.getServerManagement().getServerData(args[1]);

                if (serverData == null) {
                    sender.sendMessage(Language.SERVER_MANAGER_SERVER_DONT_EXISTS.toString()
                            .replace("%servers%", this.getServers()));
                    return;
                }
                plugin.getCoreConfig().getStringList("SERVER_MANAGER_SERVER_INFO").forEach(message -> {
                    Replacement replacement = new Replacement(message);
                    replacement.add("%name%", serverData.getServerName());
                    replacement.add("%online", serverData.getOnlinePlayers().size());
                    replacement.add("%tps%", TPSUtils.getNiceTPS(serverData.getRecentTps()[0]) + "&a, "
                            + TPSUtils.getNiceTPS(serverData.getRecentTps()[1]) + "&a, " + TPSUtils.getNiceTPS(serverData.getRecentTps()[1]));
                    replacement.add("%max%", serverData.getMaxPlayers());
                    replacement.add("%whitelist%", serverData.isWhitelisted() ? "Yes" : "No");
                    replacement.add("%maintenance%", serverData.isMaintenance() ? "Yes" : "No");

                    sender.sendMessage(replacement.toString());
                });
                return;
            }
            if (args[0].equalsIgnoreCase("runcmd")) {
                ServerData serverData = plugin.getServerManagement().getServerData(args[1]);

                if (serverData == null && !args[1].equalsIgnoreCase("all")) {
                    sender.sendMessage(Language.SERVER_MANAGER_SERVER_DONT_EXISTS.toString()
                            .replace("%servers%", this.getServers()));
                    return;
                }

                JsonChain jsonChain = new JsonChain();
                jsonChain.addProperty("sender", sender.getName());
                jsonChain.addProperty("server", args[1]);
                jsonChain.addProperty("command", CCUtils.buildMessage(args, 2));

                plugin.getRedisData().write(JedisAction.SERVER_COMMAND, jsonChain.get());

                if (args[1].equalsIgnoreCase("all")) {
                    sender.sendMessage(Language.SERVER_MANAGER_COMMAND_PERFORMED_ALL
                            .toString().replace("%command%", CCUtils.buildMessage(args, 2)));
                } else {
                    sender.sendMessage(Language.SERVER_MANAGER_COMMAND_PERFORMED_ALL.toString()
                            .replace("%server%", serverData != null ? serverData.getServerName() : args[1])
                            .replace("%command%", CCUtils.buildMessage(args, 2)));
                }
                return;
            }
            //Tasks.run(plugin, () -> sender.performCommand(command.getLabel()));;
            Tasks.run(plugin, () -> Bukkit.dispatchCommand(sender, command.getLabel()));;
        });
    }

    private String getServers() {
        return eu.revamp.spigot.utils.string.StringUtils.getStringFromList(plugin.getServerManagement().getConnectedServers().stream().map(ServerData::getServerName).collect(Collectors.toList()));
    }
}
