package eu.revamp.system.commands.essentials.staff;

import eu.revamp.spigot.utils.chat.color.CCUtils;
import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class MassayCommand extends BaseCommand {

    @Command(name = "massay", permission = "revampsystem.command.massay")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(Language.MASSAY_USAGE.toString());
            return;
        }

        PlayerUtils.getOnlinePlayers().forEach(online -> {
            online.chat(CCUtils.buildMessage(args, 0));
        });
        player.sendMessage(Language.MASSAY_SUCCESS.toString()
                .replace("%message%", CCUtils.buildMessage(args, 0)));
    }
}
