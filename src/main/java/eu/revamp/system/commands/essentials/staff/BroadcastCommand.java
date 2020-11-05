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

public class BroadcastCommand extends BaseCommand {

    @Command(name = "broadcast", permission = "revampsystem.command.broadcast", inGameOnly = false, aliases = "bc")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            CommandSender sender = command.getSender();
            String[] args = command.getArgs();

            if (args.length == 0) {
                sender.sendMessage(Language.BROADCAST_USAGE.toString());
                return;
            }
            Bukkit.broadcastMessage(Language.BROADCAST_FORMAT.toString()
                    .replace("%message%", Color.translate(CCUtils.buildMessage(args, 0))));
        });
    }
}
