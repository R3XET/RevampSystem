package eu.revamp.system.commands.punishments.punish;

import eu.revamp.system.punishments.menus.CheckMenu;
import eu.revamp.system.punishments.player.PunishPlayerData;
import eu.revamp.system.utilities.chat.Color;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.system.enums.PunishmentsLanguage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class CheckCommand extends BaseCommand {

    @Command(name = "check", permission = "punishments.command.check", aliases = {"cpunishments", "checkpunishments"}) @SuppressWarnings("deprecation")
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        Tasks.runAsync(plugin, () -> {
            if (args.length == 0) {
                player.sendMessage(PunishmentsLanguage.CHECK_USAGE.toString());
                return;
            }
            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.getPunishmentPlugin().getProfileManager().correctName(args[0]));

            PunishPlayerData targetData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());

            if (targetData == null || !target.isOnline()) {
                player.sendMessage(Color.translate("&aPlease wait..."));
                plugin.getPunishmentPlugin().getProfileManager().createPlayerDate(target.getUniqueId(), target.getName());
                targetData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());

                if (!targetData.hasPlayedBefore()) {
                    player.sendMessage(PunishmentsLanguage.HAVENT_PLAYED_BEFORE.toString());
                    plugin.getPunishmentPlugin().getProfileManager().unloadData(target.getUniqueId());
                    return;
                }

                targetData.getPunishData().load();
                targetData.load();
            }
            new CheckMenu(targetData.getPunishData()).open(player);
        });
    }
}
