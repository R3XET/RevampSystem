package eu.revamp.system.api.player;

import eu.revamp.system.database.redis.other.bson.JsonChain;
import eu.revamp.system.database.redis.payload.action.JedisAction;
import eu.revamp.system.plugin.RevampSystem;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class GlobalPlayer {
    private final RevampSystem plugin = RevampSystem.INSTANCE;

    private UUID uniqueId;
    private String name, server, address, rankName, firstJoined, lastServer;
    private List<String> permissions = new ArrayList<>();
    private long lastSeen, lastActivity = -1L;
    private int rankWeight;
    private boolean op, vanished;
    private boolean staffChatAlerts, adminChatAlerts, helpopAlerts, reportAlerts;

    public boolean hasPermission(String value) {
        if (isOp()) return true;
        return this.permissions.stream().filter(permission -> permission.equalsIgnoreCase(value)).findFirst().orElse(null) != null;
    }

/*

    public boolean isAuthorized(String permission) {
        return isAuthorized(permission, false);
    }

    public boolean isAuthorized(String permission, boolean deep) {
        if (isOp()) {
            return true;
        }
        if (permission == null || permission.isEmpty()) {
            return true;
        }
        if (deep) {
            List<String> permissions = new ArrayList<>();
            permissions.add(permission);
            String perm = permission;
            int index;
            while ((index = perm.lastIndexOf('.')) != -1) {
                perm = perm.substring(0, index);
                permissions.add(perm + ".*");
            }
            permissions.add("*");
            return permissions.stream().anyMatch(this::isAuthorized);
        }
        return getPlayer().hasPermission(permission);
    }*/


    public void sendMessage(String message) {
        plugin.getRedisData().write(JedisAction.PLAYER_MESSAGE,
                new JsonChain().addProperty("name", this.name).addProperty("message", message).get());
    }

    public boolean isCurrentOnline() {
        return plugin.getServerManagement().getConnectedServers().stream().filter(serverData ->
                serverData.getNames().stream().map(String::toLowerCase).collect(Collectors.toList())
                        .contains(name.toLowerCase())).findFirst().orElse(null) != null;
    }
}
