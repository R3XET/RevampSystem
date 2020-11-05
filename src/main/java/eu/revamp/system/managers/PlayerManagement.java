package eu.revamp.system.managers;

import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import eu.revamp.system.utilities.Manager;
import eu.revamp.system.utilities.chat.Color;
import eu.revamp.spigot.utils.generic.Tasks;
import lombok.Getter;
import lombok.Setter;
import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.api.player.PlayerData;
import eu.revamp.system.enums.Language;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class PlayerManagement extends Manager {
    private Map<UUID, PlayerData> playerData = new HashMap<>();

    public PlayerManagement(RevampSystem plugin) {
        super(plugin);
    }

    public PlayerData createPlayerData(UUID uuid, String name) {
        if (this.playerData.containsKey(uuid)) return getPlayerData(uuid);
        this.playerData.put(uuid, new PlayerData(uuid, name));
        return getPlayerData(uuid);
    }

    public PlayerData getPlayerData(UUID uuid) {
        return this.playerData.get(uuid);
    }

    public void deleteData(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) return;
        this.playerData.remove(uuid);
    }

    public PlayerData loadData(UUID uuid) {
        Document document = plugin.getMongoManager().getDocumentation().find(Filters.eq("uuid", uuid.toString())).first();

        if (document == null) {
            return null;
        }
        this.createPlayerData(uuid, document.getString("name"));
        return this.getPlayerData(uuid);
    }

    public String getFixedName(String name) {
        Document document = plugin.getMongoManager().getDocumentation().find(Filters.eq("lowerCaseName", name.toLowerCase())).first();
        if (document == null) return name;
        return document.getString("name");
    }


    public void sendStaffChatMessage(PlayerData playerData, String message) {
        plugin.getServerManagement().getGlobalPlayers().stream().filter(globalPlayer -> globalPlayer.hasPermission("revampsystem.staffchat") && globalPlayer.isStaffChatAlerts()).forEach(globalPlayer -> {
            globalPlayer.sendMessage(Language.STAFF_CHAT_FORMAT.toString()
                    .replace("%player%", playerData.getNameColor() != null ? playerData.getNameColor() + playerData.getPlayerName() : playerData.getHighestRank().getColor() + playerData.getPlayerName())
                    .replace("%prefix%", Color.translate(playerData.getHighestRank().getPrefix()))
                    .replace("%suffix%", Color.translate(playerData.getHighestRank().getSuffix()))
                    .replace("%server%", plugin.getEssentialsManagement().getServerName())
                    .replace("%message%", message));
        });
    }

    public void sendAdminChatMessage(PlayerData playerData, String message) {
        plugin.getServerManagement().getGlobalPlayers().stream().filter(globalPlayer -> globalPlayer.hasPermission("revampsystem.adminchat") && globalPlayer.isAdminChatAlerts()).forEach(globalPlayer -> {
            globalPlayer.sendMessage(Language.ADMIN_CHAT_FORMAT.toString()
                    .replace("%player%", playerData.getNameColor() != null ? playerData.getNameColor() + playerData.getPlayerName() : playerData.getHighestRank().getColor() + playerData.getPlayerName())
                    .replace("%prefix%", Color.translate(playerData.getHighestRank().getPrefix()))
                    .replace("%suffix%", Color.translate(playerData.getHighestRank().getSuffix()))
                    .replace("%server%", plugin.getEssentialsManagement().getServerName())
                    .replace("%message%", message));
        });
    }

    @SuppressWarnings("deprecation")
    public void saveData(UUID uniqueId, String value, Object key) {
        Tasks.runAsync(plugin, () -> {
            Document document = plugin.getMongoManager().getDocumentation().find(Filters.eq("uuid", uniqueId.toString())).first();

            if (document != null && document.containsKey(value)) {
                document.put(value, key);

                plugin.getMongoManager().getDocumentation().replaceOne(Filters.eq("uuid", uniqueId.toString()), document, new UpdateOptions().upsert(true));
            }
        });
    }

    public boolean hasData(UUID uuid) {
        Document document = plugin.getMongoManager().getDocumentation().find(Filters.eq("uuid", uuid.toString())).first();

        return document != null;
    }
}
