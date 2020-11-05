package eu.revamp.system.commands.essentials.messages;

import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import org.bukkit.entity.Player;

public class ToggleSoundsCommand extends BaseCommand {

    @Command(name = "togglesounds", aliases = {"tpms", "togglepms", "sounds"})
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            playerData.getMessageSystem().setSoundsEnabled(!playerData.getMessageSystem().isSoundsEnabled());
            if (playerData.getMessageSystem().isSoundsEnabled()) {
                player.sendMessage(Language.MESSAGES_TOGGLED_ON_SOUNDS.toString());
                PlayerUtils.playSound(player, plugin.getCoreConfig().getString("private-message-sound"));
            } else {
                player.sendMessage(Language.MESSAGES_TOGGLED_OFF_SOUNDS.toString());
            }
        });
    }
}
