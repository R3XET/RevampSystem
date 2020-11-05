package eu.revamp.system.commands.essentials.messages;

import eu.revamp.spigot.utils.chat.color.CCUtils;
import eu.revamp.spigot.utils.generic.Tasks;
import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.data.other.systems.MessageSystem;
import eu.revamp.system.enums.Language;
import eu.revamp.system.enums.PunishmentsLanguage;
import eu.revamp.system.punishments.player.PunishPlayerData;
import eu.revamp.system.punishments.utilities.punishments.Punishment;
import eu.revamp.system.utilities.chat.Color;
import eu.revamp.system.utilities.command.BaseCommand;
import eu.revamp.system.utilities.command.Command;
import eu.revamp.system.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessageCommand extends BaseCommand {

    @Command(name = "message", aliases = {"msg", "tell"})
    public void onCommand(CommandArgs command) {
        Tasks.runAsync(plugin, () -> {
            Player player = command.getPlayer();
            String[] args = command.getArgs();
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());

            if (args.length < 2) {
                player.sendMessage(Language.MESSAGES_USAGE.toString());
                return;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                player.sendMessage(Language.NOT_ONLINE.toString());
                return;
            }
            PlayerData targetD = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());
            if (targetD != null && targetD.isVanished() &&
                    plugin.getVanishManagement().getVanishPriority(player) < plugin.getVanishManagement().getVanishPriority(target)) {
                player.sendMessage(Language.NOT_ONLINE.toString());
                return;
            }
            PunishPlayerData punishPlayerData = plugin.getPunishmentPlugin().getProfileManager().getPlayerDataFromUUID(player.getUniqueId());
            if (punishPlayerData != null && punishPlayerData.getPunishData().isMuted()) {
                Punishment mute = punishPlayerData.getPunishData().getActiveMute();

                if (mute.isPermanent()) {
                    player.sendMessage(PunishmentsLanguage.MUTE_CANT_TALK_PERM.toString().replace("%duration%", mute.getNiceExpire()));
                } else {
                    player.sendMessage(PunishmentsLanguage.MUTE_CANT_TALK_TEMP.toString().replace("%duration%", mute.getNiceExpire()));
                }
                return;
            }
            if (!playerData.getMessageSystem().isMessagesToggled()) {
                player.sendMessage(Language.MESSAGES_HAVE_MESSAGE_TOGGLED_OFF.toString());
                return;
            }
            if (target.getName().equalsIgnoreCase(player.getName())) {
                player.sendMessage(Language.MESSAGES_CANT_SEND_YOURSELF.toString());
                return;
            }
            PlayerData targetData = plugin.getPlayerManagement().getPlayerData(target.getUniqueId());

            if (!targetData.getMessageSystem().isMessagesToggled() && !player.hasPermission("revampsystem.messages.bypass.toggled")) {
                player.sendMessage(Language.MESSAGES_HAVE_MESSAGE_TOGGLED_OFF_TARGET.toString().replace("%target%", target.getName()));
                return;
            }
            if (playerData.getMessageSystem().isIgnoring(target.getName())) {
                player.sendMessage(Language.MESSAGE_INGORING_PLAYER.toString().replace("%target%", target.getName()));
                return;
            }
            if (targetData.getMessageSystem().isIgnoring(player.getName()) && !player.hasPermission("revampsystem.messages.bypass.ignore")) {
                player.sendMessage(Language.MESSAGE_INGORING_TARGET.toString().replace("%target%", target.getName()));
                return;
            }
            if (plugin.getFilterManager().isFiltered(CCUtils.buildMessage(args, 1))) {
                if (!player.hasPermission("revampsystem.filter.bypass")) {
                    player.sendMessage(Color.translate(MessageSystem.getFormat(this.getDisplayName(playerData, true), this.getDisplayName(targetData, true), this.getDisplayName(playerData, false), this.getDisplayName(targetData, false), CCUtils.buildMessage(args, 1), true)));
                    plugin.getServerManagement().getGlobalPlayers().stream().filter(globalPlayer -> globalPlayer.hasPermission("revampsystem.filter.alerts")).forEach(globalPlayer -> globalPlayer.sendMessage(Language.FILTER_STAFF_ALERT_PRIVATE_MESASGE.toString()
                            .replace("%sender%", player.getDisplayName())
                            .replace("%target%", target.getDisplayName())
                            .replace("%sender_name%", playerData.getHighestRank().getColor() + playerData.getPlayerName())
                            .replace("%target_name%", targetData.getHighestRank().getColor() + targetData.getPlayerName())
                            .replace("%server%", plugin.getEssentialsManagement().getServerName())
                            .replace("%message%", CCUtils.buildMessage(args, 1))));
                    return;
                }
            }

            playerData.getMessageSystem().setLastMessage(target.getUniqueId());
            targetData.getMessageSystem().setLastMessage(player.getUniqueId());

            player.sendMessage(Color.translate(MessageSystem.getFormat(this.getDisplayName(playerData, true), this.getDisplayName(targetData, true), this.getDisplayName(playerData, false), this.getDisplayName(targetData, false), CCUtils.buildMessage(args, 1), true)));
            target.sendMessage(Color.translate(MessageSystem.getFormat(this.getDisplayName(playerData, true), this.getDisplayName(targetData, true), this.getDisplayName(playerData, false), this.getDisplayName(targetData, false), CCUtils.buildMessage(args, 1), false)));

            if (targetData.getMessageSystem().isSoundsEnabled()) {
                PlayerUtils.playSound(target, plugin.getCoreConfig().getString("private-message-sound"));
            }
        });
    }

    private String getDisplayName(PlayerData playerData, boolean prefix) {
        if (prefix) {
            return playerData.getHighestRank().getPrefix() + (playerData.getNameColor() != null ? playerData.getNameColor() : "") + playerData.getPlayerName();
        }
        return playerData.getHighestRank().getColor() + playerData.getPlayerName();
    }
}
