package eu.revamp.system.commands.essentials;

import eu.revamp.spigot.utils.chat.color.CCUtils;
import eu.revamp.spigot.utils.chat.color.Replacement;
import eu.revamp.spigot.utils.date.DateUtils;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HelpopCommand extends BaseCommand {

    @Command(name = "helpop", permission = "revampsystem.command.helpop", aliases = {"request"})

    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (args.length == 0) {
                player.sendMessage(Language.HELPOP_USAGE.toString());
                return;
            }
            if (playerData.getGlobalCooldowns().hasCooldown("helpop")) {
                player.sendMessage(Language.HELPOP_COOLDOWN.toString().replace("%seconds%",
                        playerData.getGlobalCooldowns().getMiliSecondsLeft("helpop")));
                return;
            }

            player.sendMessage(Language.HELPOP_TO_PLAYER.toString());

            Replacement replacement = new Replacement(Language.HELPOP_FORMAT.toString());
            replacement.add("%player%", player.getName());
            replacement.add("%reason%", ChatColor.stripColor(CCUtils.buildMessage(args, 0)));
            replacement.add("%player_server%", plugin.getEssentialsManagement().getServerName());

            plugin.getServerManagement().getGlobalPlayers().stream().filter(online -> online.hasPermission("revampsystem.helpop.see") && online.isHelpopAlerts()).forEach(online -> online.sendMessage(replacement.toString(false)));

            try {
                playerData.getGlobalCooldowns().createCooldown("helpop", System.currentTimeMillis(), System.currentTimeMillis() - DateUtils.parseDateDiff(
                        plugin.getCoreConfig().getString("helpop-cooldown"), false));
            } catch (Exception exception) {
                try {
                    playerData.getGlobalCooldowns().createCooldown("helpop", System.currentTimeMillis(), System.currentTimeMillis() - DateUtils.parseDateDiff("1m", false));
                } catch (Exception ignored) {
                }
            }
            playerData.getGlobalCooldowns().saveCooldowns();
        });
    }
}
