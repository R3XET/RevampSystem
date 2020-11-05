package eu.revamp.system.commands.economy;

import eu.revamp.spigot.utils.generic.ConversionUtils;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand extends BaseCommand {

    private final RevampSystem plugin = RevampSystem.INSTANCE;

    @Command(name = "pay", permission = "revampsystem.command.pay")
    public void onCommand(CommandArgs command) {
        String[] args = command.getArgs();
        CommandSender sender = command.getSender();
        Player player = command.getPlayer();

        if (args.length < 2){
            player.sendMessage(Language.PAY_USAGE.toString());
            return;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Language.NOT_ONLINE.toString());
            return;
        }
        if (!ConversionUtils.isDouble(args[1]) || Double.parseDouble(args[1]) < 0) {
            player.sendMessage(Language.USE_NUMBERS.toString());
            return;
        }
        double amount = Double.parseDouble(args[1]);
        if (amount <= 0){
            player.sendMessage(Language.PAY_INVALID_MONEY.toString());
            return;
        }
        PlayerData targetAccount = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData.getBalance() < amount) {
            sender.sendMessage(Language.PAY_NOT_ENOUGH_MONEY.toString().replace("%money%", String.valueOf(playerData.getBalance())));
            return;
        }

        // Added for Prison
/*
        if (plugin.isVaultEnabled()){
            plugin.getEcon().withdrawPlayer(player, amount);
            plugin.getEcon().depositPlayer(target, amount);
        } else {
            playerData.removeBalance(amount);
            targetAccount.addBalance(amount);
        }

*/

        playerData.removeBalance(amount);
        targetAccount.addBalance(amount);


        player.sendMessage(Language.PAY_PAYED.toString().replace("%player%", target.getName()).replace("%amount%", String.valueOf(amount)));
        target.sendMessage(Language.PAY_RECEIVED.toString().replace("%player%", player.getName()).replace("%amount%", String.valueOf(amount)));
    }
}
