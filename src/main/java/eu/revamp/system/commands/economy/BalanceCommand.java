package eu.revamp.system.commands.economy;

import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BalanceCommand extends BaseCommand {

    private final RevampSystem plugin = RevampSystem.INSTANCE;

    @Command(name = "balance", permission = "revampsystem.command.balance", aliases = {"bal"}, inGameOnly = false)
    public void onCommand(CommandArgs command) {
        String[] args = command.getArgs();
        CommandSender sender = command.getSender();
        if (args.length == 0){
            if (command.isPlayer()){
                Player player = command.getPlayer();
                PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

                // Added for Prison
                /*
                if (plugin.isVaultEnabled()){

                    double balance = plugin.getEcon().getBalance(player);

                    sender.sendMessage(Language.BALANCE_MESSAGE.toString()
                            .replace("%money%", String.valueOf(balance)));
                } else {
                    sender.sendMessage(Language.BALANCE_MESSAGE.toString()
                            .replace("%money%", String.valueOf(playerData.getBalance())));
                }*/
                double balance = playerData.getBalance();

                sender.sendMessage(Language.BALANCE_MESSAGE.toString()
                        .replace("%money%", String.valueOf(balance)));

            }
            else {
                sender.sendMessage(Language.FOR_PLAYER_USE_ONLY.toString());
            }
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Language.NOT_ONLINE.toString());
            return;
        }
        PlayerData targetAccount = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());
        sender.sendMessage(Language.BALANCE_MESSAGE_OTHERS.toString()
                .replace("%player%", target.getName())
                .replace("%money%", String.valueOf(targetAccount.getBalance())));
    }
}
