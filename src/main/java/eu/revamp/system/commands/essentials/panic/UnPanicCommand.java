package eu.revamp.system.commands.essentials.panic;

import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UnPanicCommand extends BaseCommand {

    @Command(name = "unpanic", permission = "revampsystem.command.unpanic")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage();
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Language.NOT_ONLINE.toString());
            return;
        }

        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());

        if (!playerData.getPanicSystem().isInPanic()) {
            player.sendMessage(Language.UNPANIC_NOT_IN_PANIC.toString()
                    .replace("%player%", target.getName()));
            return;
        }

        playerData.getPanicSystem().unPanicPlayer();
        player.sendMessage(Language.UNPANIC_UNPANICED_SENDER.toString()
                .replace("%player%", target.getName()));
        target.sendMessage(Language.UNPANIC_UNPANICED_TARGET.toString()
                .replace("%sender%", player.getName()));
    }
}
