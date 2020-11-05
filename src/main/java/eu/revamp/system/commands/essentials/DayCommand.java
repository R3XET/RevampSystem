package eu.revamp.system.commands.essentials;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import org.bukkit.entity.Player;

public class DayCommand extends BaseCommand {

    @Command(name = "day")
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            playerData.setWorldTime("DAY");
            player.setPlayerTime(0L, false);

            player.sendMessage(Language.PLAYER_DAY_SET.toString());
        });
    }
}
