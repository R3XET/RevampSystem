package eu.revamp.system.commands.essentials.staff;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import org.bukkit.entity.Player;

public class StaffModeCommand extends BaseCommand {

    @Command(name = "staffmode", permission = "revampsystem.command.staffmode", aliases = {"mod", "h", "staff"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData.isInStaffMode()) {
            plugin.getStaffModeManagement().disableStaffMode(player);
            player.sendMessage(Language.STAFF_MODE_DISABLED.toString());
        } else {
            plugin.getStaffModeManagement().enableStaffMode(player);
            player.sendMessage(Language.STAFF_MODE_ENABLED.toString());
        }
    }
}
