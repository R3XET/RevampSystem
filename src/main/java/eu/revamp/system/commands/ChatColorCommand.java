package eu.revamp.system.commands;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.system.menus.color.ChatColorMenu;
import eu.revamp.system.enums.Language;
import org.bukkit.entity.Player;

public class ChatColorCommand extends BaseCommand {

    @Command(name = "cc", aliases = "chatcolor")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        if (!player.hasPermission("revampsystem.command.chatcolor")) {
            player.sendMessage(Language.CHAT_COLOR_NO_ACCESS.toString());
            return;
        }
        new ChatColorMenu().open(player);
    }
}
