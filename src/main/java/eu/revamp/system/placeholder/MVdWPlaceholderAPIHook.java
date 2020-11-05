package eu.revamp.system.placeholder;

import be.maximvdw.placeholderapi.PlaceholderAPI;
import be.maximvdw.placeholderapi.PlaceholderReplaceEvent;
import be.maximvdw.placeholderapi.PlaceholderReplacer;
import lombok.AllArgsConstructor;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.utilities.chat.Color;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@AllArgsConstructor
public class MVdWPlaceholderAPIHook implements PlaceholderReplacer {
    private final RevampSystem plugin;
    private final String identifier;

    @Override
    public String onPlaceholderReplace(PlaceholderReplaceEvent event) {
        if (!event.isOnline()) {
            return "";
        }
        if (event.getPlayer() == null) {
            return "";
        }
        Player player = event.getPlayer();

        if (identifier.equalsIgnoreCase("player_rank")) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (playerData == null) {
                return "Default";
            }
            return Color.translate(playerData.getHighestRank().getDisplayName());
        }
        if (identifier.equalsIgnoreCase("player_color")) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (playerData == null) {
                return "Default";
            }
            if (playerData.getNameColor() == null) {
                return ChatColor.WHITE.toString();
            }
            return playerData.getNameColor();
        }
        if (identifier.equalsIgnoreCase("player_prefix")) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (playerData == null) {
                return "Default";
            }
            return Color.translate(playerData.getHighestRank().getPrefix());
        }
        if (identifier.equalsIgnoreCase("player_suffix")) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (playerData == null) {
                return "Default";
            }
            return Color.translate(playerData.getHighestRank().getSuffix());
        }
        if (identifier.equalsIgnoreCase("player_tag")) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (playerData == null) {
                return "";
            }
            if (playerData.getTag() == null) return  "";
            return Color.translate(playerData.getTag().getPrefix());
        }
        if (identifier.equalsIgnoreCase("player_coins")) {
            PlayerData playerData = plugin.getPlayerManagement().getPlayerData(player.getUniqueId());
            if (playerData == null) {
                return "";
            }
            return String.valueOf(playerData.getCoins());
        }
        return null;
    }

    public PlaceholderReplacer register() {
        PlaceholderAPI.registerPlaceholder(plugin, this.identifier, this);
        return this;
    }
}
