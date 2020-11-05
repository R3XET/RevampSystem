package eu.revamp.system.commands.economy;

import eu.revamp.spigot.utils.generic.ConversionUtils;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EconomyCommand extends BaseCommand {

    private final RevampSystem plugin = RevampSystem.INSTANCE;

    @Command(name = "economy", permission = "revampsystem.command.economy", aliases = {"eco"},inGameOnly = false)
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            String[] args = command.getArgs();
            CommandSender sender = command.getSender();
            if (args.length == 0){
                sender.sendMessage(Language.ECONOMY_USAGE.toString());
                return;
            }
            if (args.length == 3) {

                Player target = Bukkit.getPlayer(args[1]);

                if (target == null) {
                    sender.sendMessage(Language.NOT_ONLINE.toString());
                    return;
                }
                PlayerData targetAccount = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());
                if (!ConversionUtils.isDouble(args[2]) || Double.parseDouble(args[2]) <= 0) {
                    sender.sendMessage(Language.USE_NUMBERS.toString());
                    return;
                }
                double amount = Double.parseDouble(args[2]);

                if (args[0].equalsIgnoreCase("set")) {

                    // Added for Prison

                    /*
                    if (plugin.isVaultEnabled()){
                        VaultAccount account = new VaultAccount(target, economy);
                        account.set(amount);
                    } else {
                        targetAccount.setBalance(amount);
                    }*/

                    targetAccount.setBalance(amount);

                    sender.sendMessage(Language.ECONOMY_SET.toString()
                            .replace("%player%", target.getName())
                            .replace("%amount%", String.valueOf(amount)));

                    return;

                }
                if (args[0].equalsIgnoreCase("add")) {

                    // Added for Prison
/*
                    if (plugin.isVaultEnabled()){
                        VaultAccount account = new VaultAccount(target, economy);
                        account.add(amount);
                    } else {
                        targetAccount.addBalance(amount);
                    }*/

                    targetAccount.addBalance(amount);

                    sender.sendMessage(Language.ECONOMY_ADD.toString()
                            .replace("%player%", target.getName())
                            .replace("%amount%", String.valueOf(amount)));
                    return;
                }
                if (args[0].equalsIgnoreCase("remove")) {


                    // Added for Prison
/*
                    if (plugin.isVaultEnabled()){
                        VaultAccount account = new VaultAccount(target, economy);
                        account.subtract(amount);
                    } else {
                        targetAccount.removeBalance(amount);
                    }*/

                    targetAccount.removeBalance(amount);

                    sender.sendMessage(Language.ECONOMY_REMOVE.toString()
                            .replace("%player%", target.getName())
                            .replace("%amount%", String.valueOf(amount)));
                    return;
                }
                sender.sendMessage(Language.ECONOMY_USAGE.toString());
            }
        });
    }
}
