package eu.revamp.system.kit.argument;

import com.google.common.collect.ImmutableList;
import eu.revamp.spigot.utils.generic.ConversionUtils;
import eu.revamp.system.kit.Kit;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.utils.command.CommandArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KitSetMaxUsesArgument extends CommandArgument {
    private static final List<String> COMPLETIONS_THIRD = ImmutableList.of("UNLIMITED");

    private final RevampSystem plugin;

    public KitSetMaxUsesArgument(RevampSystem plugin) {
        super("setmaxuses", "Sets the maximum uses for a kit");
        this.plugin = plugin;
        this.aliases = new String[]{"setmaximumuses"};
        this.permission = "manager.kit.argument." + this.getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + this.getName() + " <kitName> <amount|unlimited>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Kit kit = this.plugin.getKitManager().getKit(args[1]);
        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "There is not a kit named " + args[1] + '.');
            return true;
        }
        int amount;
        if (args[2].equalsIgnoreCase("unlimited")) {
            amount = Integer.MAX_VALUE;
        } else {
            if (!ConversionUtils.isInteger(args[2])) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
                return true;
            }
            amount = Integer.parseInt(args[2]);
        }
        kit.setMaximumUses(amount);
        sender.sendMessage(ChatColor.GRAY + "Set maximum uses of kit " + kit.getDisplayName() + " to " + ((amount == Integer.MAX_VALUE) ? "unlimited" : amount) + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return (args.length == 3) ? KitSetMaxUsesArgument.COMPLETIONS_THIRD : Collections.emptyList();
        }
        List<Kit> kits = this.plugin.getKitManager().getKits();
        ArrayList<String> results = new ArrayList<>(kits.size());
        for (Kit kit : kits) {
            results.add(kit.getName());
        }
        return results;
    }
}
