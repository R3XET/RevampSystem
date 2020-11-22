package eu.revamp.system.commands.essentials.staff;

import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.system.utilities.file.ConfigFile;
import org.bukkit.entity.Player;

public class SetWarpCommand extends BaseCommand {

    @Command(name = "setwarp", permission = "revampsystem.command.setwarp")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        if (args.length != 1) {
            player.sendMessage(Language.WARP_SET_USAGE.toString());
            return;
        }
        String warpName = args[0];
        ConfigFile configFile = plugin.getCoreConfig();
        configFile.set("warps." + warpName + ".location.x", player.getLocation().getX());
        configFile.set("warps." + warpName + ".location.y", player.getLocation().getY());
        configFile.set("warps." + warpName + ".location.z", player.getLocation().getZ());
        configFile.set("warps." + warpName + ".location.yaw", player.getLocation().getYaw());
        configFile.set("warps." + warpName + ".location.pitch", player.getLocation().getPitch());
        configFile.set("warps." + warpName + ".location.world", player.getLocation().getWorld().toString());

        configFile.save();
        player.sendMessage(Language.WARP_SET.toString());
    }
}
