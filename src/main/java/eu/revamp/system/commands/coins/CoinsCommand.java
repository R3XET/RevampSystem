package eu.revamp.system.commands.coins;

import eu.revamp.spigot.utils.generic.ConversionUtils;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.menus.coins.CoinsMenu;
import eu.revamp.system.utilities.chat.Color;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CoinsCommand extends BaseCommand {

    @Command(name = "coins", permission = "revampsystem.command.coins", inGameOnly = false)
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            String[] args = command.getArgs();
            CommandSender sender = command.getSender();

            if (command.isPlayer()) {
                Player player = command.getPlayer();
                PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

                if (args.length == 0) {
                    player.sendMessage(Language.COINS_MESSAGE.toString()
                            .replace("%coins%", String.valueOf(playerData.getCoins()))
                            .replace("%amount%", String.valueOf(playerData.getPurchasableRanks().size())));
                    return;
                }
                if (args[0].equalsIgnoreCase("buy")) {
                    new CoinsMenu().open(player);
                    return;
                }
                if (!player.hasPermission("revampsystem.command.coins.admin")) {
                    player.sendMessage(Language.COINS_BUY_USAGE.toString());
                    return;
                }
            }
            if (args.length == 3) {
                if (args[0].equalsIgnoreCase("set")) {
                    Player target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        sender.sendMessage(Language.NOT_ONLINE.toString());
                        return;
                    }
                    PlayerData targetAccount = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());
                    if (!ConversionUtils.isInteger(args[2]) || Integer.parseInt(args[2]) < 0) {
                        sender.sendMessage(Language.USE_NUMBERS.toString());
                        return;
                    }

                    targetAccount.setCoins(Integer.parseInt(args[2]));

                    sender.sendMessage(Language.COINS_SET.toString()
                            .replace("%player%", target.getName())
                            .replace("%amount%", String.valueOf(targetAccount.getCoins())));
                    return;
                }
                if (args[0].equalsIgnoreCase("add")) {
                    Player target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        sender.sendMessage(Language.NOT_ONLINE.toString());
                        return;
                    }
                    PlayerData targetAccount = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());
                    if (!ConversionUtils.isInteger(args[2]) || Integer.parseInt(args[2]) <= 0) {
                        sender.sendMessage(Language.USE_NUMBERS.toString());
                        return;
                    }

                    targetAccount.setCoins(targetAccount.getCoins() + Integer.parseInt(args[2]));

                    sender.sendMessage(Language.COINS_SET.toString()
                            .replace("%player%", target.getName())
                            .replace("%amount%", String.valueOf(targetAccount.getCoins())));
                    return;
                }
                if (args[0].equalsIgnoreCase("remove")) {
                    Player target = Bukkit.getPlayer(args[1]);

                    if (target == null) {
                        sender.sendMessage(Language.NOT_ONLINE.toString());
                        return;
                    }
                    PlayerData targetAccount = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());
                    if (!ConversionUtils.isInteger(args[2]) || Integer.parseInt(args[2]) <= 0) {
                        sender.sendMessage(Language.USE_NUMBERS.toString());
                        return;
                    }
                    if (Integer.parseInt(args[2]) > targetAccount.getCoins()) {
                        sender.sendMessage(Color.translate("&cInvalid amount."));
                        return;
                    }

                    targetAccount.setCoins(targetAccount.getCoins() - Integer.parseInt(args[2]));

                    sender.sendMessage(Language.COINS_SET.toString()
                            .replace("%player%", target.getName())
                            .replace("%amount%", String.valueOf(targetAccount.getCoins())));
                    return;
                }
            }
            sender.sendMessage(Language.COINS_USAGE.toString());
        });
    }
}
