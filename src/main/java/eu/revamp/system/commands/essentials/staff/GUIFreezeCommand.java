package eu.revamp.system.commands.essentials.staff;


import eu.revamp.spigot.utils.chat.color.Replacement;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GUIFreezeCommand extends BaseCommand {

    @Command(name = "gfreeze", permission = "revampsystem.command.guifreeze", aliases = {"guifreeze", "guiss", "guiscreenshare"}, inGameOnly = false)
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        if (args.length == 0) {
            sender.sendMessage(Language.FREEZE_GUI_USAGE.toString());
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            sender.sendMessage(Language.NOT_ONLINE.toString());
            return;
        }

        PlayerData targetProfile = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());

        if (sender instanceof Player) {
            Player player = (Player) sender;

            player.sendMessage(this.getFreezeMessage(false, !targetProfile.isGuiFrozen(), player.getDisplayName(), target));
            target.sendMessage(this.getFreezeMessage(true, !targetProfile.isGuiFrozen(), player.getDisplayName(), target));
        } else {
            sender.sendMessage(this.getFreezeMessage(false, !targetProfile.isGuiFrozen(), sender.getName(), target));
            target.sendMessage(this.getFreezeMessage(true, !targetProfile.isGuiFrozen(), sender.getName(), target));
        }

        targetProfile.setGuiFrozen(!targetProfile.isGuiFrozen());

        if (!targetProfile.isGuiFrozen()) {
            target.closeInventory();
        }
    }

    private String getFreezeMessage(boolean isTarget, boolean isToFreeze, String player, Player target) {
        if (isToFreeze && isTarget) {
            Replacement replacement = new Replacement(Language.FREEZE_TARGET.toString());
            replacement.add("%player%", player).add("%target%", target.getDisplayName());
            return replacement.toString();
        }
        if (isToFreeze) {
            Replacement replacement = new Replacement(Language.FREEZE_PLAYER.toString());
            replacement.add("%player%", player).add("%target%", target.getDisplayName());
            return replacement.toString();
        }
        if (isTarget) {
            Replacement replacement = new Replacement(Language.UN_FREEZE_TARGET.toString());
            replacement.add("%player%", player).add("%target%", target.getDisplayName());
            return replacement.toString();
        }
        Replacement replacement = new Replacement(Language.UN_FREEZE_PLAYER.toString());
        replacement.add("%player%", player).add("%target%", target.getDisplayName());
        return replacement.toString();
    }
}
