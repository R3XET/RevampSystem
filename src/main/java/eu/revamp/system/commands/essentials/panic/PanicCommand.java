package eu.revamp.system.commands.essentials.panic;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import org.bukkit.entity.Player;

public class PanicCommand extends BaseCommand {

    @Command(name = "panic", permission = "revampsystem.command.panic")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

        if (playerData.getPanicSystem().isInPanic()) {
            player.sendMessage(Language.PANIC_COMMAND_ALREADY_IN_PANIC.toString()
                    .replace("%time%", playerData.getPanicSystem().getTimeExpiration()));
            return;
        }
        if (playerData.getPanicSystem().isOnCommandCooldown()) {
            player.sendMessage(Language.PANIC_COMMAND_COOLDOWN.toString()
                    .replace("%time%", playerData.getPanicSystem().getCommandExpiration()));
            return;
        }

        playerData.getPanicSystem().panicPlayer();
        plugin.getCoreConfig().getStringList("PANIC_MESSAGE").forEach(player::sendMessage);
        
        player.sendMessage(Language.PANIC_COMMAND_USED.toString()
                .replace("%time%", playerData.getPanicSystem().getTimeExpiration()));

        plugin.getServerManagement().getGlobalPlayers().stream().filter(globalPlayer -> globalPlayer.hasPermission("revampsystem.panic.alerts")).forEach(globalPlayer -> {
            globalPlayer.sendMessage(Language.PANIC_STAFF_ALERT.toString()
                    .replace("%server%", plugin.getEssentialsManagement().getServerName())
                    .replace("%name%", player.getDisplayName()));
        });
    }
}
