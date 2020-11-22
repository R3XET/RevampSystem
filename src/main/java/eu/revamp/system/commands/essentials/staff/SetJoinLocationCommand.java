package eu.revamp.system.commands.essentials.staff;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.system.utilities.file.ConfigFile;
import eu.revamp.system.enums.Language;
import org.bukkit.entity.Player;

public class SetJoinLocationCommand extends BaseCommand {

    @Command(name = "setjoinlocation", permission = "revampsystem.command.setjoinlocation")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        ConfigFile configFile = plugin.getCoreConfig();
        configFile.set("on-join.teleport.location.x", player.getLocation().getX());
        configFile.set("on-join.teleport.location.y", player.getLocation().getY());
        configFile.set("on-join.teleport.location.z", player.getLocation().getZ());
        configFile.set("on-join.teleport.location.yaw", player.getLocation().getYaw());
        configFile.set("on-join.teleport.location.pitch", player.getLocation().getPitch());
        configFile.set("on-join.teleport.location.world", player.getLocation().getWorld().toString());

        configFile.save();
        player.sendMessage(Language.JOIN_SPAWN_SET.toString());
        if (!configFile.getBoolean("on-join.teleport.enabled")) {
            player.sendMessage(Language.JOIN_SPAWN_NOTE.toString());
        }
    }
}
