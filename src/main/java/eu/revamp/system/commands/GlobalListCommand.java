package eu.revamp.system.commands;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import org.bukkit.entity.Player;

public class GlobalListCommand extends BaseCommand {

    @Command(name = "globallist", aliases = {"glist"}, permission = "revampsystem.command.glist")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();

            plugin.getCoreConfig().getStringList("GLOBAL_LIST.FORMAT").forEach(message -> {
                if (!message.toLowerCase().contains("%servers%")) {
                    player.sendMessage(message
                            .replace("%players%", String.valueOf(plugin.getServerManagement().getGlobalPlayers().size())));
                } else {
                    plugin.getServerManagement().getConnectedServers().forEach(serverData -> {
                        String format = plugin.getCoreConfig().getString("GLOBAL_LIST.SERVER_FORMAT");

                        player.sendMessage(format.replace("%server%", serverData.getServerName())
                                .replace("%online%", String.valueOf(serverData.getOnlinePlayers().size()))
                                .replace("%max_online%", String.valueOf(serverData.getMaxPlayers())));
                    });
                }
            });
        });
    }
}
