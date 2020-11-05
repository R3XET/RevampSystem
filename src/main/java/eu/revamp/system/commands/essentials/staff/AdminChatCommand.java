package eu.revamp.system.commands.essentials.staff;

import eu.revamp.spigot.utils.chat.color.CCUtils;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class AdminChatCommand extends BaseCommand {

    @Command(name = "adminchat", permission = "revampsystem.adminchat", aliases = {"ac"})
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (args.length == 0) {
                playerData.setAdminChat(!playerData.isAdminChat());
                player.sendMessage(playerData.isAdminChat() ? Language.ADMIN_CHAT_ENABLED.toString() : Language.ADMIN_CHAT_DISABLED.toString());
                return;
            }
            plugin.getPlayerManagement().sendAdminChatMessage(playerData, CCUtils.buildMessage(args, 0));
        });
    }
}
