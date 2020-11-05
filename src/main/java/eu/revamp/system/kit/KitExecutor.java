package eu.revamp.system.kit;

import eu.revamp.system.kit.argument.*;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.utils.command.ArgumentExecutor;
import eu.revamp.system.utils.command.CommandArgument;
import eu.revamp.system.utils.command.CommandWrapper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KitExecutor extends ArgumentExecutor {

    private final RevampSystem plugin;
    private final KitGuiArgument guiArgument;

    public KitExecutor(RevampSystem plugin) {
        super("kit");
        this.plugin = plugin;
        this.addArgument(new KitApplyArgument(plugin));
        this.addArgument(new KitCreateArgument(plugin));
        this.addArgument(new KitDeleteArgument(plugin));
        this.addArgument(new KitSetDescriptionArgument(plugin));
        this.addArgument(new KitDisableArgument(plugin));
        this.addArgument(guiArgument = new KitGuiArgument(plugin));
        this.addArgument(new KitListArgument(plugin));
        this.addArgument(new KitPreviewArgument(plugin));
        this.addArgument(new KitRenameArgument(plugin));
        this.addArgument(new KitSetDelayArgument(plugin));
        this.addArgument(new KitSetImageArgument(plugin));
        this.addArgument(new KitSetIndexArgument(plugin));
        this.addArgument(new KitSetItemsArgument(plugin));
        this.addArgument(new KitSetMaxUsesArgument(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only in-game player can execute this command.");
            return true;
        }

        Player player = ((Player) sender);
        if (args.length < 1) {
            if (player.isOp()) {
                CommandWrapper.printUsage(sender, label, this.arguments);
            } else {
                this.guiArgument.onCommand(sender, command, label, args);
            }
            return true;
        }

        if (player.isOp()) {
            CommandArgument argument = this.getArgument(args[0]);
            if (argument != null) {
                String permission = argument.getPermission();
                if (permission == null || sender.hasPermission(permission)) {
                    argument.onCommand(sender, command, label, args);
                    return true;
                }
            }
            CommandWrapper.printUsage(sender, label, this.arguments);
        }
        return false;
    }

}
