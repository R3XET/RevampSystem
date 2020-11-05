package eu.revamp.system.commands.essentials;

import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.system.utilities.file.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WarpCommand extends BaseCommand {

    @Command(name = "warp", permission = "revampsystem.command.warp")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        ConfigFile configFile = plugin.getCoreConfig();
        if (args.length != 1) {
            final String[] warps = {null};
            if (configFile.getConfigurationSection("warps") == null){
                player.sendMessage(Language.WARP_LIST.toString().replace("%warps%", "none"));
                return;
            }
            configFile.getConfigurationSection("warps").getKeys(false).forEach(warp -> {
                warps[0] = warps[0] + warp + ", ";
            });
            player.sendMessage(Language.WARP_LIST.toString().replace("%warps%", warps[0]));
            return;
        }
        String warpName = args[0];

        if (configFile.getConfigurationSection("warps." + warpName) != null) {
            World world = Bukkit.getWorld(configFile.getString("warps." + warpName + ".location.world"));
            int x = configFile.getInt("warps." + warpName + ".location.x");
            int y = configFile.getInt("warps." + warpName + ".location.y");
            int z = configFile.getInt("warps." + warpName + ".location.z");
            float yaw = configFile.getFloat("warps." + warpName + ".location.yaw");
            float pitch = configFile.getFloat("warps." + warpName + ".location.pitch");

            Location location = new Location(world, x, y, z, yaw, pitch);
            player.teleport(location);
            player.sendMessage(Language.WARP_TELEPORTED.toString().replace("%warp%", warpName));
        } else {
            player.sendMessage(Language.WARP_DOES_NOT_EXIST.toString().replace("%warp%", warpName));
        }
    }
}

