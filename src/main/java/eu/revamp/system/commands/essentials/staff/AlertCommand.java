package eu.revamp.system.commands.essentials.staff;

import eu.revamp.spigot.utils.chat.color.CCUtils;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.chat.Color;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class AlertCommand extends BaseCommand {

    @Command(name = "alert", permission = "revampsystem.command.alert", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            CommandSender sender = command.getSender();
            String[] args = command.getArgs();

            if (args.length == 0) {
                sender.sendMessage(Language.ALERT_USAGE.toString());
                return;
            }
            plugin.getServerManagement().getGlobalPlayers().forEach(globalPlayer -> globalPlayer.sendMessage(Language.ALERT_FORMAT.toString()
                    .replace("%message%", Color.translate(CCUtils.buildMessage(args, 0)))));
            Bukkit.getConsoleSender().sendMessage(Language.ALERT_FORMAT.toString()
                    .replace("%message%", Color.translate(CCUtils.buildMessage(args, 0))));
        });
    }
}
