package eu.revamp.system.commands.essentials.staff.teleport;

import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class TeleportAllCommand extends BaseCommand {

    @Command(name = "teleportall", permission = "revampsystem.command.teleportall", aliases = {"tpall"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        //Added config option
        if (plugin.getCoreConfig().getBoolean("teleport-commands-send-teleported-message-to-target")) {
            PlayerUtils.getOnlinePlayers().forEach(online -> {
                online.teleport(player);
                online.sendMessage(Language.TELEPORT_ALL_SUCCESS.toString().replace("%player%", player.getDisplayName()));
            });
        }
    }
}
