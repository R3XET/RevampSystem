package eu.revamp.system.commands.essentials.staff;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.events.VanishUpdateEvent;
import eu.revamp.system.enums.Language;
import org.bukkit.entity.Player;

public class VanishCommand extends BaseCommand {

    @Command(name = "vanish", permission = "revampsystem.command.vanish", aliases = "v")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData.isVanished()) {
            plugin.getVanishManagement().unvanishPlayer(player);
            player.sendMessage(Language.VANISH_UN_VANISHED.toString());
        } else {
            plugin.getVanishManagement().vanishPlayer(player);
            player.sendMessage(Language.VANISH_VANISHED.toString());
        }
        plugin.getServer().getPluginManager().callEvent(new VanishUpdateEvent(player));
    }
}
