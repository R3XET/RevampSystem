package eu.revamp.system.listeners;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import eu.revamp.spigot.utils.chat.color.CC;
import eu.revamp.spigot.utils.chat.color.Replacement;
import eu.revamp.spigot.utils.player.PlayerUtils;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.utilities.chat.Color;
import gg.manny.lunar.LunarClientAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatNormalListener implements Listener {

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
        if (message.startsWith(prefixAC) && message.equalsIgnoreCase(prefixAC)) {
            event.setCancelled(true);
            return;
        }
        if (message.startsWith(prefixSC) && message.equalsIgnoreCase(prefixSC)) {
            event.setCancelled(true);
            return;
        }

        if (!plugin.getCoreConfig().getBoolean("chat-format.enabled")) return;
        message = message.replace("%", "%%"); //TO FIX CHAT FORMAT BUG :/ //BECAUSE OF THAT I CAN'T USE PLACEHOLDERAPI

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

        // TO FIX CHAT BUG WITH MULTIPLE COLORS
        //String format = this.replacePlaceholders(player, replacement.toString(false)).replace("$&%%/", "&");
        String format = replacement.toString(false);
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
        } else {
            event.setFormat(format);
        }
    }

    public boolean isLunarClient(Player player) {
        return LunarClientAPI.getInstance().onClient(player);
    }

}

