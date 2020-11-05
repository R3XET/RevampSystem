package eu.revamp.system.commands.essentials.staff;

import eu.revamp.spigot.utils.generic.ConversionUtils;
import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatCommand extends BaseCommand {

    @Command(name = "chat", permission = "revampsystem.command.chat")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Language.CHAT_USAGE.toString());
                return;
            }
            if (args[0].equalsIgnoreCase("mute")) {
                if (!plugin.getChatManagement().isMuted()) {
                    plugin.getChatManagement().setMuted(true);
                    Bukkit.broadcastMessage(Language.CHAT_MUTED.toString()
                            .replace("%player%", player.getDisplayName()));
                } else {
                    player.sendMessage(Language.CHAT_ALREADY_MUTED.toString());
                }
                return;
            }
            if (args[0].equalsIgnoreCase("unmute")) {
                if (plugin.getChatManagement().isMuted()) {
                    plugin.getChatManagement().setMuted(false);
                    Bukkit.broadcastMessage(Language.CHAT_UN_MUTED.toString()
                            .replace("%player%", player.getDisplayName()));
                } else {
                    player.sendMessage(Language.CHAT_ALREADY_UN_MUTED.toString());
                }
                return;
            }
            if (args[0].equalsIgnoreCase("clear")) {
                for (int i = 0; i < 100; i++) {
                    for (Player online : PlayerUtils.getOnlinePlayers()) {
                        if (!online.hasPermission("revampsystem.chat.bypass.clear")) {
                            online.sendMessage(" ");
                        }
                    }
                }
                Bukkit.broadcastMessage(Language.CHAT_CLEAR.toString()
                        .replace("%player%", player.getDisplayName()));
                return;
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("slow")) {
                if (!ConversionUtils.isInteger(args[1])) {
                    player.sendMessage(Language.USE_NUMBERS.toString());
                    return;
                }
                plugin.getChatManagement().setDelay(Integer.parseInt(args[1]));
                Bukkit.broadcastMessage(Language.CHAT_SLOWED.toString()
                        .replace("%seconds%", args[1])
                        .replace("%player%", player.getDisplayName()));
                return;
            }
            Tasks.run(plugin, () -> player.performCommand(command.getLabel()));
        });
    }
}
