package eu.revamp.system.listeners;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import eu.revamp.hcf.RevampHCF;
import eu.revamp.hcf.factions.Faction;
import eu.revamp.hcf.factions.events.FactionChatEvent;
import eu.revamp.hcf.factions.type.PlayerFaction;
import eu.revamp.hcf.playerdata.HCFPlayerData;
import eu.revamp.spigot.utils.chat.color.CC;
import eu.revamp.spigot.utils.chat.color.Replacement;
import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.ChatChannel;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.utilities.chat.Color;
import gg.manny.lunar.LunarClientAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class ChatHCFListener implements Listener {

    private final RevampSystem plugin = RevampSystem.INSTANCE;


    private String replacePlaceholders(Player player, String source) {

        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null
                && plugin.getServer().getPluginManager().getPlugin("MVdWPlaceholderAPI") != null) {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player,
                    PlaceholderAPI.replacePlaceholders(player, source.replace("&", "$&%%/")));
        }
        if (plugin.getServer().getPluginManager().getPlugin("MVdWPlaceholderAPI") != null) {
            return PlaceholderAPI.replacePlaceholders(player, source.replace("&", "$&%%/"));
        }
        if (plugin.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            return me.clip.placeholderapi.PlaceholderAPI.setPlaceholders(player, source.replace("&", "$&%%/"));
        }
        return source;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void handleChatFormat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
        String message = event.getMessage();
        String prefixAC = plugin.getCoreConfig().getString("quick-access-prefixes.admin-chat");
        String prefixSC = plugin.getCoreConfig().getString("quick-access-prefixes.staff-chat");
        if ((message.startsWith(prefixAC) && message.equalsIgnoreCase(prefixAC)) || playerData.isAdminChat()) {
            event.setCancelled(true);
            return;
        }
        if ((message.startsWith(prefixSC) && message.equalsIgnoreCase(prefixSC)) || playerData.isStaffChat()) {
            event.setCancelled(true);
            return;
        }

        if (!plugin.getCoreConfig().getBoolean("chat-format.enabled")) return;
        message = message.replace("%", "%%"); //TO FIX CHAT FORMAT BUG :/

        Replacement replacement = new Replacement(plugin.getCoreConfig().getString("chat-format.format"));
        replacement.add("%prefix%", Color.translate(playerData.getHighestRank().getPrefix()));
        replacement.add("%prefixes%", playerData.getAllPrefixes());
        replacement.add("%suffix%", Color.translate(playerData.getHighestRank().getSuffix()));
        replacement.add("%player%", playerData.getHighestRank().formatName(player));
        replacement.add("%nameColor%", playerData.getNameColor() != null && player.hasPermission("revampsystem.command.color") ? playerData.getNameColor() : playerData.getHighestRank().getColor().toString());
        replacement.add("%tag%", plugin.getTagManagement().getTagPrefix(player));
        replacement.add("%lunar%", this.isLunarClient(player) ? CC.translate("&9&l\\ud83c\\udf19 ") : "");

        if (playerData.getChatColor() != null) {
            replacement.add("%message%", playerData.getChatColor() + ChatColor.stripColor(message));
        } else if (playerData.getHighestRank().getChatColor() == null) {
            replacement.add("%message%", ChatColor.stripColor(message));
        } else {
            replacement.add("%message%", playerData.getHighestRank().getChatColor() + ChatColor.stripColor(message));
        }

        String format = this.replacePlaceholders(player, replacement.toString(false)).replace("$&%%/", "&");
        if (player.hasPermission("revampsystem.chat.colorcodes")) {
            format = format.replace(message, Color.translate(message));
        }
        if (playerData.isFrozen()) {
            event.setCancelled(true);
            String finalFormat = format;
            PlayerUtils.getOnlinePlayers().stream().filter(online -> online.hasPermission(plugin.getStaffModeManagement().getStaffPermission())).forEach(online -> {
                online.sendMessage(plugin.getCoreConfig().getString("frozen-prefix") + finalFormat);
            });
        } else if (!playerData.getMessageSystem().isGlobalChat() && !playerData.isFrozen() && !playerData.getPanicSystem().isInPanic()) {
            event.setFormat(format);
            player.sendMessage(plugin.getCoreConfig().getString("global-chat-disabled-prefix") + format);
        } else if (plugin.isRevampHCFEnabled()) {
            PlayerFaction playerFaction = RevampHCF.getInstance().getFactionManager().getPlayerFaction(player.getUniqueId());
            ChatChannel chatChannel = (playerFaction == null) ? ChatChannel.PUBLIC : playerFaction.getMember(player).getChatChannel();
            Set<Player> recipients = event.getRecipients();
            if (chatChannel == ChatChannel.FACTION || chatChannel == ChatChannel.ALLIANCE || chatChannel == ChatChannel.CAPTAIN) {
                Collection<Player> online = playerFaction.getOnlinePlayers();
                if (chatChannel == ChatChannel.ALLIANCE) {
                    Collection<PlayerFaction> allies = playerFaction.getAlliedFactions();
                    for (PlayerFaction ally : allies) {
                        online.addAll(ally.getOnlinePlayers());
                    }
                }
                recipients.retainAll(online);
                event.setFormat(chatChannel.getRawFormat(player));
                Bukkit.getPluginManager().callEvent(new FactionChatEvent(true, playerFaction, player, chatChannel, recipients, message));
                return;
            }
            event.setCancelled(true);
            Iterator<Player> iterator = event.getRecipients().iterator();
            while (iterator.hasNext()) {
                Player target = iterator.next();
                HCFPlayerData data = RevampHCF.getInstance().getHandlerManager().getHCFPlayerDataHandler().getPlayer(target.getUniqueId());
                if (data == null) {
                    iterator.remove();
                }
            }
            String fac = (playerFaction == null) ? (ChatColor.YELLOW + Faction.FACTIONLESS_PREFIX) : playerFaction.getDisplayName(player);

            for (Player recipient : event.getRecipients()) {
                recipient.sendMessage(format.replace("%fac%", fac));
            }
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            console.sendMessage(format.replace("%fac%", fac));
        }
        else {
            event.setFormat(format);
        }
    }

    public boolean isLunarClient(Player player) {
        return LunarClientAPI.getInstance().onClient(player);
    }

}
