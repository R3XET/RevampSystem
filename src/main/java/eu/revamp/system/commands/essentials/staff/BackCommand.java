package eu.revamp.system.commands.essentials.staff;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import org.bukkit.entity.Player;

public class BackCommand extends BaseCommand {

    @Command(name = "back", permission = "revampsystem.command.back", aliases = {"lastlocation", "backlocation"})
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData == null || playerData.getBackLocation() == null) {
            player.sendMessage(Language.BACK_CANT_FIND.toString());
            return;
        }
        player.teleport(playerData.getBackLocation());
        player.sendMessage(Language.BACK_TELEPORTED.toString());

        playerData.setBackLocation(null);
    }
}
