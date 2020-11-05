package eu.revamp.system.commands.essentials.staff;

import eu.revamp.spigot.utils.chat.color.CCUtils;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SudoCommand extends BaseCommand {

    @Command(name = "sudo", permission = "revampsystem.command.sudo", aliases = "sudoplayer")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();

            if (args.length == 0) {
                player.sendMessage(Language.SUDO_USAGE.toString());
                return;
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Language.NOT_ONLINE.toString());
                return;
            }

            String chat = CCUtils.buildMessage(args, 1);
            Tasks.run(plugin, () -> target.chat(chat));

            if (chat.startsWith("/")) {
                player.sendMessage(Language.SUDO_USED_COMMAND.toString()
                        .replace("%player%", target.getName())
                        .replace("%command%", chat));
            } else {
                player.sendMessage(Language.SUDO_USED_CHAT.toString()
                        .replace("%player%", target.getName())
                        .replace("%message%", chat));
            }
        });
    }
}
