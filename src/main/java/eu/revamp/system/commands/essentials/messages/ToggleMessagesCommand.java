package eu.revamp.system.commands.essentials.messages;

import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import org.bukkit.entity.Player;

public class ToggleMessagesCommand extends BaseCommand {

    @Command(name = "togglemessages", aliases = {"tpm", "msgtoggle", "togglepm"})
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            playerData.getMessageSystem().setMessagesToggled(!playerData.getMessageSystem().isMessagesToggled());
            if (playerData.getMessageSystem().isMessagesToggled()) {
                player.sendMessage(Language.MESSAGES_TOGGLED_ON.toString());
            } else {
                player.sendMessage(Language.MESSAGES_TOGGLED_OFF.toString());
            }
        });
    }
}
