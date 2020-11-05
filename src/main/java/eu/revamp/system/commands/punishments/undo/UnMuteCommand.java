package eu.revamp.system.commands.punishments.undo;

import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.database.redis.other.bson.JsonChain;
import eu.revamp.system.database.redis.payload.action.JedisAction;
import eu.revamp.system.enums.PunishmentsLanguage;
import eu.revamp.system.punishments.player.PunishPlayerData;
import eu.revamp.system.punishments.utilities.punishments.Punishment;
import eu.revamp.system.punishments.utilities.punishments.PunishmentType;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import eu.revamp.spigot.utils.generic.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnMuteCommand extends BaseCommand {

    @Command(name = "unmute", inGameOnly = false, permission = "punishments.command.unmute") @SuppressWarnings("deprecation")
    public void onCommand(CommandArgs command) {
        CommandSender sender = command.getSender();
        String[] args = command.getArgs();

        Tasks.runAsync(plugin, () -> {
            if (args.length < 2) {
                sender.sendMessage(PunishmentsLanguage.UNMUTE_USAGE.toString());
                return;
            }

            OfflinePlayer target = Bukkit.getOfflinePlayer(plugin.getPunishmentPlugin().getProfileManager().correctName(args[0]));

            PunishPlayerData targetData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());

            if (targetData == null || !target.isOnline()) {
                plugin.getPunishmentPlugin().getProfileManager().createPlayerDate(target.getUniqueId(), target.getName());
                targetData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(target.getUniqueId());
                targetData.getPunishData().load();
            }
            if (!targetData.getPunishData().isMuted()) {
                sender.sendMessage(PunishmentsLanguage.MUTE_NOT_MUTED.toString().replace("%user%", target.getName()));
                plugin.getPunishmentPlugin().getProfileManager().unloadData(target);
                return;
            }

            StringBuilder reasonBuilder = new StringBuilder();

            for (int i = 1; i < args.length; ++i) {
                reasonBuilder.append(args[i]).append(" ");
            }
            if (reasonBuilder.length() == 0) reasonBuilder.append("Un-Muted");

            String reason = reasonBuilder.toString().trim();
            boolean silent = reason.contains("-silent") || reason.contains("-s");

            if (reason.contains("-silent")) {
                reason = reason.replace("-silent", "");
            } else if (reason.contains("-s")) {
                reason = reason.replace("-s", "");
            }

            Punishment punishment = targetData.getPunishData().getActiveMute();
            punishment.setActive(false);
            punishment.setLast(false);
            punishment.setRemovedBy(sender.getName());
            punishment.setRemovedFor(reason);
            punishment.setRemovedSilent(silent);
            punishment.setWhenRemoved(System.currentTimeMillis());

            JsonChain jsonChain = new JsonChain();
            jsonChain.addProperty("sender", sender.getName());
            jsonChain.addProperty("target", targetData.getPlayerName());
            jsonChain.addProperty("silent", punishment.isRemovedSilent());
            jsonChain.addProperty("reason", reason);
            if (sender instanceof Player) {
                Player player = (Player) sender;
                jsonChain.addProperty("senderDisplay", player.getDisplayName());

                PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
                jsonChain.addProperty("coloredName", playerData.getHighestRank().getColor() + playerData.getPlayerName());
            } else {
                jsonChain.addProperty("senderDisplay", sender.getName());
            }
            plugin.getRedisData().write(JedisAction.EXECUTE_UNMUTE, jsonChain.get());

            punishment.save(true);

            Player addedBy = Bukkit.getPlayer(punishment.getAddedBy());
            if (addedBy != null) {
                PlayerData addedByData = plugin.getPlayerManagement().getPlayerData(addedBy.getUniqueId());
                addedByData.getPunishmentsExecuted().forEach(punishHistory -> {
                    if (punishHistory.getPunishmentType() == PunishmentType.MUTE) {
                        if (punishHistory.getTarget().equals(target.getName())) {
                            if (punishHistory.getAddedAt() == punishment.getAddedAt()) {
                                punishHistory.setActive(false);
                            }
                        }
                    }
                });
            }

            plugin.getPunishmentPlugin().getProfileManager().unloadData(target);
        });
    }
}
