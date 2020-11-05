package eu.revamp.system.kit.argument;

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

public class KitSetIndexArgument extends CommandArgument {
    private final RevampSystem plugin;

    public KitSetIndexArgument(RevampSystem plugin) {
        super("setindex", "Sets the position of a kit for the GUI");
        this.plugin = plugin;
        this.aliases = new String[]{"setorder", "setindex", "setpos", "setposition"};
        this.permission = "manager.kit.argument." + this.getName();
    }

    @Override
    public String getUsage(String label) {
        return String.valueOf('/') + label + ' ' + this.getName() + " <kitName> <index[0 = minimum]>";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        Kit kit = this.plugin.getKitManager().getKit(args[1]);
        if (kit == null) {
            sender.sendMessage(ChatColor.RED + "Kit '" + args[1] + "' not found.");
            return true;
        }
        if (!ConversionUtils.isInteger(args[2])) {
            sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a number.");
            return true;
        }
        int newIndex = Integer.parseInt(args[2]);
        if (newIndex < 1) {
            sender.sendMessage(ChatColor.RED + "The kit index cannot be less than " + 1 + '.');
            return true;
        }
        List<Kit> kits = this.plugin.getKitManager().getKits();
        int totalKitAmount = kits.size() + 1;
        if (newIndex > totalKitAmount) {
            sender.sendMessage(ChatColor.RED + "The kit index must be a maximum of " + totalKitAmount + '.');
            return true;
        }
        int previousIndex = kits.indexOf(kit) + 1;
        if (newIndex == previousIndex) {
            sender.sendMessage(ChatColor.RED + "Index of kit " + kit.getDisplayName() + " is already " + newIndex + '.');
            return true;
        }
        kits.remove(kit);
        kits.add(--newIndex, kit);
        sender.sendMessage(ChatColor.GRAY + "Set the index of kit " + kit.getDisplayName() + " from " + previousIndex + " to " + newIndex + '.');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 2) {
            return Collections.emptyList();
        }
        List<Kit> kits = this.plugin.getKitManager().getKits();
        ArrayList<String> results = new ArrayList<>(kits.size());
        for (Kit kit : kits) {
            results.add(kit.getName());
        }
        return results;
    }
}
