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

public class SpawnCommand extends BaseCommand {
    @Command(name = "spawn", permission = "revampsystem.command.spawn")
    public void onCommand(CommandArgs command) {
        String[] args = command.getArgs();
        Player player = command.getPlayer();
        if (args.length != 1){
            double x = plugin.getCoreConfig().getDouble("spawn.location.x");
            double y = plugin.getCoreConfig().getDouble("spawn.location.y");
            double z = plugin.getCoreConfig().getDouble("spawn.location.z");
            float yaw = plugin.getCoreConfig().getInt("spawn.location.yaw");
            float pitch = plugin.getCoreConfig().getInt("spawn.location.pitch");
            World world = Bukkit.getWorld(plugin.getCoreConfig().getString("spawn.location.world"));
            player.teleport(new Location(world, x, y, z, yaw, pitch));
            return;
        }
        if (args[0].equalsIgnoreCase("set") && player.hasPermission("revampsystem.command.spawn.set")){

            ConfigFile configFile = plugin.getCoreConfig();
            configFile.set("spawn.location.x", player.getLocation().getX());
            configFile.set("spawn.location.y", player.getLocation().getY());
            configFile.set("spawn.location.z", player.getLocation().getZ());
            configFile.set("spawn.location.yaw", player.getLocation().getYaw());
            configFile.set("spawn.location.pitch", player.getLocation().getPitch());

            configFile.save();
            
            player.sendMessage(Language.SPAWN_SET.toString());
        }
        
    }
}
