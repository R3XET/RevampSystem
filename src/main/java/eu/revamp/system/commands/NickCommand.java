package eu.revamp.system.commands;

import eu.revamp.spigot.utils.chat.color.CC;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class NickCommand extends BaseCommand {

    @Command(name = "nick", aliases = "nickname", permission = "revampsystem.command.nick")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        if (args.length == 0) {
            player.sendMessage(Language.NICK_USAGE.toString());
            return;
        }
        String nickname = args[1];
        if (nickname.equalsIgnoreCase("none")){
            player.setDisplayName(null);
            return;
        }
        if (nickname.length() < 5 || nickname.length() > 16){
            player.sendMessage(Language.NICK_LENGTH.toString());
        } else {
            // TODO CHECK IF NOT WORKS TRY REPLACE TO ACTUAL NAME
          player.setDisplayName(CC.translate(nickname));
        }
    }
}
