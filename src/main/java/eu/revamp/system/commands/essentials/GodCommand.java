package eu.revamp.system.commands.essentials;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import org.bukkit.entity.Player;

public class GodCommand extends BaseCommand {

    @Command(name = "god", permission = "revampsystem.command.god")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        playerData.setGodMode(!playerData.isGodMode());
        player.sendMessage(playerData.isGodMode() ? Language.GOD_MODE_ENABLED.toString() : Language.GOD_MODE_DISABLED.toString());
    }
}
