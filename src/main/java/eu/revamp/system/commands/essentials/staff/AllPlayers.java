package eu.revamp.system.commands.essentials.staff;

import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class AllPlayers extends BaseCommand {
    @Command(name = "allplayers", permission = "revampsystem.command.allplayers", aliases = {"allp"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        long count = plugin.getMongoManager().getDocumentation().countDocuments();
        player.sendMessage(Language.ALLPLAYERS.toString().replace("%players%", String.valueOf(count)));
    }
}
