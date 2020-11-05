package eu.revamp.system.commands.essentials.staff;

import eu.revamp.spigot.utils.generic.ConversionUtils;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ExpCommand extends BaseCommand {

    @Command(name = "exp", permission = "revampsystem.command.exp", aliases = {"xp"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(Language.EXP_COMMAND_USAGE.toString());
            return;
        }
        if (args[0].equalsIgnoreCase("set")) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                player.sendMessage(Language.NOT_ONLINE.toString());
                return;
            }
            if (!ConversionUtils.isInteger(args[2])) {
                player.sendMessage(Language.MUST_BE_INTEGER.toString());
                return;
            }
            int amount = Integer.parseInt(args[2]);
            if (amount < 0) {
                player.sendMessage(Language.MUST_BE_INTEGER.toString());
                return;
            }
            player.setLevel(amount);
            if (target == player) {
                player.sendMessage(Language.EXP_SET.toString().replace("%exp%", String.valueOf(player.getLevel())));
            } else {
                player.sendMessage(Language.EXP_SET_OTHER.toString().replace("%exp%", String.valueOf(target.getLevel())).replace("%player%", target.getName()));
                target.sendMessage(Language.EXP_SET_OTHER_TARGET.toString().replace("%exp%", String.valueOf(player.getLevel())).replace("%player%", player.getName()));
            }
        }
    }
}