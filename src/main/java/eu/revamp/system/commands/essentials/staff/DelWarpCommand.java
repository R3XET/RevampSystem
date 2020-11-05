package eu.revamp.system.commands.essentials.staff;

import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.system.utilities.file.ConfigFile;
import org.bukkit.entity.Player;

public class DelWarpCommand extends BaseCommand {

    @Command(name = "delwarp", permission = "revampsystem.command.delwarp")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        if (args.length != 1) {
            player.sendMessage(Language.DELWARP_USAGE.toString());
            return;
        }
        String warpName = args[0];
        ConfigFile configFile = plugin.getCoreConfig();
        if (configFile.getConfigurationSection("warps." + warpName) != null){
            configFile.set("warps." + warpName, null);
            configFile.save();
            player.sendMessage(Language.DELWARP_DELETED.toString().replace("%warp%", warpName));
        } else {
            player.sendMessage(Language.DELWARP_NOT_FOUND.toString());
        }
    }
}
