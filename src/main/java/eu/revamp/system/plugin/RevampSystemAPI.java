package eu.revamp.system.plugin;

import lombok.NonNull;
import eu.revamp.system.api.ServerData;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.api.rank.RankData;
import eu.revamp.system.api.rank.grant.Grant;
import eu.revamp.system.api.tags.Tag;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class RevampSystemAPI {

    public static RevampSystem plugin = RevampSystem.INSTANCE;
    public static RevampSystemAPI INSTANCE;

    public RevampSystemAPI() {
        INSTANCE = this;
    }

    public PlayerData getPlayerData(UUID uuid) {
        return plugin.getPlayerManagement().getPlayerData(uuid);
    }

    @NonNull
    public ChatColor getPlayerNameColor(UUID uuid) {
        PlayerData playerData = this.getPlayerData(uuid);
        if (playerData == null) {
            return ChatColor.WHITE;
        }
        if (playerData.getNameColor() == null) {
            return ChatColor.WHITE;
        }
        return playerData.getHighestRank().getColor();
    }

    public RankData getPlayerRank(UUID uuid) {
        if (this.getPlayerData(uuid) == null) return new RankData("Default");
        return this.getPlayerData(uuid).getHighestRank();
    }

    public List<Grant> getActiveGrants(UUID uuid) {
        return this.getPlayerData(uuid).getActiveGrants();
    }

    public List<Grant> getAllGrants(UUID uuid) {
        return this.getPlayerData(uuid).getGrants();
    }

    public Tag getTag(UUID uuid) {
        return this.getPlayerData(uuid).getTag();
    }

    public ServerData getServerData(String server) {
        return plugin.getServerManagement().getServerData(server);
    }

    public boolean hasTag(Player player, Tag tag) {
        return player.hasPermission("revampsystem.tags." + tag.getName().toLowerCase());
    }

    public static boolean isRegistered() {
        return INSTANCE != null;
    }
}
