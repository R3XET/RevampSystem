package eu.revamp.system.commands.essentials;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import org.bukkit.entity.Player;

public class NightCommand extends BaseCommand {

    @Command(name = "night")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            playerData.setWorldTime("NIGHT");
            player.setPlayerTime(20000L, false);

            player.sendMessage(Language.PLAYER_NIGHT_SET.toString());
        });
    }
}
