package eu.revamp.system.kit.argument;

import eu.revamp.system.kit.Kit;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.utils.command.CommandArgument;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KitListArgument extends CommandArgument {
    private final RevampSystem plugin;

    public KitListArgument(RevampSystem plugin) {
        super("list", "Lists all current kits");
        this.plugin = plugin;
        this.permission = "command.kit.argument." + this.getName();
    }

    @Override
    public String getUsage(String label) {
        return '/' + label + ' ' + this.getName();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<Kit> kits = this.plugin.getKitManager().getKits();
        if (kits.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "No kits have been defined.");
            return true;
        }
        ArrayList<String> kitNames = new ArrayList<String>();
        for (Kit kit : kits) {
            String permission = kit.getPermissionNode();
            if (permission == null || sender.hasPermission(permission)) {
                kitNames.add(ChatColor.GREEN + kit.getDisplayName());
            }
        }
        String kitList2 = StringUtils.join(kitNames, ChatColor.GRAY + ", ");
        sender.sendMessage(ChatColor.GRAY + "*** Kits (" + kitNames.size() + '/' + kits.size() + ") ***");
        sender.sendMessage(ChatColor.GRAY + "[" + ChatColor.WHITE + kitList2 + ChatColor.GRAY + ']');
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return Collections.emptyList();
    }
}
