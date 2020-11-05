package eu.revamp.system.commands.essentials.messages;

import eu.revamp.spigot.utils.chat.color.CCUtils;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReplyCommand extends BaseCommand {

    @Command(name = "reply", aliases = {"r"})
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (args.length == 0) {
                player.sendMessage(Language.MESSAGES_REPLY_USAGE.toString());
                return;
            }
            if (playerData.getMessageSystem().getLastMessage() == null) {
                player.sendMessage(Language.NOT_ONLINE.toString());
                return;
            }
            Player target = Bukkit.getPlayer(playerData.getMessageSystem().getLastMessage());
            if (target == null) {
                player.sendMessage(Language.NOT_ONLINE.toString());
                return;
            }
            Tasks.run(plugin, () -> player.performCommand("message " + target.getName() + " " + CCUtils.buildMessage(args, 0)));
        });
    }
}
