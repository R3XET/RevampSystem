package eu.revamp.system.commands.essentials.staff;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.menus.settings.StaffAlertsMenu;
import org.bukkit.entity.Player;

public class StaffAlertsCommand extends BaseCommand {

    @Command(name = "staffalerts", permission = "revampsystem.command.staffalerts", aliases = {"tsm", "togglestaffmessages"})
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (playerData == null) return;

            new StaffAlertsMenu(playerData).open(player);
        });
    }
}
