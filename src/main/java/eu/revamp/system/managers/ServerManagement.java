package eu.revamp.system.managers;

import eu.revamp.system.plugin.RevampSystem;
import eu.revamp.system.utilities.Manager;
import lombok.Getter;
import lombok.Setter;
import eu.revamp.system.api.ServerData;
import eu.revamp.system.api.player.GlobalPlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ServerManagement extends Manager {
    private Set<ServerData> connectedServers = new HashSet<>();

    public ServerManagement(RevampSystem plugin) {
        super(plugin);
    }

    public ServerData createServerData(String name) {
        if (getServerData(name) != null) return null;
        this.connectedServers.add(new ServerData(name));
        return getServerData(name);
    }

    public ServerData getServerData(String name) {
        return this.connectedServers.stream().filter(serverData -> serverData.getServerName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<GlobalPlayer> getGlobalPlayers() {
        List<GlobalPlayer> players = new ArrayList<>();
        this.connectedServers.forEach(serverData -> players.addAll(serverData.getOnlinePlayers()));
        return players;
    }

    public int getGlobalMaxPlayers() {
        int i = 0;
        for (ServerData serverData : this.connectedServers) {
            i += serverData.getMaxPlayers();
        }
        return i;
    }
    public GlobalPlayer getGlobalPlayer(String name) {
        GlobalPlayer globalPlayerReturn = null;
        for (ServerData server : this.connectedServers) {
            for (GlobalPlayer globalPlayer : server.getOnlinePlayers()) {
                if (globalPlayer.getName().equalsIgnoreCase(name)) {
                    globalPlayerReturn = globalPlayer;
                }
            }
        }
        return globalPlayerReturn;
    }
}
