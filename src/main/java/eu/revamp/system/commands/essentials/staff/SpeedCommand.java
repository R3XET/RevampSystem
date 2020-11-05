package eu.revamp.system.commands.essentials.staff;

import eu.revamp.spigot.utils.generic.ConversionUtils;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class SpeedCommand extends BaseCommand {

    @Command(name = "speed", permission = "revampsystem.command.speed")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(Language.SPEED_USAGE.toString());
            return;
        }
        if (!args[0].equalsIgnoreCase("fly")
                && !args[0].equalsIgnoreCase("walk")) {
            player.sendMessage(Language.SPEED_USAGE.toString());
            return;
        }
        if (!ConversionUtils.isInteger(args[1])) {
            player.sendMessage(Language.USE_NUMBERS.toString());
            return;
        }

        int amount = Integer.parseInt(args[1]);

        if (amount < 1 || amount > 10) {
            player.sendMessage(Language.SPEED_LIMITED.toString());
            return;
        }

        if (args[0].equalsIgnoreCase("fly")) {
            player.setFlySpeed(amount * 0.1F);
            player.sendMessage(Language.SPEED_FLY_SET.toString()
                    .replace("%amount%", String.valueOf(amount)));
        } else if (args[0].equalsIgnoreCase("walk")) {
            player.setWalkSpeed(amount * 0.1F);
            player.sendMessage(Language.SPEED_WALK_SET.toString()
                    .replace("%amount%", String.valueOf(amount)));
        }
    }
}
